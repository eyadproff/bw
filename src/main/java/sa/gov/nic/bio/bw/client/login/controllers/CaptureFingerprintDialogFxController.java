package sa.gov.nic.bio.bw.client.login.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sa.gov.nic.bio.biokit.ResponseProcessor;
import sa.gov.nic.bio.biokit.beans.LivePreviewingResponse;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.fingerprint.beans.CaptureFingerprintResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.FingerprintStopPreviewResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.controllers.FxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.login.utils.LoginErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CaptureFingerprintDialogFxController extends FxControllerBase
{
	@FXML private Dialog<ButtonType> dialog;
	@FXML private ImageViewPane paneFingerprintDeviceLivePreview;
	@FXML private ImageView ivFingerprintDeviceLivePreview;
	@FXML private ImageView ivErrorIcon;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblStatus;
	@FXML private ButtonType btCancel;
	
	private FingerPosition fingerPosition;
	private String result;
	
	public String getResult(){return result;}
	
	@Override
	protected void initialize()
	{
		dialog.setOnShown(event ->
		{
			Platform.runLater(() ->
			{
			    Button btnCancel = (Button) dialog.getDialogPane().lookupButton(btCancel);
			    HBox hBox = (HBox) btnCancel.getParent();
			    double distance = hBox.getWidth() / 2.0 - btnCancel.getWidth() / 2.0 - hBox.getPadding().getLeft();
			    btnCancel.translateXProperty().set(-distance);
			});
			
			startCapturingFingerprint();
		});
	}
	
	public void showDialogAndWait()
	{
		dialog.showAndWait();
	}
	
	public void setFingerPosition(FingerPosition fingerPosition)
	{
		this.fingerPosition = fingerPosition;
		
		String fingerText = null;
		
		switch(fingerPosition)
		{
			case RIGHT_THUMB: fingerText = resources.getString("label.rightThumb"); break;
			case RIGHT_INDEX: fingerText = resources.getString("label.rightIndex"); break;
			case RIGHT_MIDDLE: fingerText = resources.getString("label.rightMiddle"); break;
			case RIGHT_RING: fingerText = resources.getString("label.rightRing"); break;
			case RIGHT_LITTLE: fingerText = resources.getString("label.rightLittle"); break;
			case LEFT_THUMB: fingerText = resources.getString("label.leftThumb"); break;
			case LEFT_INDEX: fingerText = resources.getString("label.leftIndex"); break;
			case LEFT_MIDDLE: fingerText = resources.getString("label.leftMiddle"); break;
			case LEFT_RING: fingerText = resources.getString("label.leftRing"); break;
			case LEFT_LITTLE: fingerText = resources.getString("label.leftLittle"); break;
		}
		
		dialog.setHeaderText(String.format(resources.getString("captureFingerprint.message"), fingerText));
	}
	
	private void startCapturingFingerprint()
	{
		LOGGER.info("starting fingerprint capturing...");
		
		boolean[] firstLivePreviewingResponse = {true};
		
		Task<TaskResponse<CaptureFingerprintResponse>> capturingFingerprintTask =
															new Task<TaskResponse<CaptureFingerprintResponse>>()
		{
			@Override
			protected TaskResponse<CaptureFingerprintResponse> call() throws Exception
			{
				// the processor that will process every frame of the live previewing
				ResponseProcessor<LivePreviewingResponse> responseProcessor = response -> Platform.runLater(() ->
				{
				    if(firstLivePreviewingResponse[0])
				    {
				        firstLivePreviewingResponse[0] = false;
				        GuiUtils.showNode(piProgress, false);
					    GuiUtils.showNode(lblStatus, false);
					    GuiUtils.showNode(paneFingerprintDeviceLivePreview, true);
				    }
				
				    String previewImageBase64 = response.getPreviewImage();
				    byte[] bytes = Base64.getDecoder().decode(previewImageBase64);
				    ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				// start the real capturing
				String fingerprintDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																			.getFingerprintScannerDeviceName();
				Future<TaskResponse<CaptureFingerprintResponse>> future = Context.getBioKitManager()
						.getFingerprintService().startPreviewAndAutoCapture(fingerprintDeviceName,
						                                                    fingerPosition.getPosition(),
						                                                    1, null,
						                                                    true, true,
						                                                    responseProcessor);
				return future.get();
			}
		};
		capturingFingerprintTask.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piProgress, false);
			
		    // get the response from the BioKit for the captured fingerprints
		    TaskResponse<CaptureFingerprintResponse> taskResponse = capturingFingerprintTask.getValue();
		
		    if(taskResponse.isSuccess())
		    {
		        CaptureFingerprintResponse result = taskResponse.getResult();
		
		        if(result.getReturnCode() == CaptureFingerprintResponse.SuccessCodes.SUCCESS)
		        {
		            String capturedImageBase64 = result.getCapturedImage();
		            if(capturedImageBase64 == null) return; // it happens if we stop the preview
		
		            // show the final slap image in place of the live preview image
		            byte[] bytes = Base64.getDecoder().decode(capturedImageBase64);
		            ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
		            
			        this.result = result.getCapturedWsq();
			        dialog.close();
		        }
		        else
		        {
		            GuiUtils.showNode(paneFingerprintDeviceLivePreview, false);
		            GuiUtils.showNode(ivErrorIcon, true);
		            GuiUtils.showNode(lblStatus, true);
		            
		            if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.EXCEPTION_WHILE_CAPTURING)
		            {
		                lblStatus.setText(resources.getString("label.status.exceptionWhileCapturing"));
		            }
		            else if(result.getReturnCode() ==
		                    CaptureFingerprintResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
		            {
		                lblStatus.setText(resources.getString("label.status.fingerprintDeviceNotFoundOrUnplugged"));
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
			            lblStatus.setText(resources.getString("label.status.failedToCaptureFinalImage"));
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
		                lblStatus.setText(String.format(firstLivePreviewingResponse[0] ?
                            resources.getString("label.status.failedToStartFingerprintCapturingWithErrorCode") :
                            resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
                            result.getReturnCode()));
		            }
		
		        }
		    }
		    else
		    {
			    GuiUtils.showNode(paneFingerprintDeviceLivePreview, false);
			    GuiUtils.showNode(ivErrorIcon, true);
			    GuiUtils.showNode(lblStatus, true);
		
		        lblStatus.setText(String.format(firstLivePreviewingResponse[0] ?
                            resources.getString("label.status.failedToStartFingerprintCapturingWithErrorCode") :
                            resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
		                                        taskResponse.getErrorCode()));
		
		        String errorCode = firstLivePreviewingResponse[0] ? LoginErrorCodes.C003_00003.getCode() :
				                                                    LoginErrorCodes.C003_00004.getCode();
		        String[] errorDetails = {firstLivePreviewingResponse[0] ?
	                                     "failed while starting the fingerprint capturing!" :
	                                     "failed while capturing the fingerprints!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails);
		    }
		});
		capturingFingerprintTask.setOnFailed(e ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(paneFingerprintDeviceLivePreview, false);
			GuiUtils.showNode(ivErrorIcon, true);
			GuiUtils.showNode(lblStatus, true);
		
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
		        lblStatus.setText(firstLivePreviewingResponse[0] ?
                                  resources.getString("label.status.startingFingerprintCapturingCancelled") :
                                  resources.getString("label.status.capturingFingerprintsCancelled"));
		    }
		    else
		    {
		        lblStatus.setText(firstLivePreviewingResponse[0] ?
                                  resources.getString("label.status.failedToStartFingerprintCapturing") :
                                  resources.getString("label.status.failedToCaptureFingerprints"));
		
		        String errorCode = firstLivePreviewingResponse[0] ? LoginErrorCodes.C003_00005.getCode() :
				                                                    LoginErrorCodes.C003_00006.getCode();
		        String[] errorDetails = {firstLivePreviewingResponse[0] ?
		                                 "failed while starting the fingerprint capturing!" :
		                                 "failed while capturing the fingerprints!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(capturingFingerprintTask);
	}
	
	public void stopCapturingFingerprint()
	{
		LOGGER.info("stopping fingerprint capturing...");
		
		String fingerprintDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																	.getFingerprintScannerDeviceName();
		Future<TaskResponse<FingerprintStopPreviewResponse>> future = Context.getBioKitManager()
									 .getFingerprintService().cancelCapture(fingerprintDeviceName,
				                                                            FingerPosition.RIGHT_SLAP.getPosition());
		
		Task<TaskResponse<FingerprintStopPreviewResponse>> task =
															new Task<TaskResponse<FingerprintStopPreviewResponse>>()
		{
			@Override
			protected TaskResponse<FingerprintStopPreviewResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
		    TaskResponse<FingerprintStopPreviewResponse> taskResponse = task.getValue();
		
		    if(taskResponse.isSuccess())
		    {
		        FingerprintStopPreviewResponse result = taskResponse.getResult();
		
		        if(result.getReturnCode() != FingerprintStopPreviewResponse.SuccessCodes.SUCCESS &&
		           result.getReturnCode() != FingerprintStopPreviewResponse.FailureCodes.NOT_CAPTURING_NOW)
		        {
			        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
			        Context.getCoreFxController().showErrorDialog(String.valueOf(result.getReturnCode()),
			                                                      taskResponse.getException(), errorDetails);
		        }
		    }
		    else
		    {
		        String errorCode = LoginErrorCodes.C003_00007.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
		    Throwable exception = task.getException();
		
		    if(exception instanceof ExecutionException) exception = exception.getCause();
			
			String errorCode = LoginErrorCodes.C003_00008.getCode();
			String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		
		Context.getExecutorService().submit(task);
	}
}