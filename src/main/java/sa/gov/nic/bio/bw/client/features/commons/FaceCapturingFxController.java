package sa.gov.nic.bio.bw.client.features.commons;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
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
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import sa.gov.nic.bio.biokit.ResponseProcessor;
import sa.gov.nic.bio.biokit.beans.LivePreviewingResponse;
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.face.FaceStopPreviewResponse;
import sa.gov.nic.bio.biokit.face.beans.CaptureFaceResponse;
import sa.gov.nic.bio.biokit.face.beans.FaceStartPreviewResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.features.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.client.features.commons.utils.CommonsErrorCodes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class FaceCapturingFxController extends WizardStepFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(FaceCapturingFxController.class.getName());
	public static final String KEY_ICAO_REQUIRED = "ICAO_REQUIRED";
	public static final String KEY_ICAO_SUCCESS_ICON_VISIBLE = "ICAO_SUCCESS_ICON_VISIBLE";
	public static final String KEY_ICAO_WARNING_ICON_VISIBLE = "ICAO_WARNING_ICON_VISIBLE";
	public static final String KEY_ICAO_ERROR_ICON_VISIBLE = "ICAO_ERROR_ICON_VISIBLE";
	public static final String KEY_ICAO_MESSAGE_VISIBLE = "ICAO_MESSAGE_VISIBLE";
	public static final String KEY_ICAO_MESSAGE = "ICAO_MESSAGE";
	public static final String KEY_CAPTURED_IMAGE = "CAPTURED_IMAGE";
	public static final String KEY_CROPPED_IMAGE = "CROPPED_IMAGE";
	public static final String KEY_CAPTURED_IMAGE_TP_ACTIVE = "CAPTURED_IMAGE_TP_ACTIVE";
	public static final String KEY_CAPTURED_IMAGE_TP_CAPTURED = "CAPTURED_IMAGE_TP_CAPTURED";
	public static final String KEY_CAPTURED_IMAGE_TP_DUPLICATED = "CAPTURED_IMAGE_TP_DUPLICATED";
	public static final String KEY_CAPTURED_IMAGE_TP_VALID = "CAPTURED_IMAGE_TP_VALID";
	public static final String KEY_CROPPED_IMAGE_TP_ACTIVE = "CROPPED_IMAGE_TP_ACTIVE";
	public static final String KEY_CROPPED_IMAGE_TP_CAPTURED = "CROPPED_IMAGE_TP_CAPTURED";
	public static final String KEY_CROPPED_IMAGE_TP_DUPLICATED = "CROPPED_IMAGE_TP_DUPLICATED";
	public static final String KEY_CROPPED_IMAGE_TP_VALID = "CROPPED_IMAGE_TP_VALID";
	
	@FXML private ResourceBundle resources;
	@FXML private FourStateTitledPane tpCameraLivePreview;
	@FXML private FourStateTitledPane tpCapturedImage;
	@FXML private FourStateTitledPane tpCroppedImage;
	@FXML private ImageView ivSuccessIcao;
	@FXML private ImageView ivWarningIcao;
	@FXML private ImageView ivErrorIcao;
	@FXML private ImageView ivCameraLivePreview;
	@FXML private ImageView ivCapturedImage;
	@FXML private ImageView ivCroppedImage;
	@FXML private AutoScalingStackPane subScenePane;
	@FXML private Label lblStatus;
	@FXML private Label lblIcaoMessage;
	@FXML private ProgressIndicator piProgress;
	@FXML private ProgressIndicator piIcao;
	@FXML private ProgressIndicator piCameraLivePreview;
	@FXML private ProgressIndicator piCapturedImage;
	@FXML private ProgressIndicator piCroppedImage;
	@FXML private Button btnCancel;
	@FXML private Button btnStartCameraLivePreview;
	@FXML private Button btnStopCameraLivePreview;
	@FXML private Button btnCaptureFace;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private Group face3D;
	private Timeline faceAnimationTimeline;
	private PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
	private boolean cameraInitializedAtLeastOnce = false;
	private boolean captureInProgress = false;
	private boolean icaoRequired = true;
	
	private Translate pivotBase = new Translate(0.0, 0.0, -1.0);
	private Rotate xRotateBase = new Rotate(0.0, Rotate.X_AXIS);
	private Rotate yRotateBase = new Rotate(0.0, Rotate.Y_AXIS);
	private Rotate zRotateBase = new Rotate(0.0, Rotate.Z_AXIS);
	
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
		Group face3DGroup;
		try
		{
			face3DGroup = FXMLLoader.load(getClass().getResource("fxml/face3DModel.fxml"), stringsBundle);
		}
		catch(IOException e)
		{
			String errorCode = CommonsErrorCodes.C008_00001.getCode();
			String[] errorDetails = {"failed to load the face 3D model!"};
			coreFxController.showErrorDialog(errorCode, e, errorDetails);
			
			return;
		}
		
		face3D = (Group) face3DGroup.lookup("#face3D");
		face3D.getTransforms().setAll(pivotBase);
		
		AutoScalingStackPane shapePane = new AutoScalingStackPane(face3DGroup);
		SubScene subScene = new SubScene(shapePane, 100.0, 80.0, true,
		                                 SceneAntialiasing.BALANCED);
		subScene.setFill(Color.TRANSPARENT);
		subScene.setCamera(perspectiveCamera);
		
		subScenePane.getChildren().setAll(subScene);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Boolean bool = (Boolean) uiInputData.get(KEY_ICAO_REQUIRED);
			if(bool != null) icaoRequired = bool;
			
			if(icaoRequired) btnNext.disableProperty().bind(ivCapturedImage.visibleProperty().not().and(
											ivCroppedImage.imageProperty().isNull()).and(
											ivSuccessIcao.imageProperty().isNull()));
			else btnNext.disableProperty().bind(ivCapturedImage.imageProperty().isNull().or(
											ivCroppedImage.imageProperty().isNull()));
			
			bool = (Boolean) uiInputData.get(KEY_ICAO_SUCCESS_ICON_VISIBLE);
			if(bool != null) GuiUtils.showNode(ivSuccessIcao, bool);
			
			bool = (Boolean) uiInputData.get(KEY_ICAO_WARNING_ICON_VISIBLE);
			if(bool != null) GuiUtils.showNode(ivWarningIcao, bool);
			
			bool = (Boolean) uiInputData.get(KEY_ICAO_ERROR_ICON_VISIBLE);
			if(bool != null) GuiUtils.showNode(ivErrorIcao, bool);
			
			bool = (Boolean) uiInputData.get(KEY_ICAO_MESSAGE_VISIBLE);
			if(bool != null) GuiUtils.showNode(lblIcaoMessage, bool);
			
			String icaoMessage = (String) uiInputData.get(KEY_ICAO_MESSAGE);
			lblIcaoMessage.setText(icaoMessage);
			
			Image capturedImage = (Image) uiInputData.get(KEY_CAPTURED_IMAGE);
			if(capturedImage != null)
			{
				ivCapturedImage.setImage(capturedImage);
				GuiUtils.attachImageDialog(coreFxController, ivCapturedImage, tpCapturedImage.getText(),
				                           resources.getString("label.contextMenu.showImage"));
			}
			
			Image croppedImage = (Image) uiInputData.get(KEY_CROPPED_IMAGE);
			if(croppedImage != null)
			{
				ivCroppedImage.setImage(croppedImage);
				GuiUtils.attachImageDialog(coreFxController, ivCroppedImage, tpCroppedImage.getText(),
				                           resources.getString("label.contextMenu.showImage"));
			}
			
			bool = (Boolean) uiInputData.get(KEY_CAPTURED_IMAGE_TP_ACTIVE);
			if(bool != null) tpCapturedImage.setActive(bool);
			
			bool = (Boolean) uiInputData.get(KEY_CAPTURED_IMAGE_TP_CAPTURED);
			if(bool != null) tpCapturedImage.setCaptured(bool);
			
			bool = (Boolean) uiInputData.get(KEY_CAPTURED_IMAGE_TP_DUPLICATED);
			if(bool != null) tpCapturedImage.setDuplicated(bool);
			
			bool = (Boolean) uiInputData.get(KEY_CAPTURED_IMAGE_TP_VALID);
			if(bool != null) tpCapturedImage.setValid(bool);
			
			bool = (Boolean) uiInputData.get(KEY_CROPPED_IMAGE_TP_ACTIVE);
			if(bool != null) tpCroppedImage.setActive(bool);
			
			bool = (Boolean) uiInputData.get(KEY_CROPPED_IMAGE_TP_CAPTURED);
			if(bool != null) tpCroppedImage.setCaptured(bool);
			
			bool = (Boolean) uiInputData.get(KEY_CROPPED_IMAGE_TP_DUPLICATED);
			if(bool != null) tpCroppedImage.setDuplicated(bool);
			
			bool = (Boolean) uiInputData.get(KEY_CROPPED_IMAGE_TP_VALID);
			if(bool != null) tpCroppedImage.setValid(bool);
			
			DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
															coreFxController.getDeviceManagerGadgetPaneController();
			
			if(deviceManagerGadgetPaneController.isCameraInitialized())
			{
				GuiUtils.showNode(btnStartCameraLivePreview, true);
			}
			else
			{
				lblStatus.setText(resources.getString("label.status.cameraNotInitialized"));
				GuiUtils.showNode(lblStatus, true);
			}
			
			deviceManagerGadgetPaneController.setCameraInitializationListener(initialized -> Platform.runLater(() ->
			{
				GuiUtils.showNode(lblStatus, true);
				GuiUtils.showNode(btnStopCameraLivePreview, false);
				GuiUtils.showNode(btnCaptureFace, false);
				GuiUtils.showNode(btnCancel, false);
				
				tpCameraLivePreview.setActive(false);
				ivCameraLivePreview.setImage(null);
				
				if(initialized)
				{
					GuiUtils.showNode(btnStartCameraLivePreview, true);
					lblStatus.setText(resources.getString("label.status.cameraInitializedSuccessfully"));
					cameraInitializedAtLeastOnce = true;
					LOGGER.info("The camera is initialized!");
				}
				else if(cameraInitializedAtLeastOnce)
				{
					GuiUtils.showNode(btnStartCameraLivePreview, false);
					lblStatus.setText(resources.getString("label.status.cameraDisconnected"));
					LOGGER.info("The camera is disconnected!");
				}
			}));
		}
	}
	
	@Override
	protected void onLeaving(Map<String, Object> uiDataMap)
	{
		if(btnStopCameraLivePreview.isVisible()) btnStopCameraLivePreview.fire();
		
		uiDataMap.put(KEY_ICAO_SUCCESS_ICON_VISIBLE, ivSuccessIcao.isVisible());
		uiDataMap.put(KEY_ICAO_WARNING_ICON_VISIBLE, ivWarningIcao.isVisible());
		uiDataMap.put(KEY_ICAO_ERROR_ICON_VISIBLE, ivErrorIcao.isVisible());
		uiDataMap.put(KEY_ICAO_MESSAGE_VISIBLE, lblIcaoMessage.isVisible());
		uiDataMap.put(KEY_ICAO_MESSAGE, lblIcaoMessage.getText());
		uiDataMap.put(KEY_CAPTURED_IMAGE, ivCapturedImage.getImage());
		uiDataMap.put(KEY_CROPPED_IMAGE, ivCroppedImage.getImage());
		
		if(tpCapturedImage.isCaptured())
		{
			uiDataMap.put(KEY_CAPTURED_IMAGE_TP_ACTIVE, tpCapturedImage.isActive());
			uiDataMap.put(KEY_CAPTURED_IMAGE_TP_CAPTURED, true);
			uiDataMap.put(KEY_CAPTURED_IMAGE_TP_DUPLICATED, tpCapturedImage.isDuplicated());
			uiDataMap.put(KEY_CAPTURED_IMAGE_TP_VALID, tpCapturedImage.isValid());
		}
		
		if(tpCroppedImage.isCaptured())
		{
			uiDataMap.put(KEY_CROPPED_IMAGE_TP_ACTIVE, tpCroppedImage.isActive());
			uiDataMap.put(KEY_CROPPED_IMAGE_TP_CAPTURED, true);
			uiDataMap.put(KEY_CROPPED_IMAGE_TP_DUPLICATED, tpCroppedImage.isDuplicated());
			uiDataMap.put(KEY_CROPPED_IMAGE_TP_VALID, tpCroppedImage.isValid());
		}
	}
	
	@FXML
	private void onStartCameraLivePreviewButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("starting camera live preview...");
		
		GuiUtils.showNode(btnStartCameraLivePreview, false);
		GuiUtils.showNode(piCameraLivePreview, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.waitingDeviceResponse"));
		boolean[] first = {true};
		
		Task<ServiceResponse<FaceStartPreviewResponse>> task = new Task<ServiceResponse<FaceStartPreviewResponse>>()
		{
			@Override
			protected ServiceResponse<FaceStartPreviewResponse> call() throws Exception
			{
				ResponseProcessor<LivePreviewingResponse> responseProcessor = response -> Platform.runLater(() ->
				{
				    if(first[0])
				    {
				        first[0] = false;
				        lblStatus.setText(stringsBundle.getString("label.status.cameraLivePreviewing"));
				        GuiUtils.showNode(piCameraLivePreview, false);
					    GuiUtils.showNode(btnStopCameraLivePreview, true);
					    GuiUtils.showNode(btnCaptureFace, true);
				    }
				    
				    String previewImageBase64 = response.getPreviewImage();
				    byte[] bytes = Base64.getDecoder().decode(previewImageBase64);
				    
					if(!captureInProgress)
					{
						tpCameraLivePreview.setActive(true);
						ivCameraLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
					}
				});
				
				String cameraDeviceName = coreFxController.getDeviceManagerGadgetPaneController().getCameraDeviceName();
				Future<ServiceResponse<FaceStartPreviewResponse>> future = Context.getBioKitManager().getFaceService()
															  .startPreview(cameraDeviceName, responseProcessor);
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piCameraLivePreview, false);
			GuiUtils.showNode(btnStopCameraLivePreview, false);
			GuiUtils.showNode(btnCaptureFace, false);
			
		    ServiceResponse<FaceStartPreviewResponse> serviceResponse = task.getValue();
			
		    if(serviceResponse.isSuccess())
		    {
			    FaceStartPreviewResponse result = serviceResponse.getResult();
			
			    if(result.getReturnCode() != FaceStartPreviewResponse.SuccessCodes.SUCCESS)
			    {
				    GuiUtils.showNode(btnStartCameraLivePreview, true);
				    lblStatus.setText(String.format(
						    stringsBundle.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
						    result.getReturnCode()));
			    }
		    }
		    else
		    {
			    GuiUtils.showNode(btnStartCameraLivePreview, true);
			    lblStatus.setText(String.format(
					    stringsBundle.getString("label.status.failedToStartCameraLivePreviewingWithErrorCode"),
					    serviceResponse.getErrorCode()));
			
			    String errorCode = CommonsErrorCodes.C008_00002.getCode();
			    String[] errorDetails = {"failed while starting the camera live preview!"};
			    coreFxController.showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
		    GuiUtils.showNode(piCameraLivePreview, false);
			GuiUtils.showNode(btnStopCameraLivePreview, false);
			GuiUtils.showNode(btnCaptureFace, false);
			
			Throwable exception = task.getException();
			
			if(exception instanceof ExecutionException) exception = exception.getCause();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnStartCameraLivePreview, true);
				lblStatus.setText(stringsBundle.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(stringsBundle.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				GuiUtils.showNode(btnCancel, false);
				GuiUtils.showNode(btnStartCameraLivePreview, true);
				lblStatus.setText(stringsBundle.getString("label.status.startingCameraLivePreviewingCancelled"));
			}
			else
			{
				GuiUtils.showNode(btnStartCameraLivePreview, true);
				lblStatus.setText(stringsBundle.getString("label.status.failedToStartCameraLivePreviewing"));
				
				String errorCode = CommonsErrorCodes.C008_00003.getCode();
				String[] errorDetails = {"failed while starting the camera live preview!"};
				coreFxController.showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onStopCameraLivePreviewButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("stop capturing the face...");
		
		GuiUtils.showNode(btnCaptureFace, false);
		GuiUtils.showNode(btnStopCameraLivePreview, false);
		GuiUtils.showNode(btnCancel, true);
		
		ivCameraLivePreview.setImage(null);
		
		lblStatus.setText(stringsBundle.getString("label.status.stoppingCameraLivePreviewing"));
		
		String cameraDeviceName = coreFxController.getDeviceManagerGadgetPaneController().getCameraDeviceName();
		Future<ServiceResponse<FaceStopPreviewResponse>> future = Context.getBioKitManager().getFaceService()
																		 .stopPreview(cameraDeviceName);
		
		btnCancel.setOnAction(e -> future.cancel(true));
		
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
		    GuiUtils.showNode(btnStartCameraLivePreview, true);
		
		    ServiceResponse<FaceStopPreviewResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
		        FaceStopPreviewResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == FaceStopPreviewResponse.SuccessCodes.SUCCESS)
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
		
		        String errorCode = CommonsErrorCodes.C008_00004.getCode();
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
			
			if(exception instanceof ExecutionException) exception = exception.getCause();
		
		    if(exception instanceof TimeoutException)
		    {
		        GuiUtils.showNode(btnStartCameraLivePreview, true);
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
		        GuiUtils.showNode(btnStartCameraLivePreview, true);
		        lblStatus.setText(stringsBundle.getString("label.status.failedToStopCameraLivePreviewing"));
		
		        String errorCode = CommonsErrorCodes.C008_00005.getCode();
		        String[] errorDetails = {"failed while stopping the camera live preview!"};
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
		GuiUtils.showNode(btnStopCameraLivePreview, false);
		GuiUtils.showNode(btnCancel, true);
		GuiUtils.showNode(ivSuccessIcao, false);
		GuiUtils.showNode(ivWarningIcao, false);
		GuiUtils.showNode(ivErrorIcao, false);
		GuiUtils.showNode(lblIcaoMessage, false);
		GuiUtils.showNode(piIcao, true);
		GuiUtils.showNode(piCapturedImage, true);
		GuiUtils.showNode(piCroppedImage, true);
		GuiUtils.showNode(btnCancel, true);
		
		if(faceAnimationTimeline != null)
		{
			face3D.getTransforms().setAll(pivotBase, xRotateBase, yRotateBase, zRotateBase);
			faceAnimationTimeline.stop();
		}
		
		ivCapturedImage.setImage(null);
		ivCroppedImage.setImage(null);
		
		captureInProgress = true;
		tpCameraLivePreview.setActive(false);
		tpCameraLivePreview.setDisable(true);
		
		tpCapturedImage.setActive(true);
		tpCapturedImage.setCaptured(false);
		tpCapturedImage.setDuplicated(false);
		tpCapturedImage.setValid(false);
		tpCroppedImage.setActive(true);
		tpCroppedImage.setCaptured(false);
		tpCroppedImage.setDuplicated(false);
		tpCroppedImage.setValid(false);
		
		lblStatus.setText(stringsBundle.getString("label.status.capturingFace"));
		
		String cameraDeviceName = coreFxController.getDeviceManagerGadgetPaneController().getCameraDeviceName();
		Future<ServiceResponse<CaptureFaceResponse>> future = Context.getBioKitManager().getFaceService()
																	 .captureFace(cameraDeviceName, true);
		
		btnCancel.setOnAction(e -> future.cancel(true));
		
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
			GuiUtils.showNode(piIcao, false);
			GuiUtils.showNode(piCapturedImage, false);
			GuiUtils.showNode(piCroppedImage, false);
			GuiUtils.showNode(btnCancel, false);
			GuiUtils.showNode(btnStopCameraLivePreview, true);
			GuiUtils.showNode(btnCaptureFace, true);
			
			tpCameraLivePreview.setDisable(false);
			captureInProgress = false;
			
		    ServiceResponse<CaptureFaceResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
			    CaptureFaceResponse result = serviceResponse.getResult();
			    
			    if(result.getReturnCode() == CaptureFaceResponse.SuccessCodes.SUCCESS)
			    {
				    lblStatus.setText(stringsBundle.getString("label.status.cameraLivePreviewing"));
				    
				    String capturedImage = result.getCapturedImage();
				    String croppedImage = result.getCroppedImage();
				    
				    if(capturedImage != null)
				    {
					    byte[] bytes = Base64.getDecoder().decode(capturedImage);
					    ivCapturedImage.setImage(new Image(new ByteArrayInputStream(bytes)));
					
					    tpCapturedImage.setActive(true);
					    tpCapturedImage.setCaptured(true);
					    tpCapturedImage.setValid(true);
					
					    GuiUtils.attachImageDialog(coreFxController, ivCapturedImage, tpCapturedImage.getText(),
					                               resources.getString("label.contextMenu.showImage"));
				    }
				    else
				    {
					    tpCapturedImage.setActive(true);
					    tpCapturedImage.setCaptured(true);
					    tpCapturedImage.setDuplicated(true);
				    }
				    
				    boolean croppedImageExists = false;
				    
				    if(croppedImage != null)
				    {
					    byte[] bytes = Base64.getDecoder().decode(croppedImage);
					    ivCroppedImage.setImage(new Image(new ByteArrayInputStream(bytes)));
					    croppedImageExists = true;
					
					    GuiUtils.attachImageDialog(coreFxController, ivCroppedImage, tpCroppedImage.getText(),
					                               resources.getString("label.contextMenu.showImage"));
				    }
				    else
				    {
					    tpCroppedImage.setActive(true);
					    tpCroppedImage.setCaptured(true);
					    tpCroppedImage.setDuplicated(true);
				    }
				
				    String icaoCode = result.getIcaoErrorMessage();
				    GuiUtils.showNode(lblIcaoMessage, true);
				    
				    if(CaptureFaceResponse.IcaoCodes.SUCCESS.equals(icaoCode))
				    {
				    	GuiUtils.showNode(ivSuccessIcao, true);
				    	lblIcaoMessage.setText(resources.getString("label.icao.success"));
					
					    tpCroppedImage.setActive(true);
					    tpCroppedImage.setCaptured(true);
					    tpCroppedImage.setValid(true);
					    
					    if(btnStopCameraLivePreview.isVisible()) btnStopCameraLivePreview.fire();
				    }
				    else
				    {
				    	if(croppedImageExists)
					    {
						    tpCroppedImage.setActive(true);
						    tpCroppedImage.setCaptured(true);
						    tpCroppedImage.setValid(false);
					    }
				    	
				    	switch(icaoCode)
					    {
						    case CaptureFaceResponse.IcaoCodes.FACE_NOT_DETECTED:
						    {
							    GuiUtils.showNode(ivErrorIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.noFace"));
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.MULTIPLE_FACES_DETECTED:
						    {
							    GuiUtils.showNode(ivErrorIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.multipleFaces"));
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.PITCH_ERROR:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.pitch"));
							    animateHeadRotation(Rotate.X_AXIS, 10.0, 5);
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.YAW_ERROR:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.yaw"));
							    animateHeadRotation(Rotate.Y_AXIS, 25.0, 5);
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.ROLL_ERROR:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.roll"));
							    animateHeadRotation(Rotate.Z_AXIS, 10.0, 5);
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.SHADOW_ERROR:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.shadow"));
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.RIGHT_EYE_CLOSED:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.rightEyeClosed"));
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.LEFT_EYE_CLOSED:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.leftEyeClosed"));
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.EYE_GAZE_ERROR:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.eyeGaze"));
							    break;
						    }
						    default:
						    {
							    GuiUtils.showNode(ivErrorIcao, true);
							    lblIcaoMessage.setText(String.format(resources.getString("label.icao.unknown"),
							                                         icaoCode));
							    break;
						    }
					    }
				    }
			    }
			    else
			    {
				    lblStatus.setText(String.format(
						    stringsBundle.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
						    result.getReturnCode()));
			    }
		    }
		    else
		    {
			    GuiUtils.showNode(btnStopCameraLivePreview, true);
			    GuiUtils.showNode(btnCaptureFace, true);
			    
			    lblStatus.setText(String.format(
					    stringsBundle.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
					    serviceResponse.getErrorCode()));
			
			    String errorCode = CommonsErrorCodes.C008_00006.getCode();
			    String[] errorDetails = {"failed while capturing the face!"};
			    coreFxController.showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piIcao, false);
			GuiUtils.showNode(piCapturedImage, false);
			GuiUtils.showNode(piCroppedImage, false);
			GuiUtils.showNode(btnStopCameraLivePreview, true);
			GuiUtils.showNode(btnCaptureFace, true);
			
			tpCameraLivePreview.setDisable(false);
			captureInProgress = false;
			
			Throwable exception = task.getException();
			
			if(exception instanceof ExecutionException) exception = exception.getCause();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnStartCameraLivePreview, true);
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
				
				String errorCode = CommonsErrorCodes.C008_00007.getCode();
				String[] errorDetails = {"failed while capturing the face!"};
				coreFxController.showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(task);
	}
	
	private void animateHeadRotation(Point3D axis, double degree, int cycleCount)
	{
		Rotate rotate = new Rotate(0.0, 0.0, 0.0, 0.0, axis);
		
		face3D.getTransforms().setAll(pivotBase, rotate);
		faceAnimationTimeline = new Timeline(
			new KeyFrame(
					Duration.seconds(0.0),
					new KeyValue(rotate.angleProperty(), 0.0)
			),
			new KeyFrame(
					Duration.seconds(0.5),
					new KeyValue(rotate.angleProperty(), degree)
			),
			new KeyFrame(
					Duration.seconds(1.0),
					new KeyValue(rotate.angleProperty(), 0.0)
			),
			new KeyFrame(
					Duration.seconds(1.5),
					new KeyValue(rotate.angleProperty(), -degree)
			),
			new KeyFrame(
					Duration.seconds(2.0),
					new KeyValue(rotate.angleProperty(), 0.0)
			)
		);
		faceAnimationTimeline.setCycleCount(cycleCount);
		faceAnimationTimeline.play();
	}
}