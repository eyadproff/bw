package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sa.gov.nic.bio.bcl.utils.CancelCommand;
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.scanner.beans.ScanResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class ScanFingerprintCardPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_CARD_IMAGE = "CARD_IMAGE";
	
	private static final Logger LOGGER = Logger.getLogger(ScanFingerprintCardPaneFxController.class.getName());
	
	@FXML private ImageView ivFingerprintImage;
	@FXML private ImageView ivFingerprintImagePlaceHolder;
	@FXML private Button btnScanFingerprints;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/scanFingerprintCard.fxml");
	}
	
	@Override
	protected void initialize()
	{
		ivFingerprintImagePlaceHolder.visibleProperty().bind(ivFingerprintImage.imageProperty().isNull());
		ivFingerprintImagePlaceHolder.managedProperty().bind(ivFingerprintImage.imageProperty().isNull());
		btnNext.disableProperty().bind(ivFingerprintImage.imageProperty().isNull());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Image image = (Image) uiInputData.get(KEY_CARD_IMAGE);
			if(image != null)
			{
				ivFingerprintImage.setImage(image);
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintImage,
				                           resources.getString("label.fingerprintCardImage"),
				                           resources.getString("label.contextMenu.showImage"), false);
			}
			
			DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
			
			if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
			{
				boolean devicesRunnerAutoRun = "true".equals(System.getProperty("jnlp.bio.bw.devicesRunner.autoRun"));
				if(devicesRunnerAutoRun) deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
			}
		}
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		uiDataMap.put(KEY_CARD_IMAGE, ivFingerprintImage.getImage());
	}
	
	@FXML
	private void onScanCardButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		ivFingerprintImage.setImage(null);
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.scanningFingerprintsCard");
		
		Future<ServiceResponse<ScanResponse>> future = Context.getBioKitManager().getScannerService().scan();
		Stage dialogStage = DialogUtils.buildProgressDialog(cancelCommand, message, future,
		                                                    resources.getString("button.cancel"));
		
		Task<ServiceResponse<ScanResponse>> task = new Task<ServiceResponse<ScanResponse>>()
		{
			@Override
			protected ServiceResponse<ScanResponse> call() throws Exception
			{
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
		{
			dialogStage.close();
			if(cancelCommand.isCanceled()) return;
			
		    ServiceResponse<ScanResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
		        ScanResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == ScanResponse.SuccessCodes.SUCCESS)
		        {
		            String finalImage = result.getFinalImage();
			        byte[] finalImageBytes = Base64.getDecoder().decode(finalImage);
			        ivFingerprintImage.setImage(new Image(new ByteArrayInputStream(finalImageBytes)));
			        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintImage,
			                                   resources.getString("label.fingerprintCardImage"),
			                                   resources.getString("label.contextMenu.showImage"), false);
			        
			        String successMessage = resources.getString("message.successScan");
			        showSuccessNotification(successMessage);
		        }
		        else if(result.getReturnCode() == ScanResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
		        {
			        String warningMessage = resources.getString("message.scannerNotConnected");
			        showWarningNotification(warningMessage);
		        }
		        else if(result.getReturnCode() == ScanResponse.FailureCodes.FAILED_TO_SCAN_TENPRINT_IMAGE)
		        {
			        String errorMessage = resources.getString("message.failedToScanCard");
			        showErrorNotification(errorMessage);
		        }
		        else
		        {
			        String[] errorDetails = {"failed to scan the ten-print card!"};
			        Context.getCoreFxController().showErrorDialog(String.valueOf(result.getReturnCode()),
			                                                      null, errorDetails);
			        
		        }
		    }
		    else
		    {
		        LOGGER.severe("failed to receive a response for scanning the ten-print card!");
		        String[] errorDetails = {"failed to receive a response for scanning the ten-print card!"};
		        Context.getCoreFxController().showErrorDialog(serviceResponse.getErrorCode(),
		                                                      serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			dialogStage.close();
			
		    Throwable exception = task.getException();
			
			if(exception instanceof NotConnectedException)
			{
				LOGGER.info("the device-runner in not running!");
				String warningMessage = resources.getString("message.devicesRunnerNotRunning");
				showWarningNotification(warningMessage);
			}
		    else if(exception instanceof CancellationException)
		    {
		        LOGGER.info("scanning the ten-print card is cancelled!");
		    }
		    else
		    {
		        LOGGER.severe("failed to scan the ten-print card!");
		
		        String errorCode = FingerprintCardIdentificationErrorCodes.C013_00001.getCode();
		        String[] errorDetails = {"failed to scan the ten-print card!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(task);
		
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
	
	@FXML
	private void onNextButtonClicked(ActionEvent actionEvent)
	{
		goNext();
	}
}