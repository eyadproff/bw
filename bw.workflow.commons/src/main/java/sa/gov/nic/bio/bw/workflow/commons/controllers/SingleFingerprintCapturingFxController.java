package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.fingerprint.beans.CaptureFingerprintResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.FingerprintStopPreviewResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.core.beans.FingerprintQualityThreshold;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FingerprintDeviceType;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@FxmlFile("singleFingerprintCapturing.fxml")
public class SingleFingerprintCapturingFxController extends WizardStepFxControllerBase
{
	@Input private Boolean hidePreviousButton;
	@Input private Boolean acceptBadQualityFingerprint;
	@Input private Integer acceptBadQualityFingerprintMinRetires;
	@Output private Fingerprint capturedFingerprint;
	@Output private Finger capturedFingerprintForBackend;
	@Output private String fingerprintBase64Image;
	@Output private FingerPosition selectedFingerprintPosition;
	
	@FXML private VBox paneControlsInnerContainer;
	@FXML private ScrollPane paneControlsOuterContainer;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblStatus;
	@FXML private FourStateTitledPane tpCapturedFingerprint;
	@FXML private ImageView ivSuccess;
	@FXML private ImageView ivCapturedFingerprintPlaceholder;
	@FXML private ImageView ivCapturedFingerprint;
	@FXML private SVGPath svgRightLittle;
	@FXML private SVGPath svgRightRing;
	@FXML private SVGPath svgRightMiddle;
	@FXML private SVGPath svgRightIndex;
	@FXML private SVGPath svgRightThumb;
	@FXML private SVGPath svgLeftLittle;
	@FXML private SVGPath svgLeftRing;
	@FXML private SVGPath svgLeftMiddle;
	@FXML private SVGPath svgLeftIndex;
	@FXML private SVGPath svgLeftThumb;
	
	@FXML private TitledPane tpFingerprintDeviceLivePreview;
	@FXML private Button btnCancel;
	@FXML private Button btnCaptureFingerprint;
	@FXML private Button btnStopFingerprintCapturing;
	@FXML private Button btnChangeFingerprint;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private Map<Integer, FingerprintQualityThreshold> fingerprintQualityThresholdMap;
	private Map<Integer, String[]> fingerprintLabelMap;
	private boolean fingerprintDeviceInitializedAtLeastOnce = false;
	private boolean workflowStarted = false;
	private AtomicBoolean stopCapturingIsInProgress = new AtomicBoolean();
	
	@Override
	protected void onAttachedToScene()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setNextFingerprintDeviceType(FingerprintDeviceType.SINGLE);
		
		btnChangeFingerprint.disableProperty().bind(btnCaptureFingerprint.visibleProperty().not());
		if(acceptBadQualityFingerprint) btnNext.disableProperty().bind(ivCapturedFingerprint.imageProperty().isNull());
		else btnNext.disableProperty().bind(ivSuccess.visibleProperty().not());
		
		paneControlsInnerContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
		{
			paneControlsOuterContainer.requestLayout();
			return paneControlsOuterContainer.getViewportBounds().getHeight();
		}, paneControlsOuterContainer.viewportBoundsProperty()));
		
		if(selectedFingerprintPosition == null) selectedFingerprintPosition = FingerPosition.RIGHT_INDEX;
		activateFingerprint(selectedFingerprintPosition);
		
		// show the image placeholder if and only if there is no image
		ivCapturedFingerprintPlaceholder.visibleProperty().bind(ivCapturedFingerprint.imageProperty().isNull());
		
		fingerprintLabelMap = new HashMap<>();
		fingerprintLabelMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
                                new String[]{resources.getString("label.fingers.thumb"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
                                new String[]{resources.getString("label.fingers.index"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
                                new String[]{resources.getString("label.fingers.middle"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_RING.getPosition(),
                                new String[]{resources.getString("label.fingers.ring"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
                                new String[]{resources.getString("label.fingers.little"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_THUMB.getPosition(),
                                new String[]{resources.getString("label.fingers.thumb"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_INDEX.getPosition(),
                                new String[]{resources.getString("label.fingers.index"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
                                new String[]{resources.getString("label.fingers.middle"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_RING.getPosition(),
                                new String[]{resources.getString("label.fingers.ring"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
                                new String[]{resources.getString("label.fingers.little"),
		                                     resources.getString("label.leftHand")});
		
		// retrieve the quality threshold for each finger from the user session cache, or construct it and cache it
		UserSession userSession = Context.getUserSession();
		@SuppressWarnings("unchecked")
		Map<Integer, FingerprintQualityThreshold> persistedMap = (Map<Integer, FingerprintQualityThreshold>)
											userSession.getAttribute("lookups.fingerprint.qualityThresholdMap");
		
		if(persistedMap != null) fingerprintQualityThresholdMap = persistedMap;
		else
		{
			fingerprintQualityThresholdMap = new HashMap<>();
			for(FingerPosition fingerPosition : FingerPosition.values())
			{
				var position = fingerPosition.getPosition();
				
				if(position >= FingerPosition.RIGHT_THUMB.getPosition() &&
				   position <= FingerPosition.LEFT_LITTLE.getPosition())
				{
					fingerprintQualityThresholdMap.put(position, new FingerprintQualityThreshold(fingerPosition));
				}
			}
			
			userSession.setAttribute("lookups.fingerprint.qualityThresholdMap", fingerprintQualityThresholdMap);
		}
		
		btnNext.disabledProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(!newValue) btnNext.requestFocus();
		});
		
		if(hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
		
		// load the persisted captured fingerprint, if any
		if(capturedFingerprint != null)
		{
			workflowStarted = true;
			
			var position = capturedFingerprint.getDmFingerData().getPosition();
			var labels = fingerprintLabelMap.get(position);
			
			showFingerprint(capturedFingerprint, ivCapturedFingerprint, tpCapturedFingerprint, labels[1], labels[0]);
			
			GuiUtils.showNode(lblStatus, true);
			
			if(capturedFingerprint.isAcceptableQuality())
			{
				GuiUtils.showNode(ivSuccess, true);
				lblStatus.setText(resources.getString("label.status.successfullyCapturedTheFingerprint"));
			}
			else lblStatus.setText(resources.getString("label.status.theCapturedFingerprintHasBadQuality"));
			
			btnCaptureFingerprint.setText(resources.getString("button.recaptureTheFingerprint"));
		}
		
		// register a listener to the event of the devices-runner being running or not
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
		{
		    if(running &&
		            !deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.SINGLE))
		    {
		        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SINGLE);
		    }
		});
		
		// register a listener to the event of the fingerprint device being initialized or disconnected
		deviceManagerGadgetPaneController.setFingerprintScannerInitializationListener(initialized -> Platform.runLater(() ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStopFingerprintCapturing, false);
		
		    if(initialized)
		    {
		    	if(capturedFingerprint != null)
			    {
				    btnCaptureFingerprint.setText(resources.getString("button.recaptureTheFingerprint"));
			    }
		    	else btnCaptureFingerprint.setText(resources.getString("button.captureTheFingerprint"));
		    	
		        GuiUtils.showNode(btnCaptureFingerprint, true);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.fingerprintScannerInitializedSuccessfully"));
		        fingerprintDeviceInitializedAtLeastOnce = true;
		        LOGGER.info("The fingerprint scanner is initialized!");
		    }
		    else if(fingerprintDeviceInitializedAtLeastOnce)
		    {
		        GuiUtils.showNode(btnCaptureFingerprint, false);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.fingerprintScannerDisconnected"));
		        LOGGER.info("The fingerprint scanner is disconnected!");
		    }
		    else
		    {
			    GuiUtils.showNode(btnCaptureFingerprint, false);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.fingerprintScannerNotInitialized"));
		    }
		}));
		
		// prepare for next fingerprint capturing if the fingerprint device is connected and initialized, otherwise
		// auto-run and auto-initialize as configured
		if(deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.SINGLE))
		{
			GuiUtils.showNode(btnCaptureFingerprint, true);
			
			if(capturedFingerprint != null)
			{
				btnCaptureFingerprint.setText(resources.getString("button.recaptureTheFingerprint"));
			}
			else btnCaptureFingerprint.setText(resources.getString("button.captureTheFingerprint"));
		}
		else if(deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SINGLE);
		}
		else
		{
			GuiUtils.showNode(btnCaptureFingerprint, false);
			GuiUtils.showNode(lblStatus, true);
			lblStatus.setText(resources.getString("label.status.fingerprintScannerNotInitialized"));
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
	}
	
	@Override
	protected void onDetachingFromScene()
	{
		Platform.runLater(btnStopFingerprintCapturing::fire);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().setDevicesRunnerRunningListener(null);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController()
									 .setFingerprintScannerInitializationListener(null);
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(!workflowStarted) return;
		
		if(btnStopFingerprintCapturing.isVisible() || piProgress.isVisible())
		{
			Platform.runLater(btnStopFingerprintCapturing::fire);
		}
		
		if(capturedFingerprint != null)
		{
			DMFingerData fingerData = capturedFingerprint.getDmFingerData();
			fingerprintBase64Image = fingerData.getFinger();
			capturedFingerprintForBackend = new Finger(fingerData.getPosition(), fingerData.getFingerWsqImage(),
			                                           null);
		}
	}
	
	@FXML
	private void onCaptureFingerprintButtonClicked(ActionEvent event)
	{
		ivCapturedFingerprint.setImage(null);
		tpCapturedFingerprint.setActive(false);
		tpCapturedFingerprint.setCaptured(false);
		tpCapturedFingerprint.setValid(false);
		capturedFingerprint = null;
		
		workflowStarted = true;
		GuiUtils.showNode(piProgress, false);
		GuiUtils.showNode(ivSuccess, false);
		GuiUtils.showNode(btnCaptureFingerprint, false);
		GuiUtils.showNode(lblStatus, true);
		GuiUtils.showNode(btnStopFingerprintCapturing, true);
		lblStatus.setText(resources.getString("label.status.capturingTheFingerprint"));
		LOGGER.info("capturing the fingerprint (position = " + selectedFingerprintPosition + ")...");
		
		Task<TaskResponse<CaptureFingerprintResponse>> capturingFingerprintTask = new Task<>()
		{
			@Override
			protected TaskResponse<CaptureFingerprintResponse> call() throws Exception
			{
				// start the real capturing
				String fingerprintDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																			.getFingerprintScannerDeviceName();
				Future<TaskResponse<CaptureFingerprintResponse>> future = Context.getBioKitManager()
						.getFingerprintService().startPreviewAndAutoCapture(fingerprintDeviceName,
						                                                    selectedFingerprintPosition.getPosition(),
						                                                    1, null, true,
						                                                    true, null);
				return future.get();
			}
		};
		capturingFingerprintTask.setOnSucceeded(e ->
        {
	        GuiUtils.showNode(piProgress, false);
        	GuiUtils.showNode(btnStopFingerprintCapturing, false);
        	GuiUtils.showNode(btnCaptureFingerprint, true);
        	GuiUtils.showNode(lblStatus, true);
	        btnCaptureFingerprint.setText(resources.getString("button.recaptureTheFingerprint"));
	        
	        // get the response from the BioKit for the captured fingerprints
	        TaskResponse<CaptureFingerprintResponse> taskResponse = capturingFingerprintTask.getValue();
	        
	        if(taskResponse.isSuccess())
	        {
		        CaptureFingerprintResponse result = taskResponse.getResult();
	        	
		        if(result.getReturnCode() == CaptureFingerprintResponse.SuccessCodes.SUCCESS)
		        {
			        String capturedImageBase64 = result.getCapturedImage();
			        if(capturedImageBase64 == null) return; // it happens if we stop the preview
			
			        var labels = fingerprintLabelMap.get(selectedFingerprintPosition.getPosition());
			        var dmFingerData = result.getFingerData().get(0);
			
			        FingerprintQualityThreshold qualityThreshold = fingerprintQualityThresholdMap.get(
					        dmFingerData.getPosition());
			        boolean acceptableFingerprintNfiq = qualityThreshold.getMaximumAcceptableNFIQ() >=
					        dmFingerData.getNfiqQuality();
			
			        boolean acceptableFingerprintMinutiaeCount = qualityThreshold.getMinimumAcceptableMinutiaeCount() <=
					        dmFingerData.getMinutiaeCount();
			
			        boolean acceptableFingerprintImageIntensity = qualityThreshold.getMinimumAcceptableImageIntensity() <=
					        dmFingerData.getIntensity() &&
					        qualityThreshold.getMaximumAcceptableImageIntensity() >=
							        dmFingerData.getIntensity();
			
			        boolean acceptableQuality = acceptableFingerprintNfiq && acceptableFingerprintMinutiaeCount &&
					        acceptableFingerprintImageIntensity;
			
			        capturedFingerprint = new Fingerprint(dmFingerData, result.getCapturedWsq(), capturedImageBase64);
			        capturedFingerprint.setAcceptableFingerprintNfiq(acceptableFingerprintNfiq);
			        capturedFingerprint.setAcceptableFingerprintMinutiaeCount(acceptableFingerprintMinutiaeCount);
			        capturedFingerprint.setAcceptableFingerprintImageIntensity(acceptableFingerprintImageIntensity);
			        capturedFingerprint.setAcceptableQuality(acceptableQuality);
			
			        showFingerprint(capturedFingerprint, ivCapturedFingerprint, tpCapturedFingerprint, labels[1], labels[0]);
			        
			        GuiUtils.showNode(ivSuccess, acceptableQuality);
			        if(acceptableQuality)
			        {
			        	lblStatus.setText(resources.getString("label.status.successfullyCapturedTheFingerprint"));
			        }
			        else lblStatus.setText(resources.getString("label.status.theCapturedFingerprintHasBadQuality"));
		        }
		        // we skip FAILED_TO_CAPTURE_FINAL_IMAGE because it happens only when we send "stop" command
		        else
		        {
			        GuiUtils.showNode(piProgress, false);
			        GuiUtils.showNode(btnCaptureFingerprint, true);
			        
			        if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.EXCEPTION_WHILE_CAPTURING)
			        {
				        lblStatus.setText(resources.getString("label.status.exceptionWhileCapturing"));
			        }
			        else if(result.getReturnCode() ==
					                            CaptureFingerprintResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
			        {
				        lblStatus.setText(resources.getString("label.status.fingerprintDeviceNotFoundOrUnplugged"));
				
				        GuiUtils.showNode(btnCaptureFingerprint, false);
				        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
						                        Context.getCoreFxController().getDeviceManagerGadgetPaneController();
				        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SINGLE);
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.DEVICE_BUSY)
			        {
				        lblStatus.setText(resources.getString("label.status.fingerprintDeviceBusy"));
			        }
			        else if(result.getReturnCode() ==
					                        CaptureFingerprintResponse.FailureCodes.WRONG_NUMBER_OF_EXPECTED_FINGERS)
			        {
				        lblStatus.setText(resources.getString("label.status.wrongNumberOfExpectedFingers"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.SEGMENTATION_FAILED)
			        {
				        lblStatus.setText(resources.getString("label.status.segmentationFailed"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.WSQ_CONVERSION_FAILED)
			        {
				        lblStatus.setText(resources.getString("label.status.wsqConversionFailed"));
			        }
			        else if(result.getReturnCode() ==
				                                CaptureFingerprintResponse.FailureCodes.FAILED_TO_CAPTURE_FINAL_IMAGE)
			        {
				        if(!stopCapturingIsInProgress.get())
				        {
					        lblStatus.setText(resources.getString("label.status.failedToCaptureFinalImage"));
					
					        GuiUtils.showNode(btnCaptureFingerprint, false);
					        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
							        Context.getCoreFxController().getDeviceManagerGadgetPaneController();
					        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SINGLE);
				        }
			        }
			        else if(result.getReturnCode() ==
					                        CaptureFingerprintResponse.FailureCodes.EXCEPTION_IN_FINGER_HANDLER_CAPTURE)
			        {
				        lblStatus.setText(resources.getString("label.status.exceptionInFingerHandlerCapture"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.POOR_IMAGE_QUALITY)
			        {
				        lblStatus.setText(resources.getString("label.status.poorImageQuality"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.SENSOR_IS_DIRTY)
			        {
				        lblStatus.setText(resources.getString("label.status.sensorIsDirty"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.CAPTURE_TIMEOUT)
			        {
				        lblStatus.setText(resources.getString("label.status.captureTimeout"));
			        }
			        else
			        {
				        lblStatus.setText(String.format(
			                resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
                            result.getReturnCode()));
			        }
		        }
	        }
	        else
	        {
		        GuiUtils.showNode(piProgress, false);
		        GuiUtils.showNode(btnCaptureFingerprint, true);
		        
		        lblStatus.setText(String.format(
				        resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
		                                        taskResponse.getErrorCode()));
		
		        String errorCode = CommonsErrorCodes.C008_00051.getCode();
		        String[] errorDetails = {"failed while capturing the fingerprint!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
	        }
	
	        stopCapturingIsInProgress.set(false);
        });
		capturingFingerprintTask.setOnFailed(e ->
		{
			stopCapturingIsInProgress.set(false);
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnStopFingerprintCapturing, false);
			GuiUtils.showNode(btnCaptureFingerprint, true);
			btnCaptureFingerprint.setText(resources.getString("button.recaptureTheFingerprint"));
			
			Throwable exception = capturingFingerprintTask.getException();
			
			if(exception instanceof ExecutionException) exception = exception.getCause();
			
			if(exception instanceof TimeoutException)
			{
				lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				lblStatus.setText(resources.getString("label.status.capturingFingerprintsCancelled"));
			}
			else
			{
				lblStatus.setText(resources.getString("label.status.failedToCaptureFingerprints"));
				
				String errorCode = CommonsErrorCodes.C008_00052.getCode();
				String[] errorDetails = {"failed while capturing the fingerprint!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			}
		});
		
		Context.getExecutorService().submit(capturingFingerprintTask);
	}
	
	@FXML
	private void onStopFingerprintCapturingButtonClicked(ActionEvent event)
	{
		LOGGER.info("stopping fingerprint capturing...");
		
		GuiUtils.showNode(btnStopFingerprintCapturing, false);
		GuiUtils.showNode(piProgress, true);
		lblStatus.setText(resources.getString("label.status.stoppingTheFingerprintCapturing"));
		
		String fingerprintDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																	.getFingerprintScannerDeviceName();
		Future<TaskResponse<FingerprintStopPreviewResponse>> future = Context.getBioKitManager()
									.getFingerprintService().cancelCapture(fingerprintDeviceName,
															               selectedFingerprintPosition.getPosition());
		
		Task<TaskResponse<FingerprintStopPreviewResponse>> task = new Task<>()
		{
			@Override
			protected TaskResponse<FingerprintStopPreviewResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnCaptureFingerprint, true);
			
		    TaskResponse<FingerprintStopPreviewResponse> taskResponse = task.getValue();
		
		    if(taskResponse.isSuccess())
		    {
			    FingerprintStopPreviewResponse result = taskResponse.getResult();
			
			    if(result.getReturnCode() == FingerprintStopPreviewResponse.SuccessCodes.SUCCESS ||
				   result.getReturnCode() == FingerprintStopPreviewResponse.FailureCodes.NOT_CAPTURING_NOW)
		        {
		            lblStatus.setText(resources.getString("label.status.successfullyStoppedFingerprintCapturing"));
		        }
		        else
		        {
		            lblStatus.setText(String.format(
		                    resources.getString("label.status.failedToStopFingerprintCapturingWithErrorCode"),
		                    result.getReturnCode()));
		        }
		    }
		    else
		    {
		        lblStatus.setText(String.format(
				        resources.getString("label.status.failedToStopFingerprintCapturingWithErrorCode"),
				        taskResponse.getErrorCode()));
		
		        String errorCode = CommonsErrorCodes.C008_00053.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnCaptureFingerprint, true);
			
			Throwable exception = task.getException();
		
		    if(exception instanceof ExecutionException) exception = exception.getCause();
		
		    if(exception instanceof TimeoutException)
		    {
		        lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
		    }
		    else if(exception instanceof NotConnectedException)
		    {
		        lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
		    }
		    else if(exception instanceof CancellationException)
		    {
		        lblStatus.setText(resources.getString("label.status.stoppingFingerprintCapturingCancelled"));
		    }
		    else
		    {
		        lblStatus.setText(resources.getString("label.status.failedToStopFingerprintCapturing"));
		
		        String errorCode = CommonsErrorCodes.C008_00054.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		    }
		});
		
		stopCapturingIsInProgress.set(true);
		Context.getExecutorService().submit(task);
	}
	
	private void showFingerprint(Fingerprint fingerprint, ImageView imageView, FourStateTitledPane titledPane,
	                             String handLabel, String fingerLabel)
	{
		String fingerprintImageBase64 = fingerprint.getSlapImage();
		byte[] fingerprintImageBytes = Base64.getDecoder().decode(fingerprintImageBase64);
		imageView.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));
		
		titledPane.setActive(true);
		titledPane.setCaptured(true);
		titledPane.setValid(fingerprint.isAcceptableQuality());
		titledPane.setDuplicated(fingerprint.isDuplicated());
		
		StackPane titleRegion = (StackPane) titledPane.lookup(".title");
		attachFingerprintResultTooltip(titleRegion, titledPane,
		                               fingerprint.getDmFingerData().getNfiqQuality(),
		                               fingerprint.getDmFingerData().getMinutiaeCount(),
		                               fingerprint.getDmFingerData().getIntensity(),
		                               fingerprint.isAcceptableFingerprintNfiq(),
		                               fingerprint.isAcceptableFingerprintMinutiaeCount(),
		                               fingerprint.isAcceptableFingerprintImageIntensity(),
		                               fingerprint.isDuplicated());
		
		String dialogTitle = fingerLabel + " (" + handLabel + ")";
		GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, dialogTitle,
		                           resources.getString("label.contextMenu.showImage"), false);
	}
	
	private void attachFingerprintResultTooltip(Node sourceNode, Node targetNode, int nfiq, int minutiaeCount,
	                                            int imageIntensity, boolean acceptableNfiq,
	                                            boolean acceptableMinutiaeCount, boolean acceptableImageIntensity,
	                                            boolean duplicated)
	{
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10.0));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);
		
		Label lblNfiq = new Label(resources.getString("label.tooltip.nfiq"));
		Label lblMinutiaeCount = new Label(resources.getString("label.tooltip.minutiaeCount"));
		Label lblIntensity = new Label(resources.getString("label.tooltip.imageIntensity"));
		Label lblDuplicatedFingerprint = new Label(resources.getString("label.tooltip.duplicatedFinger"));
		
		Image successImage = new Image(CommonImages.ICON_SUCCESS_16PX.getAsInputStream());
		Image warningImage = new Image(CommonImages.ICON_WARNING_16PX.getAsInputStream());
		Image errorImage = new Image(CommonImages.ICON_ERROR_16PX.getAsInputStream());
		
		lblNfiq.setGraphic(new ImageView(acceptableNfiq ? successImage : warningImage));
		lblMinutiaeCount.setGraphic(new ImageView(acceptableMinutiaeCount ? successImage : warningImage));
		lblIntensity.setGraphic(new ImageView(acceptableImageIntensity ? successImage : warningImage));
		lblDuplicatedFingerprint.setGraphic(new ImageView(duplicated ? errorImage : successImage));
		
		gridPane.add(lblNfiq, 0, 0);
		gridPane.add(lblMinutiaeCount, 0, 1);
		gridPane.add(lblIntensity, 0, 2);
		gridPane.add(lblDuplicatedFingerprint, 0, 3);
		
		String sNfiq = AppUtils.localizeNumbers(String.valueOf(nfiq));
		String sMinutiaeCount = AppUtils.localizeNumbers(String.valueOf(minutiaeCount));
		String sIntensity = AppUtils.localizeNumbers(String.valueOf(imageIntensity)) + "%";
		String sDuplicatedFingerprint = resources.getString(duplicated ? "label.tooltip.yes" : "label.tooltip.no");
		
		TextField txtNfiq = new TextField(sNfiq);
		TextField txtMinutiaeCount = new TextField(sMinutiaeCount);
		TextField txtIntensity = new TextField(sIntensity);
		TextField txtDuplicatedFingerprint = new TextField(sDuplicatedFingerprint);
		
		txtNfiq.setFocusTraversable(false);
		txtMinutiaeCount.setFocusTraversable(false);
		txtIntensity.setFocusTraversable(false);
		txtDuplicatedFingerprint.setFocusTraversable(false);
		txtNfiq.setEditable(false);
		txtMinutiaeCount.setEditable(false);
		txtIntensity.setEditable(false);
		txtDuplicatedFingerprint.setEditable(false);
		txtNfiq.setPrefColumnCount(3);
		txtMinutiaeCount.setPrefColumnCount(3);
		txtIntensity.setPrefColumnCount(3);
		txtDuplicatedFingerprint.setPrefColumnCount(3);
		
		gridPane.add(txtNfiq, 1, 0);
		gridPane.add(txtMinutiaeCount, 1, 1);
		gridPane.add(txtIntensity, 1, 2);
		gridPane.add(txtDuplicatedFingerprint, 1, 3);
		
		PopOver popOver = new PopOver(gridPane);
		popOver.setDetachable(false);
		popOver.setConsumeAutoHidingEvents(false);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		
		sourceNode.setOnMouseClicked(mouseEvent ->
		{
			if(popOver.isShowing()) popOver.hide();
			else popOver.show(targetNode);
		});
	}
	
	@FXML
	private void onChangeFingerprintButtonClicked(ActionEvent actionEvent)
	{
		try
		{
			ChangeFingerprintDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), ChangeFingerprintDialogFxController.class,
					false);
			
			if(controller != null)
			{
				controller.setCurrentFingerPosition(selectedFingerprintPosition);
				boolean confirmed = controller.showDialogAndWait();
				
				if(confirmed)
				{
					FingerPosition fingerPosition = controller.getCurrentFingerPosition();
					if(fingerPosition != null) activateFingerprint(fingerPosition);
				}
			}
		}
		catch(Exception e)
		{
			String errorCode = CommonsErrorCodes.C008_00055.getCode();
			String[] errorDetails = {"Failed to load (" + ChangeFingerprintDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
		}
	}
	
	private void activateFingerprint(FingerPosition fingerPosition)
	{
		selectedFingerprintPosition = fingerPosition;
		
		GuiUtils.showNode(svgLeftLittle, fingerPosition == FingerPosition.LEFT_LITTLE);
		GuiUtils.showNode(svgLeftRing, fingerPosition == FingerPosition.LEFT_RING);
		GuiUtils.showNode(svgLeftMiddle, fingerPosition == FingerPosition.LEFT_MIDDLE);
		GuiUtils.showNode(svgLeftIndex, fingerPosition == FingerPosition.LEFT_INDEX);
		GuiUtils.showNode(svgLeftThumb, fingerPosition == FingerPosition.LEFT_THUMB);
		GuiUtils.showNode(svgRightLittle, fingerPosition == FingerPosition.RIGHT_LITTLE);
		GuiUtils.showNode(svgRightRing, fingerPosition == FingerPosition.RIGHT_RING);
		GuiUtils.showNode(svgRightMiddle, fingerPosition == FingerPosition.RIGHT_MIDDLE);
		GuiUtils.showNode(svgRightIndex, fingerPosition == FingerPosition.RIGHT_INDEX);
		GuiUtils.showNode(svgRightThumb, fingerPosition == FingerPosition.RIGHT_THUMB);
	}
}