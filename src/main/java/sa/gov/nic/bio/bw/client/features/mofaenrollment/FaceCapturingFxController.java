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
import sa.gov.nic.bio.biokit.ResponseProcessor;
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.beans.StartPreviewResponse;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.face.FaceStopPreviewResponse;
import sa.gov.nic.bio.biokit.face.beans.CaptureFaceResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
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
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class FaceCapturingFxController extends WizardStepFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(FaceCapturingFxController.class.getName());
	
	@FXML private ResourceBundle resources;
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
	@FXML private Button btnStartFaceCapturing;
	@FXML private Button btnCaptureFace;
	@FXML private Button btnStopFaceCapturing;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private boolean applyIcao = true;
	private boolean cameraInitializedAtLeastOnce = false;
	
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
		
		SubScene subScene = new SubScene(shapePane, 100.0, 100.0, true,
		                                 SceneAntialiasing.BALANCED);
		subScene.setFill(Color.TRANSPARENT);
		subScene.setCamera(new PerspectiveCamera());
		
		subScenePane.getChildren().setAll(subScene);
		
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
															coreFxController.getDeviceManagerGadgetPaneController();
			
			if(deviceManagerGadgetPaneController.isCameraInitialized())
			{
				GuiUtils.showNode(btnStartFaceCapturing, true);
			}
			else
			{
				lblStatus.setText(resources.getString("label.status.cameraNotInitialized"));
				GuiUtils.showNode(lblStatus, true);
			}
			
			deviceManagerGadgetPaneController.setCameraInitializationListener(initialized ->
			{
				GuiUtils.showNode(lblStatus, true);
				GuiUtils.showNode(btnStopFaceCapturing, false);
				GuiUtils.showNode(btnCaptureFace, false);
				GuiUtils.showNode(btnCancel, false);
				
				tpCameraLivePreview.setActive(false);
				ivCameraLivePreview.setImage(null);
				
				if(initialized)
				{
					GuiUtils.showNode(btnStartFaceCapturing, true);
					lblStatus.setText(resources.getString("label.status.cameraInitializedSuccessfully"));
					cameraInitializedAtLeastOnce = true;
				}
				else if(cameraInitializedAtLeastOnce)
				{
					GuiUtils.showNode(btnStartFaceCapturing, false);
					lblStatus.setText(resources.getString("label.status.cameraDisconnected"));
				}
			});
		}
	}
	
	@Override
	protected void onLeaving(Map<String, Object> uiDataMap)
	{
		if(btnStopFaceCapturing.isVisible()) btnStopFaceCapturing.fire();
	}
	
	@FXML
	private void onStartFaceCapturingButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("start capturing the face...");
		
		GuiUtils.showNode(btnStartFaceCapturing, false);
		GuiUtils.showNode(btnCaptureFace, true);
		GuiUtils.showNode(btnStopFaceCapturing, true);
		GuiUtils.showNode(piCameraLivePreview, true);
		
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
					tpCameraLivePreview.setCaptured(false);
				    ivCameraLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				String cameraDeviceName = coreFxController.getDeviceManagerGadgetPaneController().getCameraDeviceName();
				Future<ServiceResponse<Void>> future = Context.getBioKitManager().getFaceService()
															  .startPreview(cameraDeviceName, responseProcessor);
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piCameraLivePreview, false);
			GuiUtils.showNode(btnStopFaceCapturing, false);
			GuiUtils.showNode(btnCaptureFace, false);
			
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
			GuiUtils.showNode(btnStopFaceCapturing, false);
			GuiUtils.showNode(btnCaptureFace, false);
			
			Throwable exception = task.getException();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnStartFaceCapturing, true);
				lblStatus.setText(stringsBundle.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(stringsBundle.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				lblStatus.setText(stringsBundle.getString("label.status.startingCameraLivePreviewingCancelled"));
				GuiUtils.showNode(btnCancel, false);
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
		tpCameraLivePreview.setCaptured(true);
		
		lblStatus.setText(stringsBundle.getString("label.status.capturingFace"));
		
		String cameraDeviceName = coreFxController.getDeviceManagerGadgetPaneController().getCameraDeviceName();
		Future<ServiceResponse<CaptureFaceResponse>> future = Context.getBioKitManager().getFaceService()
																	 .captureFace(cameraDeviceName, true);
		
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
			    	GuiUtils.showNode(btnStartFaceCapturing, true);
			    	
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
				    GuiUtils.showNode(btnStopFaceCapturing, true);
				    GuiUtils.showNode(btnCaptureFace, true);
			    	
				    lblStatus.setText(String.format(
						    stringsBundle.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
						    result.getReturnCode()));
			    }
		    }
		    else
		    {
			    GuiUtils.showNode(btnStopFaceCapturing, true);
			    GuiUtils.showNode(btnCaptureFace, true);
			    
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
			GuiUtils.showNode(btnStopFaceCapturing, true);
			GuiUtils.showNode(btnCaptureFace, true);
			
			Throwable exception = task.getException();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnStartFaceCapturing, true);
				lblStatus.setText(stringsBundle.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(stringsBundle.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				lblStatus.setText(stringsBundle.getString("label.status.capturingFaceCancelled"));
				GuiUtils.showNode(btnCancel, false);
			}
			else
			{
				lblStatus.setText(stringsBundle.getString("label.status.failedToCaptureTheFace"));
				
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
		
		String cameraDeviceName = coreFxController.getDeviceManagerGadgetPaneController().getCameraDeviceName();
		Future<ServiceResponse<FaceStopPreviewResponse>> future = Context.getBioKitManager().getFaceService()
																		 .stopPreview(cameraDeviceName);
		
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
				GuiUtils.showNode(btnStartFaceCapturing, true);
				lblStatus.setText(stringsBundle.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(stringsBundle.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				lblStatus.setText(stringsBundle.getString("label.status.stoppingCameraLivePreviewingCancelled"));
				GuiUtils.showNode(btnCancel, false);
			}
			else
			{
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