package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bcl.utils.CancelCommand;
import sa.gov.nic.bio.biokit.ResponseProcessor;
import sa.gov.nic.bio.biokit.beans.InitializeResponse;
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.beans.StartPreviewResponse;
import sa.gov.nic.bio.biokit.exceptions.AlreadyConnectedException;
import sa.gov.nic.bio.biokit.fingerprint.beans.CaptureFingerprintResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.client.core.beans.FingerprintQualityThreshold;
import sa.gov.nic.bio.bw.client.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.ui.ThreeStateSVGPath;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.ui.ThreeStateTitledPane;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.utils.MofaEnrollmentErrorCodes;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class FingerprintCapturingFxController extends WizardStepFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(FingerprintCapturingFxController.class.getName());
	
	@FXML private AutoScalingStackPane spRightHand;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblStatus;
	@FXML private TitledPane tpRightHand;
	@FXML private TitledPane tpLeftHand;
	@FXML private SplitPane spFingerprints;
	@FXML private ImageView ivFingerprintDeviceLivePreview;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivLeftLittle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftThumb;
	@FXML private ThreeStateSVGPath svgRightHand;
	@FXML private ThreeStateSVGPath svgLeftHand;
	@FXML private ThreeStateSVGPath svgRightLittle;
	@FXML private ThreeStateSVGPath svgRightRing;
	@FXML private ThreeStateSVGPath svgRightMiddle;
	@FXML private ThreeStateSVGPath svgRightIndex;
	@FXML private ThreeStateSVGPath svgRightThumb;
	@FXML private ThreeStateSVGPath svgLeftLittle;
	@FXML private ThreeStateSVGPath svgLeftRing;
	@FXML private ThreeStateSVGPath svgLeftMiddle;
	@FXML private ThreeStateSVGPath svgLeftIndex;
	@FXML private ThreeStateSVGPath svgLeftThumb;
	@FXML private ThreeStateTitledPane tpRightLittle;
	@FXML private ThreeStateTitledPane tpRightRing;
	@FXML private ThreeStateTitledPane tpRightMiddle;
	@FXML private ThreeStateTitledPane tpRightIndex;
	@FXML private ThreeStateTitledPane tpRightThumb;
	@FXML private ThreeStateTitledPane tpLeftLittle;
	@FXML private ThreeStateTitledPane tpLeftRing;
	@FXML private ThreeStateTitledPane tpLeftMiddle;
	@FXML private ThreeStateTitledPane tpLeftIndex;
	@FXML private ThreeStateTitledPane tpLeftThumb;
	@FXML private CheckBox cbRightLittle;
	@FXML private CheckBox cbRightRing;
	@FXML private CheckBox cbRightMiddle;
	@FXML private CheckBox cbRightIndex;
	@FXML private CheckBox cbRightThumb;
	@FXML private CheckBox cbLeftLittle;
	@FXML private CheckBox cbLeftRing;
	@FXML private CheckBox cbLeftMiddle;
	@FXML private CheckBox cbLeftIndex;
	@FXML private CheckBox cbLeftThumb;
	@FXML private Button btnLeftLittle;
	@FXML private Button btnLeftRing;
	@FXML private Button btnLeftMiddle;
	@FXML private Button btnLeftIndex;
	@FXML private Button btnLeftThumb;
	@FXML private Button btnRightThumb;
	@FXML private Button btnRightIndex;
	@FXML private Button btnRightMiddle;
	@FXML private Button btnRightRing;
	@FXML private Button btnRightLittle;
	@FXML private Button btnCancel;
	@FXML private Button btnConnectToDeviceManager;
	@FXML private Button btnDisconnectFromDeviceManager;
	@FXML private Button btnReinitializeDevice;
	@FXML private Button btnStartFingerprintCapturing;
	@FXML private Button btnStopFingerprintCapturing;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	// TODO: add restart biokit button
	// TODO: add start over fingerprint capturing
	
	private String fingerprintDeviceName;
	private Map<Integer, FingerprintQualityThreshold> fingerprintQualityThresholdMap = new HashMap<>();
	private Map<Integer, Fingerprint> capturedFingerprints = new HashMap<>();
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/fingerprintCapturing.fxml");
	}
	
	@Override
	protected void initialize()
	{
		tpLeftHand.prefWidthProperty().bind(spFingerprints.widthProperty().multiply(0.5));
		tpRightHand.prefWidthProperty().bind(spFingerprints.widthProperty().multiply(0.5));
		
		tpRightLittle.disableProperty().bind(cbRightLittle.selectedProperty().not());
		tpRightRing.disableProperty().bind(cbRightRing.selectedProperty().not());
		tpRightMiddle.disableProperty().bind(cbRightMiddle.selectedProperty().not());
		tpRightIndex.disableProperty().bind(cbRightIndex.selectedProperty().not());
		tpRightThumb.disableProperty().bind(cbRightThumb.selectedProperty().not());
		tpLeftLittle.disableProperty().bind(cbLeftLittle.selectedProperty().not());
		tpLeftRing.disableProperty().bind(cbLeftRing.selectedProperty().not());
		tpLeftMiddle.disableProperty().bind(cbLeftMiddle.selectedProperty().not());
		tpLeftIndex.disableProperty().bind(cbLeftIndex.selectedProperty().not());
		tpLeftThumb.disableProperty().bind(cbLeftThumb.selectedProperty().not());
		
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
		
		fingerprintQualityThresholdMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.RIGHT_THUMB));
		fingerprintQualityThresholdMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.RIGHT_INDEX));
		fingerprintQualityThresholdMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.RIGHT_MIDDLE));
		fingerprintQualityThresholdMap.put(FingerPosition.RIGHT_RING.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.RIGHT_RING));
		fingerprintQualityThresholdMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.RIGHT_LITTLE));
		fingerprintQualityThresholdMap.put(FingerPosition.LEFT_THUMB.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.LEFT_THUMB));
		fingerprintQualityThresholdMap.put(FingerPosition.LEFT_INDEX.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.LEFT_INDEX));
		fingerprintQualityThresholdMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.LEFT_MIDDLE));
		fingerprintQualityThresholdMap.put(FingerPosition.LEFT_RING.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.LEFT_RING));
		fingerprintQualityThresholdMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
		                                   new FingerprintQualityThreshold(FingerPosition.LEFT_LITTLE));
	}
	
	@Override
	public void onControllerReady(){}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm) btnConnectToDeviceManager.fire();
	}
	
	@FXML
	private void onConnectToDeviceManagerButtonClicked(ActionEvent event)
	{
		LOGGER.info("connecting to BioKit...");
		
		GuiUtils.showNode(btnConnectToDeviceManager, false);
		GuiUtils.showNode(btnReinitializeDevice, false);
		GuiUtils.showNode(btnStartFingerprintCapturing, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(btnCancel, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.connectingToDeviceManager"));
		
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
				boolean isListening = BclUtils.isLocalhostPortListening(bioKitManager.getWebsocketPort());
				if(cancelCommand.isCanceled()) return null;
				
				if(!isListening)
				{
					LOGGER.info("Bio-Kit is not running! Launching via BCL...");
					int checkEverySeconds = 1000; // make it configurable
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
	private void onDisconnectFromDeviceManagerButtonClicked(ActionEvent event)
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
	private void onReinitializeDeviceButtonClicked(ActionEvent event)
	{
		LOGGER.info("initializing fingerprint device...");
		
		GuiUtils.showNode(btnDisconnectFromDeviceManager, false);
		GuiUtils.showNode(btnReinitializeDevice, false);
		GuiUtils.showNode(btnStartFingerprintCapturing, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(btnCancel, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.initializingDevice"));
		
		// 13 is the position of the right slap
		Future<ServiceResponse<InitializeResponse>> future = Context.getBioKitManager()
																	.getFingerprintService()
																	.initialize(13);
		
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
					LOGGER.info("initialized fingerprint device successfully!");
					GuiUtils.showNode(btnStartFingerprintCapturing, true);
					fingerprintDeviceName = result.getCurrentDeviceName();
					lblStatus.setText(stringsBundle.getString("label.status.DeviceInitializedSuccessfully"));
					
					int position = getNextFingerprintPositionToCapture();
					
					cbRightLittle.setVisible(position == FingerPosition.RIGHT_SLAP.getPosition());
					cbRightRing.setVisible(position == FingerPosition.RIGHT_SLAP.getPosition());
					cbRightMiddle.setVisible(position == FingerPosition.RIGHT_SLAP.getPosition());
					cbRightIndex.setVisible(position == FingerPosition.RIGHT_SLAP.getPosition());
					cbLeftLittle.setVisible(position == FingerPosition.LEFT_SLAP.getPosition());
					cbLeftRing.setVisible(position == FingerPosition.LEFT_SLAP.getPosition());
					cbLeftMiddle.setVisible(position == FingerPosition.LEFT_SLAP.getPosition());
					cbLeftIndex.setVisible(position == FingerPosition.LEFT_SLAP.getPosition());
					cbRightThumb.setVisible(position == FingerPosition.TWO_THUMBS.getPosition());
					cbLeftThumb.setVisible(position == FingerPosition.TWO_THUMBS.getPosition());
					
					tpRightLittle.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					tpRightRing.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					tpRightMiddle.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					tpRightIndex.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					tpLeftLittle.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					tpLeftRing.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					tpLeftMiddle.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					tpLeftIndex.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					tpRightThumb.setActive(position == FingerPosition.TWO_THUMBS.getPosition());
					tpLeftThumb.setActive(position == FingerPosition.TWO_THUMBS.getPosition());
					
					svgRightLittle.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					svgRightRing.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					svgRightMiddle.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					svgRightIndex.setActive(position == FingerPosition.RIGHT_SLAP.getPosition());
					svgLeftLittle.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					svgLeftRing.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					svgLeftMiddle.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					svgLeftIndex.setActive(position == FingerPosition.LEFT_SLAP.getPosition());
					svgRightThumb.setActive(position == FingerPosition.TWO_THUMBS.getPosition());
					svgLeftThumb.setActive(position == FingerPosition.TWO_THUMBS.getPosition());
					
					GuiUtils.showNode(svgRightLittle, position == FingerPosition.RIGHT_SLAP.getPosition());
					GuiUtils.showNode(svgRightRing, position == FingerPosition.RIGHT_SLAP.getPosition());
					GuiUtils.showNode(svgRightMiddle, position == FingerPosition.RIGHT_SLAP.getPosition());
					GuiUtils.showNode(svgRightIndex, position == FingerPosition.RIGHT_SLAP.getPosition());
					GuiUtils.showNode(svgRightThumb, position == FingerPosition.TWO_THUMBS.getPosition());
					GuiUtils.showNode(svgLeftLittle, position == FingerPosition.LEFT_SLAP.getPosition());
					GuiUtils.showNode(svgLeftRing, position == FingerPosition.LEFT_SLAP.getPosition());
					GuiUtils.showNode(svgLeftMiddle, position == FingerPosition.LEFT_SLAP.getPosition());
					GuiUtils.showNode(svgLeftIndex, position == FingerPosition.LEFT_SLAP.getPosition());
					GuiUtils.showNode(svgLeftThumb, position == FingerPosition.TWO_THUMBS.getPosition());
					
					if(position == FingerPosition.RIGHT_SLAP.getPosition() && cbRightIndex.isSelected())
					{
						String message = stringsBundle.getString("label.tooltip.skipFinger");
						
						// if the root pane is still on the scene
						if(coreFxController.getBodyPane().getChildren().contains(rootPane))
						{
							showMessageTooltip(cbRightIndex, message);
						}
					}
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
	private void onStartFingerprintCapturingButtonClicked(ActionEvent event)
	{
		// TODO: determine the fingers
		
		int position = getNextFingerprintPositionToCapture();
		
		LOGGER.info("capturing the fingerprints (position = " + position + ")...");
		
		GuiUtils.showNode(btnStartFingerprintCapturing, false);
		GuiUtils.showNode(btnStopFingerprintCapturing, true);
		
		lblStatus.setText(stringsBundle.getString("label.status.waitingDeviceResponse"));
		boolean[] first = {true};
		
		Task<ServiceResponse<CaptureFingerprintResponse>> task = new Task<ServiceResponse<CaptureFingerprintResponse>>()
		{
			@Override
			protected ServiceResponse<CaptureFingerprintResponse> call() throws Exception
			{
				ResponseProcessor<StartPreviewResponse> responseProcessor = response -> Platform.runLater(() ->
				{
					if(first[0])
					{
						first[0] = false;
						lblStatus.setText(stringsBundle.getString("label.status.capturingFingerprints"));
					}
					
					String previewImageBase64 = response.getPreviewImage();
					byte[] bytes = Base64.getDecoder().decode(previewImageBase64);
					ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				// TODO: TEMP
				int expectedFingersCount = 4;
				List<Integer> missingFingers = new ArrayList<>();
				
				Future<ServiceResponse<CaptureFingerprintResponse>> future = Context.getBioKitManager()
									.getFingerprintService()
									.startPreviewAndAutoCapture(fingerprintDeviceName, position, expectedFingersCount,
									                            missingFingers, responseProcessor);
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
        {
	        ServiceResponse<CaptureFingerprintResponse> serviceResponse = task.getValue();
	        
	        if(serviceResponse.isSuccess())
	        {
		        CaptureFingerprintResponse result = serviceResponse.getResult();
	        	
		        if(result.getReturnCode() == CaptureFingerprintResponse.SuccessCodes.SUCCESS)
		        {
			        if(result.isWrongSlap())
			        {
			        
			        }
			        else
			        {
				        String capturedImageBase64 = result.getCapturedImage();
				        byte[] bytes = Base64.getDecoder().decode(capturedImageBase64);
				        ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
			        	
			        	List<DMFingerData> fingerData = result.getFingerData();
				
				        fingerData.forEach(dmFingerData ->
				        {
					        String fingerprintImageBase64 = dmFingerData.getFinger();
					        byte[] fingerprintImageBytes = Base64.getDecoder().decode(fingerprintImageBase64);
					        boolean acceptableQuality = isAcceptableFingerprint(dmFingerData.getPosition(),
					                                                            dmFingerData.getNfiqQuality(),
					                                                            dmFingerData.getMinutiaeCount(),
					                                                            dmFingerData.getIntensity());
					        
					        switch(dmFingerData.getPosition())
					        {
						        case 2:
						        {
						        	ivRightIndex.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));
						        	attachFingerprintResultTooltip(btnRightIndex, tpRightIndex,
							                                       dmFingerData.getNfiqQuality(),
							                                       dmFingerData.getMinutiaeCount(),
							                                       dmFingerData.getIntensity());
							        tpRightIndex.setCaptured(acceptableQuality);
							        svgRightIndex.setCaptured(acceptableQuality);
						        	tpRightIndex.setValid(acceptableQuality);
						        	svgRightIndex.setValid(acceptableQuality);
						        	GuiUtils.showNode(cbRightIndex, false);
						        	GuiUtils.showNode(btnRightIndex, true);
						        	
						        	String dialogTitle = stringsBundle.getString("label.fingers.index") + " (" +
									                     stringsBundle.getString("label.rightHand") + ")";
							        attachImageDialog(ivRightIndex, dialogTitle);
						        	break;
						        }
						        case 3:
						        {
							        ivRightMiddle.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));
							        attachFingerprintResultTooltip(btnRightMiddle, tpRightMiddle,
							                                       dmFingerData.getNfiqQuality(),
							                                       dmFingerData.getMinutiaeCount(),
							                                       dmFingerData.getIntensity());
							        tpRightMiddle.setCaptured(acceptableQuality);
							        svgRightMiddle.setCaptured(acceptableQuality);
							        tpRightMiddle.setValid(acceptableQuality);
							        svgRightMiddle.setValid(acceptableQuality);
							        GuiUtils.showNode(cbRightMiddle, false);
							        GuiUtils.showNode(btnRightMiddle, true);
							
							        String dialogTitle = stringsBundle.getString("label.fingers.middle") + " (" +
									        stringsBundle.getString("label.rightHand") + ")";
							        attachImageDialog(ivRightMiddle, dialogTitle);
							        break;
						        }
						        case 4:
						        {
							        ivRightRing.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));
							        attachFingerprintResultTooltip(btnRightRing, tpRightRing,
							                                       dmFingerData.getNfiqQuality(),
							                                       dmFingerData.getMinutiaeCount(),
							                                       dmFingerData.getIntensity());
							        tpRightRing.setCaptured(acceptableQuality);
							        svgRightRing.setCaptured(acceptableQuality);
							        tpRightRing.setValid(acceptableQuality);
							        svgRightRing.setValid(acceptableQuality);
							        GuiUtils.showNode(cbRightRing, false);
							        GuiUtils.showNode(btnRightRing, true);
							
							        String dialogTitle = stringsBundle.getString("label.fingers.ring") + " (" +
									        stringsBundle.getString("label.rightHand") + ")";
							        attachImageDialog(ivRightRing, dialogTitle);
							        break;
						        }
						        case 5:
						        {
							        ivRightLittle.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));
							        attachFingerprintResultTooltip(btnRightLittle, tpRightLittle,
							                                       dmFingerData.getNfiqQuality(),
							                                       dmFingerData.getMinutiaeCount(),
							                                       dmFingerData.getIntensity());
							        tpRightLittle.setCaptured(acceptableQuality);
							        svgRightLittle.setCaptured(acceptableQuality);
							        tpRightLittle.setValid(acceptableQuality);
							        svgRightLittle.setValid(acceptableQuality);
							        GuiUtils.showNode(cbRightLittle, false);
							        GuiUtils.showNode(btnRightLittle, true);
							
							        String dialogTitle = stringsBundle.getString("label.fingers.little") + " (" +
									        stringsBundle.getString("label.rightHand") + ")";
							        attachImageDialog(ivRightLittle, dialogTitle);
							        break;
						        }
					        }
				        });
			        }
		        }
		        else
		        {
		        
		        }
	        }
	        else
	        {
	        
	        }
        });
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onStopFingerprintCapturingButtonClicked(ActionEvent event)
	{
	
	}
	
	private void attachFingerprintResultTooltip(Button button, Node targetNode, int nfiq, int minutiaeCount,
	                                            int intensity)
	{
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);
		
		Label lblNfiq = new Label(stringsBundle.getString("label.tooltip.nfiq"));
		Label lblMinutiaeCount = new Label(stringsBundle.getString("label.tooltip.minutiaeCount"));
		Label lblIntensity = new Label(stringsBundle.getString("label.tooltip.intensity"));
		
		Image successImage = new Image(Thread.currentThread()
				                             .getContextClassLoader()
				                             .getResourceAsStream("sa/gov/nic/bio/bw/client/features/" +
						                                                  "mofaenrollment/images/success.png"));
		Image warningImage = new Image(Thread.currentThread()
				                             .getContextClassLoader()
				                             .getResourceAsStream("sa/gov/nic/bio/bw/client/features/" +
						                                                  "mofaenrollment/images/warning.png"));
		lblNfiq.setGraphic(new ImageView(successImage));
		lblMinutiaeCount.setGraphic(new ImageView(successImage));
		lblIntensity.setGraphic(new ImageView(warningImage));
		
		gridPane.add(lblNfiq, 0, 0);
		gridPane.add(lblMinutiaeCount, 0, 1);
		gridPane.add(lblIntensity, 0, 2);
		
		String sNfiq = AppUtils.replaceNumbersOnly(String.valueOf(nfiq), Locale.getDefault());
		String sMinutiaeCount = AppUtils.replaceNumbersOnly(String.valueOf(minutiaeCount), Locale.getDefault());
		String sIntensity = AppUtils.replaceNumbersOnly(String.valueOf(intensity), Locale.getDefault()) + "%";
		
		TextField txtNfiq = new TextField(sNfiq);
		TextField txtMinutiaeCount = new TextField(sMinutiaeCount);
		TextField txtIntensity = new TextField(sIntensity);
		
		txtNfiq.setFocusTraversable(false);
		txtMinutiaeCount.setFocusTraversable(false);
		txtIntensity.setFocusTraversable(false);
		txtNfiq.setEditable(false);
		txtMinutiaeCount.setEditable(false);
		txtIntensity.setEditable(false);
		txtNfiq.setPrefColumnCount(3);
		txtMinutiaeCount.setPrefColumnCount(3);
		txtIntensity.setPrefColumnCount(3);
		
		gridPane.add(txtNfiq, 1, 0);
		gridPane.add(txtMinutiaeCount, 1, 1);
		gridPane.add(txtIntensity, 1, 2);
		
		PopOver popOver = new PopOver(gridPane);
		popOver.setDetachable(false);
		popOver.setConsumeAutoHidingEvents(false);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		
		button.setOnAction(actionEvent ->
		{
			if(popOver.isShowing()) popOver.hide();
			else popOver.show(targetNode);
		});
	}
	
	private void showMessageTooltip(CheckBox checkBox, String message)
	{
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(8.0));
		
		Label lblMessage = new Label(message);
		lblMessage.setTextAlignment(TextAlignment.CENTER);
		lblMessage.setWrapText(true);
		vBox.getChildren().add(lblMessage);
		
		PopOver popOver = new PopOver(vBox);
		popOver.setArrowIndent(5.0);
		popOver.setDetachable(false);
		popOver.setConsumeAutoHidingEvents(false);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		popOver.setAutoHide(true);
		popOver.show(checkBox);
		
		// fix the position
		popOver.setY(popOver.getY() - 7.0);
		popOver.setX(popOver.getX() - (coreFxController.getCurrentLanguage().getNodeOrientation() ==
																		NodeOrientation.RIGHT_TO_LEFT ? 7.0 : 5.0));
		
		// auto-hide after 2 seconds
		PauseTransition pause = new PauseTransition(Duration.seconds(2.0));
		pause.setOnFinished(e -> popOver.hide());
		pause.play();
		
		// catch the mouse click
		checkBox.setOnAction(event -> popOver.hide());
		popOver.setOnHidden(event -> checkBox.setOnAction(null));
		popOver.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent ->
		{
			if(mouseEvent.getButton() == MouseButton.PRIMARY)
			{
				popOver.hide();
				checkBox.fire();
			}
		});
	}
	
	private void attachImageDialog(ImageView imageView, String dialogTitle)
	{
		Runnable runnable = () ->
		{
			ImageView iv = new ImageView(imageView.getImage());
			BorderPane borderPane = new BorderPane();
			borderPane.setCenter(iv);
			Stage stage = DialogUtils.buildCustomDialog(coreFxController.getPrimaryStage(), dialogTitle, borderPane,
			                                            coreFxController.getCurrentLanguage()
					                                           .getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
			stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent ->
			{
				if(keyEvent.getCode() == KeyCode.ESCAPE) stage.close();
			});
			stage.show();
		};
		
		imageView.setOnMouseClicked(mouseEvent ->
		{
			// left-double-click
			if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2)
			{
				runnable.run();
			}
		});
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem = new MenuItem(stringsBundle.getString("label.contextMenu.showImage"));
		menuItem.setOnAction(event -> runnable.run());
		contextMenu.getItems().add(menuItem);
		
		imageView.setOnContextMenuRequested(contextMenuEvent -> contextMenu.show(imageView,
		                                                                         contextMenuEvent.getScreenX(),
		                                                                         contextMenuEvent.getScreenY()));
	}
	
	private int getNextFingerprintPositionToCapture()
	{
		if(!capturedFingerprints.containsKey(FingerPosition.RIGHT_INDEX.getPosition()) ||
				!capturedFingerprints.containsKey(FingerPosition.RIGHT_MIDDLE.getPosition()) ||
				!capturedFingerprints.containsKey(FingerPosition.RIGHT_RING.getPosition()) ||
				!capturedFingerprints.containsKey(FingerPosition.RIGHT_LITTLE.getPosition()) ||
				!capturedFingerprints.get(FingerPosition.RIGHT_INDEX.getPosition()).isAcceptableQuality() ||
				!capturedFingerprints.get(FingerPosition.RIGHT_MIDDLE.getPosition()).isAcceptableQuality() ||
				!capturedFingerprints.get(FingerPosition.RIGHT_RING.getPosition()).isAcceptableQuality() ||
				!capturedFingerprints.get(FingerPosition.RIGHT_LITTLE.getPosition()).isAcceptableQuality())
		{
			return FingerPosition.RIGHT_SLAP.getPosition();
		}
		else if(!capturedFingerprints.containsKey(FingerPosition.LEFT_INDEX.getPosition()) ||
				!capturedFingerprints.containsKey(FingerPosition.LEFT_MIDDLE.getPosition()) ||
				!capturedFingerprints.containsKey(FingerPosition.LEFT_RING.getPosition()) ||
				!capturedFingerprints.containsKey(FingerPosition.LEFT_LITTLE.getPosition()) ||
				!capturedFingerprints.get(FingerPosition.LEFT_INDEX.getPosition()).isAcceptableQuality() ||
				!capturedFingerprints.get(FingerPosition.LEFT_MIDDLE.getPosition()).isAcceptableQuality() ||
				!capturedFingerprints.get(FingerPosition.LEFT_RING.getPosition()).isAcceptableQuality() ||
				!capturedFingerprints.get(FingerPosition.LEFT_LITTLE.getPosition()).isAcceptableQuality())
		{
			return FingerPosition.LEFT_SLAP.getPosition();
			
			
		}
		else if(!capturedFingerprints.containsKey(FingerPosition.RIGHT_THUMB.getPosition()) ||
				!capturedFingerprints.containsKey(FingerPosition.LEFT_THUMB.getPosition()) ||
				!capturedFingerprints.get(FingerPosition.RIGHT_THUMB.getPosition()).isAcceptableQuality() ||
				!capturedFingerprints.get(FingerPosition.LEFT_THUMB.getPosition()).isAcceptableQuality())
		{
			return FingerPosition.TWO_THUMBS.getPosition();
			
		}
		else return -1;
	}
	
	private boolean isAcceptableFingerprint(int fingerPosition, int nfiq, int minutiaeCount, int imageIntensity)
	{
		FingerprintQualityThreshold qualityThreshold = fingerprintQualityThresholdMap.get(fingerPosition);
		return qualityThreshold.getMaximumAcceptableNFIQ() <= nfiq &&
			   qualityThreshold.getMinimumAcceptableMinutiaeCount() >= minutiaeCount &&
			   qualityThreshold.getMinimumAcceptableImageIntensity() >= imageIntensity &&
			   qualityThreshold.getMaximumAcceptableImageIntensity() <= imageIntensity;
	}
}