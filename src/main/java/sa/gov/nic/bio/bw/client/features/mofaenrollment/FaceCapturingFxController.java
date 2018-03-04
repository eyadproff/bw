package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bcl.utils.CancelCommand;
import sa.gov.nic.bio.biokit.ResponseProcessor;
import sa.gov.nic.bio.biokit.beans.InitializeResponse;
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.beans.StartPreviewResponse;
import sa.gov.nic.bio.biokit.exceptions.AlreadyConnectedException;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.face.FaceStopPreviewResponse;
import sa.gov.nic.bio.biokit.face.beans.CaptureFaceResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.client.core.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.utils.MofaEnrollmentErrorCodes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class FaceCapturingFxController extends WizardStepFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(FaceCapturingFxController.class.getName());
	
	@FXML private FourStateTitledPane tpCameraLivePreview;
	@FXML private ImageView ivCameraLivePreview;
	@FXML private ImageView ivCapturedImage;
	@FXML private ImageView ivCroppedImage;
	@FXML private AutoScalingStackPane subScenePane;
	@FXML private Label lblStatus;
	@FXML private ProgressIndicator piProgress;
	@FXML private ProgressIndicator piCameraLivePreview;
	@FXML private ProgressIndicator piCapturedImage;
	@FXML private ProgressIndicator piCroppedImage;
	@FXML private Button btnCancel;
	@FXML private Button btnConnectToDeviceManager;
	@FXML private Button btnDisconnectFromDeviceManager;
	@FXML private Button btnReinitializeDevice;
	@FXML private Button btnStartFaceCapturing;
	@FXML private Button btnCaptureFace;
	@FXML private Button btnStopFaceCapturing;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private String faceDeviceName;
	private boolean applyIcao = true;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/faceCapturing.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
	}
	
	@Override
	public void onControllerReady()
	{
		Group shape3D = null;
		try
		{
			shape3D = FXMLLoader.load(getClass().getResource("fxml/face3DModel.fxml"), stringsBundle);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		AutoScalingStackPane shapePane = new AutoScalingStackPane(shape3D);
		
		SubScene subScene = new SubScene(shapePane, 100.0, 100.0, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.TRANSPARENT);
		subScene.setCamera(new PerspectiveCamera());
		
		subScenePane.getChildren().setAll(subScene);
		
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm) btnConnectToDeviceManager.fire();
	}
	
	@FXML
	private void onConnectToDeviceManagerButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("connecting to BioKit...");
		
		GuiUtils.showNode(btnConnectToDeviceManager, false);
		GuiUtils.showNode(btnReinitializeDevice, false);
		GuiUtils.showNode(btnStartFaceCapturing, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(btnCancel, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.connectingToDeviceManager"));
		
		BioKitManager bioKitManager = Context.getBioKitManager();
		
		final CancelCommand cancelCommand = new CancelCommand();
		btnCancel.setOnAction(e -> cancelCommand.cancel());
		Task<Void> task = new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				boolean isListening = BclUtils.isLocalhostPortListening(bioKitManager.getWebsocketPort());
				if(cancelCommand.isCanceled()) return null;
				
				if(!isListening)
				{
					LOGGER.info("Bio-Kit is not running! Launching via BCL...");
					int checkEverySeconds = 1000; // TODO: make it configurable
					BclUtils.launchAppByBCL(Context.getServerUrl(), bioKitManager.getBclId(),
					                        bioKitManager.getWebsocketPort(), checkEverySeconds, cancelCommand);
				}
				
				if(cancelCommand.isCanceled()) return null;
				bioKitManager.connect();
				return null;
			}
		};
		task.setOnSucceeded(e ->
		{
		    if(cancelCommand.isCanceled())
		    {
		        GuiUtils.showNode(piProgress, false);
		        GuiUtils.showNode(btnCancel, false);
		        GuiUtils.showNode(btnConnectToDeviceManager, true);
		        lblStatus.setText(stringsBundle.getString("label.status.connectingToDeviceManagerCancelled"));
		        return;
		    };
		
		    LOGGER.info("successfully connected to BioKit");
		
		    // if the root pane is still on the scene
		    if(coreFxController.getBodyPane().getChildren().contains(rootPane))
		    {
		        btnReinitializeDevice.fire();
		    }
		});
		task.setOnFailed(e ->
		{
		    lblStatus.setText(stringsBundle.getString("label.status.connectingToDeviceManagerFailed"));
		    Throwable exception = task.getException();
		
		    if(exception instanceof AlreadyConnectedException) btnReinitializeDevice.fire();
		    else
		    {
		        GuiUtils.showNode(piProgress, false);
		        GuiUtils.showNode(btnCancel, false);
		        GuiUtils.showNode(btnConnectToDeviceManager, true);
		
		        String errorCode = MofaEnrollmentErrorCodes.C007_00001.getCode();
		        String[] errorDetails = {"failed to connect to BioKit!"};
		        coreFxController.showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onDisconnectFromDeviceManagerButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("disconnecting from BioKit...");
		
		GuiUtils.showNode(btnDisconnectFromDeviceManager, false);
		GuiUtils.showNode(btnConnectToDeviceManager, false);
		GuiUtils.showNode(btnReinitializeDevice, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(btnCancel, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.disconnectingFromDeviceManager"));
		
		BioKitManager bioKitManager = Context.getBioKitManager();
		
		final CancelCommand cancelCommand = new CancelCommand();
		btnCancel.setOnAction(e ->
		{
		    cancelCommand.cancel();
		});
		Task<Void> task = new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				bioKitManager.disconnect();
				return null;
			}
		};
		task.setOnSucceeded(e ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnCancel, false);
		
		    if(cancelCommand.isCanceled())
		    {
		        GuiUtils.showNode(btnConnectToDeviceManager, true);
		        lblStatus.setText(stringsBundle.getString("label.status.disconnectingToDeviceManagerCancelled"));
		        return;
		    }
		
		    GuiUtils.showNode(btnConnectToDeviceManager, true);
		    lblStatus.setText(stringsBundle.getString("label.status.successfullyDisconnectedFromDeviceManager"));
		    LOGGER.info("successfully disconnected from BioKit");
		});
		task.setOnFailed(e ->
		{
		    Throwable exception = task.getException();
		
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnCancel, false);
		    GuiUtils.showNode(btnConnectToDeviceManager, true);
		
		    String errorCode = MofaEnrollmentErrorCodes.C007_00004.getCode();
		    String[] errorDetails = {"failed to disconnect from BioKit!"};
		    coreFxController.showErrorDialog(errorCode, exception, errorDetails);
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onReinitializeDeviceButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("initializing face device...");
		
		GuiUtils.showNode(btnDisconnectFromDeviceManager, false);
		GuiUtils.showNode(btnReinitializeDevice, false);
		GuiUtils.showNode(btnStartFaceCapturing, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(btnCancel, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.initializingDevice"));
		
		Future<ServiceResponse<InitializeResponse>> future = Context.getBioKitManager().getFaceService().initialize();
		
		btnCancel.setOnAction(e ->
		{
		    future.cancel(true);
		});
		
		Task<ServiceResponse<InitializeResponse>> task = new Task<ServiceResponse<InitializeResponse>>()
		{
			@Override
			protected ServiceResponse<InitializeResponse> call() throws Exception
			{
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnCancel, false);
		
		    ServiceResponse<InitializeResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
		        InitializeResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == InitializeResponse.SuccessCodes.SUCCESS)
		        {
		            LOGGER.info("initialized face device successfully!");
		            GuiUtils.showNode(btnStartFaceCapturing, true);
		            faceDeviceName = result.getCurrentDeviceName();
		            lblStatus.setText(stringsBundle.getString("label.status.DeviceInitializedSuccessfully"));
		
		            //renameCaptureFingerprintsButton(position, false);
		        }
		        else if(result.getReturnCode() == InitializeResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
		        {
		            GuiUtils.showNode(btnDisconnectFromDeviceManager, true);
		            GuiUtils.showNode(btnReinitializeDevice, true);
		            lblStatus.setText(stringsBundle.getString("label.status.DeviceIsUnplugged"));
		        }
		        else
		        {
		            GuiUtils.showNode(btnDisconnectFromDeviceManager, true);
		            GuiUtils.showNode(btnReinitializeDevice, true);
		            String status = String.format(stringsBundle.getString(
		                    "label.status.DeviceFailedToInitialize"), result.getReturnCode());
		            lblStatus.setText(status);
		        }
		    }
		    else
		    {
		        GuiUtils.showNode(btnReinitializeDevice, true);
		
		        String errorCode = MofaEnrollmentErrorCodes.C007_00002.getCode();
		        String[] errorDetails = {"failed to to receive a response when initializing the fingerprint device!",
		                "service errorCode = " + serviceResponse.getErrorCode()};
		        coreFxController.showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnCancel, false);
		    GuiUtils.showNode(btnDisconnectFromDeviceManager, true);
		    GuiUtils.showNode(btnReinitializeDevice, true);
		
		    Throwable exception = task.getException();
		
		    if(exception instanceof CancellationException)
		    {
		        lblStatus.setText(stringsBundle.getString("label.status.initializingDeviceCancelled"));
		    }
		    else
		    {
		        String errorCode = MofaEnrollmentErrorCodes.C007_00003.getCode();
		        String[] errorDetails = {"failed to initialize the fingerprint device!"};
		        coreFxController.showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onStartFaceCapturingButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("start capturing the face...");
		
		GuiUtils.showNode(btnStartFaceCapturing, false);
		GuiUtils.showNode(btnCaptureFace, true);
		GuiUtils.showNode(btnStopFaceCapturing, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.waitingDeviceResponse"));
		boolean[] first = {true};
		
		Task<ServiceResponse<Void>> task = new Task<ServiceResponse<Void>>()
		{
			@Override
			protected ServiceResponse<Void> call() throws Exception
			{
				ResponseProcessor<StartPreviewResponse> responseProcessor = response -> Platform.runLater(() ->
				{
				    if(first[0])
				    {
				        first[0] = false;
				        lblStatus.setText(stringsBundle.getString("label.status.cameraLivePreviewing"));
				        GuiUtils.showNode(piCameraLivePreview, false);
				        tpCameraLivePreview.setActive(true);
				    }
				
				    String previewImageBase64 = response.getPreviewImage();
				    byte[] bytes = Base64.getDecoder().decode(previewImageBase64);
				    ivCameraLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				Future<ServiceResponse<Void>> future = Context.getBioKitManager().getFaceService()
															  .startPreview(faceDeviceName, responseProcessor);
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piCameraLivePreview, false);
		    ServiceResponse<Void> serviceResponse = task.getValue();
			
		    if(!serviceResponse.isSuccess())
		    {
			    lblStatus.setText(String.format(
					    stringsBundle.getString("label.status.failedToStartCameraLivePreviewingWithErrorCode"),
					    serviceResponse.getErrorCode()));
			
			    String errorCode = MofaEnrollmentErrorCodes.C007_00001.getCode();
			    String[] errorDetails = {"failed while starting the camera live preview!"};
			    coreFxController.showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
		    GuiUtils.showNode(piCameraLivePreview, false);
			
			Throwable exception = task.getException();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnDisconnectFromDeviceManager, true);
				GuiUtils.showNode(btnReinitializeDevice, true);
				GuiUtils.showNode(btnStartFaceCapturing, true);
				lblStatus.setText(stringsBundle.getString("label.status.deviceManagerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				GuiUtils.showNode(btnConnectToDeviceManager, true);
				lblStatus.setText(stringsBundle.getString("label.status.disconnectedFromDeviceManager"));
			}
			else
			{
				lblStatus.setText(stringsBundle.getString("label.status.failedToStartCameraLivePreviewing"));
				
				String errorCode = MofaEnrollmentErrorCodes.C007_00001.getCode();
				String[] errorDetails = {"failed while starting the camera live preview!"};
				coreFxController.showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onCaptureFaceButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("capture the face...");
		
		GuiUtils.showNode(btnCaptureFace, false);
		GuiUtils.showNode(btnStopFaceCapturing, false);
		GuiUtils.showNode(btnCancel, true);
		GuiUtils.showNode(piCapturedImage, true);
		GuiUtils.showNode(piCroppedImage, true);
		GuiUtils.showNode(btnCancel, true);
		
		ivCapturedImage.setImage(null);
		ivCroppedImage.setImage(null);
		
		lblStatus.setText(stringsBundle.getString("label.status.capturingFace"));
		
		Future<ServiceResponse<CaptureFaceResponse>> future = Context.getBioKitManager().getFaceService()
																	 .captureFace(faceDeviceName, true);
		
		btnCancel.setOnAction(e ->
		{
		    future.cancel(true);
		});
		
		Task<ServiceResponse<CaptureFaceResponse>> task = new Task<ServiceResponse<CaptureFaceResponse>>()
		{
			@Override
			protected ServiceResponse<CaptureFaceResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piCapturedImage, false);
			GuiUtils.showNode(piCroppedImage, false);
			GuiUtils.showNode(btnCancel, false);
			
		    ServiceResponse<CaptureFaceResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
			    CaptureFaceResponse result = serviceResponse.getResult();
			    
			    if(result.getReturnCode() == CaptureFaceResponse.SuccessCodes.SUCCESS)
			    {
				    String capturedImage = result.getCapturedImage();
				    String croppedImage = result.getCroppedImage();
				    
				    if(capturedImage != null)
				    {
					    byte[] bytes = Base64.getDecoder().decode(capturedImage);
					    ivCapturedImage.setImage(new Image(new ByteArrayInputStream(bytes)));
				    }
				    if(croppedImage != null)
				    {
					    byte[] bytes = Base64.getDecoder().decode(croppedImage);
					    ivCroppedImage.setImage(new Image(new ByteArrayInputStream(bytes)));
				    }
				    
				    // TODO
				    if(applyIcao)
				    {
					   
				    }
				    else
				    {
					   
				    }
			    }
			    else
			    {
				    GuiUtils.showNode(btnCancel, false);
			    	
				    lblStatus.setText(String.format(
						    stringsBundle.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
						    result.getReturnCode()));
			    }
		    }
		    else
		    {
			    lblStatus.setText(String.format(
					    stringsBundle.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
					    serviceResponse.getErrorCode()));
			
			    String errorCode = MofaEnrollmentErrorCodes.C007_00001.getCode();
			    String[] errorDetails = {"failed while capturing the face!"};
			    coreFxController.showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piCapturedImage, false);
			GuiUtils.showNode(piCroppedImage, false);
			GuiUtils.showNode(btnCancel, false);
			
			Throwable exception = task.getException();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnDisconnectFromDeviceManager, true);
				GuiUtils.showNode(btnReinitializeDevice, true);
				GuiUtils.showNode(btnStartFaceCapturing, true);
				lblStatus.setText(stringsBundle.getString("label.status.deviceManagerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				GuiUtils.showNode(btnConnectToDeviceManager, true);
				lblStatus.setText(stringsBundle.getString("label.status.disconnectedFromDeviceManager"));
			}
			else
			{
				lblStatus.setText(stringsBundle.getString("label.status.failedToStopCameraLivePreviewing"));
				
				String errorCode = MofaEnrollmentErrorCodes.C007_00001.getCode();
				String[] errorDetails = {"failed while capturing the face!"};
				coreFxController.showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onStopFaceCapturingButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("stop capturing the face...");
		
		GuiUtils.showNode(btnCaptureFace, false);
		GuiUtils.showNode(btnStopFaceCapturing, false);
		GuiUtils.showNode(btnCancel, true);
		
		ivCameraLivePreview.setImage(null);
		
		lblStatus.setText(stringsBundle.getString("label.status.stoppingCameraLivePreviewing"));
		
		Future<ServiceResponse<FaceStopPreviewResponse>> future = Context.getBioKitManager().getFaceService()
																		 .stopPreview(faceDeviceName);
		
		btnCancel.setOnAction(e ->
		{
		    future.cancel(true);
		});
		
		Task<ServiceResponse<FaceStopPreviewResponse>> task = new Task<ServiceResponse<FaceStopPreviewResponse>>()
		{
			@Override
			protected ServiceResponse<FaceStopPreviewResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			tpCameraLivePreview.setActive(false);
			ivCameraLivePreview.setImage(null);
			
		    GuiUtils.showNode(btnCancel, false);
			GuiUtils.showNode(btnStartFaceCapturing, true);
		    
		    ServiceResponse<FaceStopPreviewResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
			    FaceStopPreviewResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == CaptureFaceResponse.SuccessCodes.SUCCESS)
		        {
			        lblStatus.setText(
			        		stringsBundle.getString("label.status.successfullyStoppedCameraLivePreviewing"));
		        }
		        else
		        {
			        lblStatus.setText(String.format(
					        stringsBundle.getString("label.status.failedToStopCameraLivePreviewingWithErrorCode"),
					        result.getReturnCode()));
		        }
		    }
		    else
		    {
			    lblStatus.setText(String.format(
					    stringsBundle.getString("label.status.failedToStopCameraLivePreviewingWithErrorCode"),
					    serviceResponse.getErrorCode()));
			
			    String errorCode = MofaEnrollmentErrorCodes.C007_00001.getCode();
			    String[] errorDetails = {"failed while stopping the camera live preview!"};
			    coreFxController.showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			tpCameraLivePreview.setActive(false);
			ivCameraLivePreview.setImage(null);
			
			GuiUtils.showNode(btnCancel, false);
			
			Throwable exception = task.getException();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnDisconnectFromDeviceManager, true);
				GuiUtils.showNode(btnReinitializeDevice, true);
				GuiUtils.showNode(btnStartFaceCapturing, true);
				lblStatus.setText(stringsBundle.getString("label.status.deviceManagerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				GuiUtils.showNode(btnConnectToDeviceManager, true);
				lblStatus.setText(stringsBundle.getString("label.status.disconnectedFromDeviceManager"));
			}
			else
			{
				GuiUtils.showNode(btnDisconnectFromDeviceManager, true);
				GuiUtils.showNode(btnReinitializeDevice, true);
				GuiUtils.showNode(btnStartFaceCapturing, true);
				lblStatus.setText(stringsBundle.getString("label.status.failedToStopCameraLivePreviewing"));
				
				String errorCode = MofaEnrollmentErrorCodes.C007_00001.getCode();
				String[] errorDetails = {"failed while stopping the camera live preview!"};
				coreFxController.showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(task);
	}
}