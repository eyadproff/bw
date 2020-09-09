package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
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
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.face.FaceStopPreviewResponse;
import sa.gov.nic.bio.biokit.face.beans.CaptureFaceResponse;
import sa.gov.nic.bio.biokit.face.beans.FaceStartPreviewResponse;
import sa.gov.nic.bio.bw.commons.resources.fxml.CommonFXML;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@FxmlFile("faceCapturing.fxml")
public class FaceCapturingFxController extends WizardStepFxControllerBase
{
	@Input private Boolean acceptAnyCapturedImage;
	@Input private Boolean acceptBadQualityFace;
	@Input private Integer acceptBadQualityFaceMinRetries;
	@Input private String imageExtension;
	@Output private Image capturedFacePhoto;
	@Output private Image croppedFacePhoto;
	@Output private Image facePhoto;
	@Output private String facePhotoBase64;
	@Output private Boolean icaoSuccessIconVisible;
	@Output private Boolean icaoWarningIconVisible;
	@Output private Boolean icaoErrorIconVisible;
	@Output private Boolean icaoMessageVisible;
	@Output private String icaoMessage;
	@Output private Boolean capturedImageTitledPaneActive;
	@Output private Boolean capturedImageTitledPaneCaptured;
	@Output private Boolean capturedImageTitledPaneDuplicated;
	@Output private Boolean capturedImageTitledPaneValid;
	@Output private Boolean croppedImageTitledPaneActive;
	@Output private Boolean croppedImageTitledPaneCaptured;
	@Output private Boolean croppedImageTitledPaneDuplicated;
	@Output private Boolean croppedImageTitledPaneValid;
	
	@FXML private FourStateTitledPane tpCameraLivePreview;
	@FXML private FourStateTitledPane tpCapturedImage;
	@FXML private FourStateTitledPane tpCroppedImage;
	@FXML private ImageView ivSuccessIcao;
	@FXML private ImageView ivWarningIcao;
	@FXML private ImageView ivErrorIcao;
	@FXML private ImageView ivCameraLivePreviewPlaceholder;
	@FXML private ImageView ivCapturedImagePlaceholder;
	@FXML private ImageView ivCroppedImagePlaceholder;
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
	private int successfulCroppedCapturingCount = 0;
	
	private Translate pivotBase = new Translate(0.0, 0.0, -1.0);
	private Rotate xRotateBase = new Rotate(0.0, Rotate.X_AXIS);
	private Rotate yRotateBase = new Rotate(0.0, Rotate.Y_AXIS);
	private Rotate zRotateBase = new Rotate(0.0, Rotate.Z_AXIS);
	
	@Override
	protected void onAttachedToScene()
	{
		ivCameraLivePreviewPlaceholder.visibleProperty().bind(ivCameraLivePreview.imageProperty().isNull().and(
				piCameraLivePreview.visibleProperty().not()));
		ivCapturedImagePlaceholder.visibleProperty().bind(ivCapturedImage.imageProperty().isNull().and(
				piCapturedImage.visibleProperty().not()));
		ivCroppedImagePlaceholder.visibleProperty().bind(ivCroppedImage.imageProperty().isNull().and(
				piCroppedImage.visibleProperty().not()));
		
		btnNext.disabledProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(!newValue) btnNext.requestFocus();
		});
		
		if(Platform.isSupported(ConditionalFeature.SCENE3D))
		{
			Context.getExecutorService().submit(() ->
			{
				Group face3DGroup;
				try
				{
					URL url = CommonFXML.FACE_3D_MODEL.getAsUrl();
					if(url != null) face3DGroup = FXMLLoader.load(url, resources);
					else
					{
						String errorCode = CommonsErrorCodes.C008_00017.getCode();
						String[] errorDetails = {"failed to load the face 3D model fxml!"};
						Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails, getTabIndex());
						
						return;
					}
				}
				catch(Exception e)
				{
					String errorCode = CommonsErrorCodes.C008_00001.getCode();
					String[] errorDetails = {"failed to load the face 3D model!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
					
					return;
				}
				
				Platform.runLater(() ->
				{
					face3D = (Group) face3DGroup.lookup("#face3D");
					face3D.getTransforms().setAll(pivotBase);
					
					AutoScalingStackPane shapePane = new AutoScalingStackPane(face3DGroup);
					SubScene subScene = new SubScene(shapePane, 100.0, 80.0, true,
					                                 SceneAntialiasing.BALANCED);
					subScene.setFill(Color.TRANSPARENT);
					subScene.setCamera(perspectiveCamera);
					
					subScenePane.getChildren().setAll(subScene);
					GuiUtils.showNode(subScenePane, true);
				});
			});
		}
		
		if(acceptAnyCapturedImage != null && acceptAnyCapturedImage)
		{
			btnNext.disableProperty().bind(ivCapturedImage.imageProperty().isNull().and(
										   ivCroppedImage.imageProperty().isNull()));
		}
		else if(acceptBadQualityFace != null && acceptBadQualityFaceMinRetries != null && acceptBadQualityFace)
		{
			btnNext.setDisable(!ivSuccessIcao.isVisible() &&
					                   successfulCroppedCapturingCount < acceptBadQualityFaceMinRetries);
		}
		else // accept good quality face only
		{
			btnNext.disableProperty().bind(ivCapturedImage.imageProperty().isNull().or(
					ivCroppedImage.imageProperty().isNull()).or(
					ivSuccessIcao.visibleProperty().not()));
		}
		
		if(icaoSuccessIconVisible != null) GuiUtils.showNode(ivSuccessIcao, icaoSuccessIconVisible);
		if(icaoWarningIconVisible != null) GuiUtils.showNode(ivWarningIcao, icaoWarningIconVisible);
		if(icaoErrorIconVisible != null) GuiUtils.showNode(ivErrorIcao, icaoErrorIconVisible);
		if(icaoMessageVisible != null) GuiUtils.showNode(lblIcaoMessage, icaoMessageVisible);
		
		lblIcaoMessage.setText(icaoMessage);
		
		if(capturedFacePhoto != null)
		{
			ivCapturedImage.setImage(capturedFacePhoto);
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCapturedImage, tpCapturedImage.getText(),
			                           resources.getString("label.contextMenu.showImage"), false);
		}
		
		if(croppedFacePhoto != null)
		{
			ivCroppedImage.setImage(croppedFacePhoto);
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCroppedImage, tpCroppedImage.getText(),
			                           resources.getString("label.contextMenu.showImage"), false);
		}
		
		if(capturedImageTitledPaneActive != null) tpCapturedImage.setActive(capturedImageTitledPaneActive);
		if(capturedImageTitledPaneCaptured != null) tpCapturedImage.setCaptured(capturedImageTitledPaneCaptured);
		if(capturedImageTitledPaneDuplicated != null)
			tpCapturedImage.setDuplicated(capturedImageTitledPaneDuplicated);
		if(capturedImageTitledPaneValid != null) tpCapturedImage.setValid(capturedImageTitledPaneValid);
		if(croppedImageTitledPaneActive != null) tpCroppedImage.setActive(croppedImageTitledPaneActive);
		if(croppedImageTitledPaneCaptured != null) tpCroppedImage.setCaptured(croppedImageTitledPaneCaptured);
		if(croppedImageTitledPaneDuplicated != null) tpCroppedImage.setDuplicated(croppedImageTitledPaneDuplicated);
		if(croppedImageTitledPaneValid != null) tpCroppedImage.setValid(croppedImageTitledPaneValid);
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
				Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
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
		
		Platform.runLater(() ->
		{
		    if(Context.getCoreFxController().getDeviceManagerGadgetPaneController().isDevicesRunnerRunning() &&
		            !Context.getCoreFxController().getDeviceManagerGadgetPaneController().isCameraInitialized())
		    {
		        Context.getCoreFxController().getDeviceManagerGadgetPaneController().initializeCamera();
		    }
		});
	}
	
	@Override
	protected void onDetachingFromScene()
	{
		Platform.runLater(btnStopCameraLivePreview::fire);
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		icaoSuccessIconVisible = ivSuccessIcao.isVisible();
		icaoWarningIconVisible = ivWarningIcao.isVisible();
		icaoErrorIconVisible = ivErrorIcao.isVisible();
		icaoMessageVisible = lblIcaoMessage.isVisible();
		icaoMessage = lblIcaoMessage.getText();
		capturedFacePhoto = ivCapturedImage.getImage();
		croppedFacePhoto = ivCroppedImage.getImage();
		
		if(tpCapturedImage.isCaptured())
		{
			capturedImageTitledPaneActive = tpCapturedImage.isActive();
			capturedImageTitledPaneCaptured = true;
			capturedImageTitledPaneDuplicated = tpCapturedImage.isDuplicated();
			capturedImageTitledPaneValid = tpCapturedImage.isValid();
		}
		
		if(tpCroppedImage.isCaptured())
		{
			croppedImageTitledPaneCaptured = true;
			croppedImageTitledPaneActive = tpCroppedImage.isActive();
			croppedImageTitledPaneDuplicated = tpCroppedImage.isDuplicated();
			croppedImageTitledPaneValid = tpCroppedImage.isValid();
		}
		
		Image capturedImage = ivCapturedImage.getImage();
		Image croppedImage = ivCroppedImage.getImage();
		
		if(croppedImage != null) facePhoto = croppedImage;
		else facePhoto = capturedImage;
		
		try
		{

			if(facePhoto != null) {
				if (imageExtension!=null)
					facePhotoBase64 = AppUtils.imageToBase64(facePhoto, imageExtension);
				else
					facePhotoBase64 = AppUtils.imageToBase64(facePhoto);
			}else {
				facePhotoBase64 = null;
			}

		}
		catch(IOException e)
		{
			String errorCode = CommonsErrorCodes.C008_00018.getCode();
			String[] errorDetails = {"failed to encode the face image as Base64!"};
			Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails, getTabIndex());
		}
	}
	
	@FXML
	private void onStartCameraLivePreviewButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("starting camera live preview...");
		
		GuiUtils.showNode(btnStartCameraLivePreview, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(piCameraLivePreview, true);
		
		lblStatus.setText(resources.getString("label.status.waitingDeviceResponse"));
		boolean[] first = {true};
		
		Task<TaskResponse<FaceStartPreviewResponse>> task = new Task<TaskResponse<FaceStartPreviewResponse>>()
		{
			@Override
			protected TaskResponse<FaceStartPreviewResponse> call() throws Exception
			{
				ResponseProcessor<LivePreviewingResponse> responseProcessor = response -> Platform.runLater(() ->
				{
				    if(first[0])
				    {
				        first[0] = false;
				        lblStatus.setText(resources.getString("label.status.cameraLivePreviewing"));
					    GuiUtils.showNode(piProgress, false);
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
				
				String cameraDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																	   .getCameraDeviceName();
				Future<TaskResponse<FaceStartPreviewResponse>> future = Context.getBioKitManager().getFaceService()
															  .startPreview(cameraDeviceName, responseProcessor);
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(piCameraLivePreview, false);
			GuiUtils.showNode(btnStopCameraLivePreview, false);
			GuiUtils.showNode(btnCaptureFace, false);
			
		    TaskResponse<FaceStartPreviewResponse> taskResponse = task.getValue();
			
		    if(taskResponse.isSuccess())
		    {
			    FaceStartPreviewResponse result = taskResponse.getResult();
			
			    if(result.getReturnCode() != FaceStartPreviewResponse.SuccessCodes.SUCCESS)
			    {
				    GuiUtils.showNode(btnStartCameraLivePreview, true);
				    
			    	if(result.getReturnCode() ==
						                    FaceStartPreviewResponse.FailureCodes.EXCEPTION_WHILE_GETTING_PREVIEW_IMAGE)
				    {
					    lblStatus.setText(resources.getString("label.status.exceptionWhileGettingPreviewImage"));
				    }
				    else if(result.getReturnCode() ==
					                                FaceStartPreviewResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
				    {
					    lblStatus.setText(resources.getString("label.status.cameraNotFoundOrUnplugged"));
					
					    GuiUtils.showNode(btnStartCameraLivePreview,false);
					    DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
						                        Context.getCoreFxController().getDeviceManagerGadgetPaneController();
					    deviceManagerGadgetPaneController.initializeCamera();
				    }
				    else if(result.getReturnCode() == FaceStartPreviewResponse.FailureCodes.DEVICE_BUSY)
				    {
					    lblStatus.setText(resources.getString("label.status.cameraBusy"));
				    }
				    else
				    {
					    lblStatus.setText(String.format(
							    resources.getString("label.status.failedToStartCameraLivePreviewingWithErrorCode"),
							    result.getReturnCode()));
				    }
			    }
		    }
		    else
		    {
			    GuiUtils.showNode(btnStartCameraLivePreview, true);
			    lblStatus.setText(String.format(
					    resources.getString("label.status.failedToStartCameraLivePreviewingWithErrorCode"),
					    taskResponse.getErrorCode()));
			
			    String errorCode = CommonsErrorCodes.C008_00002.getCode();
			    String[] errorDetails = {"failed while starting the camera live preview!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(piCameraLivePreview, false);
			GuiUtils.showNode(btnStopCameraLivePreview, false);
			GuiUtils.showNode(btnCaptureFace, false);
			
			Throwable exception = task.getException();
			
			if(exception instanceof ExecutionException) exception = exception.getCause();
			
			if(exception instanceof TimeoutException)
			{
				GuiUtils.showNode(btnStartCameraLivePreview, true);
				lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				GuiUtils.showNode(btnStartCameraLivePreview, true);
				lblStatus.setText(resources.getString("label.status.startingCameraLivePreviewingCancelled"));
			}
			else
			{
				GuiUtils.showNode(btnStartCameraLivePreview, true);
				lblStatus.setText(resources.getString("label.status.failedToStartCameraLivePreviewing"));
				
				String errorCode = CommonsErrorCodes.C008_00003.getCode();
				String[] errorDetails = {"failed while starting the camera live preview!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			}
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onStopCameraLivePreviewButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("stopping camera live preview...");
		
		GuiUtils.showNode(btnCaptureFace, false);
		GuiUtils.showNode(btnStopCameraLivePreview, false);
		GuiUtils.showNode(piProgress, true);
		
		ivCameraLivePreview.setImage(null);
		
		lblStatus.setText(resources.getString("label.status.stoppingCameraLivePreviewing"));
		
		String cameraDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
															   .getCameraDeviceName();
		Future<TaskResponse<FaceStopPreviewResponse>> future = Context.getBioKitManager().getFaceService()
																		 .stopPreview(cameraDeviceName);
		
		Task<TaskResponse<FaceStopPreviewResponse>> task = new Task<TaskResponse<FaceStopPreviewResponse>>()
		{
			@Override
			protected TaskResponse<FaceStopPreviewResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piProgress, false);
		    tpCameraLivePreview.setActive(false);
		    ivCameraLivePreview.setImage(null);
		
		    GuiUtils.showNode(btnStartCameraLivePreview, true);
		
		    TaskResponse<FaceStopPreviewResponse> taskResponse = task.getValue();
		
		    if(taskResponse.isSuccess())
		    {
		        FaceStopPreviewResponse result = taskResponse.getResult();
		
		        if(result.getReturnCode() == FaceStopPreviewResponse.SuccessCodes.SUCCESS)
		        {
		            lblStatus.setText(
		                    resources.getString("label.status.successfullyStoppedCameraLivePreviewing"));
		        }
		        else
		        {
		            lblStatus.setText(String.format(
		                    resources.getString("label.status.failedToStopCameraLivePreviewingWithErrorCode"),
		                    result.getReturnCode()));
		        }
		    }
		    else
		    {
		        lblStatus.setText(String.format(
				        resources.getString("label.status.failedToStopCameraLivePreviewingWithErrorCode"),
				        taskResponse.getErrorCode()));
		
		        String errorCode = CommonsErrorCodes.C008_00004.getCode();
		        String[] errorDetails = {"failed while stopping the camera live preview!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piProgress, false);
		    tpCameraLivePreview.setActive(false);
		    ivCameraLivePreview.setImage(null);
		
		    Throwable exception = task.getException();
			
			if(exception instanceof ExecutionException) exception = exception.getCause();
		
		    if(exception instanceof TimeoutException)
		    {
		        GuiUtils.showNode(btnStartCameraLivePreview, true);
		        lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
		    }
		    else if(exception instanceof NotConnectedException)
		    {
		        lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
		    }
		    else if(exception instanceof CancellationException)
		    {
		        lblStatus.setText(resources.getString("label.status.stoppingCameraLivePreviewingCancelled"));
		    }
		    else
		    {
		        GuiUtils.showNode(btnStartCameraLivePreview, true);
		        lblStatus.setText(resources.getString("label.status.failedToStopCameraLivePreviewing"));
		
		        String errorCode = CommonsErrorCodes.C008_00005.getCode();
		        String[] errorDetails = {"failed while stopping the camera live preview!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		    }
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onCaptureFaceButtonClicked(ActionEvent actionEvent)
	{
		LOGGER.info("capturing the face...");
		if(acceptBadQualityFace != null && acceptBadQualityFace) btnNext.setDisable(true);
		
		GuiUtils.showNode(btnCaptureFace, false);
		GuiUtils.showNode(btnStopCameraLivePreview, false);
		GuiUtils.showNode(ivSuccessIcao, false);
		GuiUtils.showNode(ivWarningIcao, false);
		GuiUtils.showNode(ivErrorIcao, false);
		GuiUtils.showNode(lblIcaoMessage, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(piIcao, true);
		GuiUtils.showNode(piCapturedImage, true);
		GuiUtils.showNode(piCroppedImage, true);
		
		if(Platform.isSupported(ConditionalFeature.SCENE3D) && faceAnimationTimeline != null)
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
		
		lblStatus.setText(resources.getString("label.status.capturingFace"));
		
		String cameraDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
															   .getCameraDeviceName();
		Future<TaskResponse<CaptureFaceResponse>> future = Context.getBioKitManager().getFaceService()
																	 .captureFace(cameraDeviceName, true);
		
		Task<TaskResponse<CaptureFaceResponse>> task = new Task<TaskResponse<CaptureFaceResponse>>()
		{
			@Override
			protected TaskResponse<CaptureFaceResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(piIcao, false);
			GuiUtils.showNode(piCapturedImage, false);
			GuiUtils.showNode(piCroppedImage, false);
			GuiUtils.showNode(btnStopCameraLivePreview, true);
			GuiUtils.showNode(btnCaptureFace, true);
			
			tpCameraLivePreview.setDisable(false);
			captureInProgress = false;
			
		    TaskResponse<CaptureFaceResponse> taskResponse = task.getValue();
		
		    if(taskResponse.isSuccess())
		    {
			    CaptureFaceResponse result = taskResponse.getResult();
			    
			    if(result.getReturnCode() == CaptureFaceResponse.SuccessCodes.SUCCESS)
			    {
				    lblStatus.setText(resources.getString("label.status.cameraLivePreviewing"));
				    
				    String capturedImage = result.getCapturedImage();
				    String croppedImage = result.getCroppedImage();
				    
				    if(capturedImage != null)
				    {
					    byte[] bytes = Base64.getDecoder().decode(capturedImage);
					    ivCapturedImage.setImage(new Image(new ByteArrayInputStream(bytes)));
					
					    tpCapturedImage.setActive(true);
					    tpCapturedImage.setCaptured(true);
					    tpCapturedImage.setValid(true);
					
					    GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCapturedImage,
					                               tpCapturedImage.getText(),
					                               resources.getString("label.contextMenu.showImage"), false);
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
					    successfulCroppedCapturingCount++;
					    byte[] bytes = Base64.getDecoder().decode(croppedImage);
					    ivCroppedImage.setImage(new Image(new ByteArrayInputStream(bytes)));
					    croppedImageExists = true;
					
					    GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCroppedImage,
					                               tpCroppedImage.getText(),
					                               resources.getString("label.contextMenu.showImage"), false);
				    }
				    else
				    {
					    tpCroppedImage.setActive(true);
					    tpCroppedImage.setCaptured(true);
					    tpCroppedImage.setDuplicated(true);
				    }
				
				    String icaoCode = result.getIcaoErrorMessage();
				    GuiUtils.showNode(lblIcaoMessage, true);
				    boolean icaoSuccess = CaptureFaceResponse.IcaoCodes.SUCCESS.equals(icaoCode);
				    
				    if(icaoSuccess)
				    {
				    	GuiUtils.showNode(ivSuccessIcao, true);
				    	lblIcaoMessage.setText(resources.getString("label.icao.success"));
					
					    tpCroppedImage.setActive(true);
					    tpCroppedImage.setCaptured(true);
					    tpCroppedImage.setValid(true);
					    
					    if(btnStopCameraLivePreview.isVisible()) Platform.runLater(btnStopCameraLivePreview::fire);
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
							    animateHeadRotation(Rotate.X_AXIS, 10.0);
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.YAW_ERROR:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.yaw"));
							    animateHeadRotation(Rotate.Y_AXIS, 25.0);
							    break;
						    }
						    case CaptureFaceResponse.IcaoCodes.ROLL_ERROR:
						    {
							    GuiUtils.showNode(ivWarningIcao, true);
							    lblIcaoMessage.setText(resources.getString("label.icao.roll"));
							    animateHeadRotation(Rotate.Z_AXIS, 10.0);
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
				
				    if(acceptBadQualityFace != null && acceptBadQualityFaceMinRetries != null && acceptBadQualityFace)
				    {
					    btnNext.setDisable(ivErrorIcao.isVisible() || (!icaoSuccess &&
					                       successfulCroppedCapturingCount < acceptBadQualityFaceMinRetries));
				    }
			    }
			    else
			    {
				    lblStatus.setText(String.format(
						    resources.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
						    result.getReturnCode()));
			    }
		    }
		    else
		    {
			    GuiUtils.showNode(btnStopCameraLivePreview, true);
			    GuiUtils.showNode(btnCaptureFace, true);
			    
			    lblStatus.setText(String.format(
					    resources.getString("label.status.failedToCaptureTheFaceWithErrorCode"),
					    taskResponse.getErrorCode()));
			
			    String errorCode = CommonsErrorCodes.C008_00006.getCode();
			    String[] errorDetails = {"failed while capturing the face!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piProgress, false);
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
				lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				lblStatus.setText(resources.getString("label.status.capturingFaceCancelled"));
			}
			else
			{
				lblStatus.setText(resources.getString("label.status.failedToCaptureTheFace"));
				
				String errorCode = CommonsErrorCodes.C008_00007.getCode();
				String[] errorDetails = {"failed while capturing the face!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			}
		});
		
		Context.getExecutorService().submit(task);
	}
	
	private void animateHeadRotation(Point3D axis, double degree)
	{
		if(Platform.isSupported(ConditionalFeature.SCENE3D))
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
			faceAnimationTimeline.setCycleCount(5);
			faceAnimationTimeline.play();
		}
	}
}