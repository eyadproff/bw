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
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.beans.FingerprintUiComponents;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.ui.FourStateSVGPath;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.ui.FourStateTitledPane;
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
	@FXML private FourStateSVGPath svgRightHand;
	@FXML private FourStateSVGPath svgLeftHand;
	@FXML private FourStateSVGPath svgRightLittle;
	@FXML private FourStateSVGPath svgRightRing;
	@FXML private FourStateSVGPath svgRightMiddle;
	@FXML private FourStateSVGPath svgRightIndex;
	@FXML private FourStateSVGPath svgRightThumb;
	@FXML private FourStateSVGPath svgLeftLittle;
	@FXML private FourStateSVGPath svgLeftRing;
	@FXML private FourStateSVGPath svgLeftMiddle;
	@FXML private FourStateSVGPath svgLeftIndex;
	@FXML private FourStateSVGPath svgLeftThumb;
	@FXML private FourStateTitledPane tpFingerprintDeviceLivePreview;
	@FXML private FourStateTitledPane tpRightLittle;
	@FXML private FourStateTitledPane tpRightRing;
	@FXML private FourStateTitledPane tpRightMiddle;
	@FXML private FourStateTitledPane tpRightIndex;
	@FXML private FourStateTitledPane tpRightThumb;
	@FXML private FourStateTitledPane tpLeftLittle;
	@FXML private FourStateTitledPane tpLeftRing;
	@FXML private FourStateTitledPane tpLeftMiddle;
	@FXML private FourStateTitledPane tpLeftIndex;
	@FXML private FourStateTitledPane tpLeftThumb;
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
	private int currentPosition = FingerPosition.RIGHT_SLAP.getPosition();
	private Map<Integer, Fingerprint> capturedFingerprints = new HashMap<>();
	private Map<Integer, FingerprintQualityThreshold> fingerprintQualityThresholdMap;
	private Map<Integer, FingerprintUiComponents> fingerprintUiComponentsMap = new HashMap<>();
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/fingerprintCapturing.fxml");
	}
	
	@Override
	public void onControllerReady()
	{
		// accumulate all the fingerprint-related components in a map so that we can apply actions on them easily
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
                    new FingerprintUiComponents(FingerPosition.RIGHT_THUMB, FingerPosition.TWO_THUMBS, ivRightThumb,
                                                svgRightThumb, tpRightThumb, cbRightThumb, btnRightThumb,
                                                stringsBundle.getString("label.fingers.thumb"),
                                                stringsBundle.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
                    new FingerprintUiComponents(FingerPosition.RIGHT_INDEX, FingerPosition.RIGHT_SLAP, ivRightIndex,
                                                svgRightIndex, tpRightIndex, cbRightIndex, btnRightIndex,
                                                stringsBundle.getString("label.fingers.index"),
                                                stringsBundle.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
                    new FingerprintUiComponents(FingerPosition.RIGHT_MIDDLE, FingerPosition.RIGHT_SLAP, ivRightMiddle,
                                                svgRightMiddle, tpRightMiddle, cbRightMiddle, btnRightMiddle,
                                                stringsBundle.getString("label.fingers.middle"),
                                                stringsBundle.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_RING.getPosition(),
                    new FingerprintUiComponents(FingerPosition.RIGHT_RING, FingerPosition.RIGHT_SLAP, ivRightRing,
                                                svgRightRing, tpRightRing, cbRightRing, btnRightRing,
                                                stringsBundle.getString("label.fingers.ring"),
                                                stringsBundle.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
                    new FingerprintUiComponents(FingerPosition.RIGHT_LITTLE, FingerPosition.RIGHT_SLAP, ivRightLittle,
                                                svgRightLittle, tpRightLittle, cbRightLittle, btnRightLittle,
                                                stringsBundle.getString("label.fingers.little"),
                                                stringsBundle.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_THUMB.getPosition(),
                    new FingerprintUiComponents(FingerPosition.LEFT_THUMB, FingerPosition.TWO_THUMBS, ivLeftThumb,
                                                svgLeftThumb, tpLeftThumb, cbLeftThumb, btnLeftThumb,
                                                stringsBundle.getString("label.fingers.thumb"),
                                                stringsBundle.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_INDEX.getPosition(),
                    new FingerprintUiComponents(FingerPosition.LEFT_INDEX, FingerPosition.LEFT_SLAP, ivLeftIndex,
                                                svgLeftIndex, tpLeftIndex, cbLeftIndex, btnLeftIndex,
                                                stringsBundle.getString("label.fingers.index"),
                                                stringsBundle.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
                    new FingerprintUiComponents(FingerPosition.LEFT_MIDDLE, FingerPosition.LEFT_SLAP, ivLeftMiddle,
                                                svgLeftMiddle, tpLeftMiddle, cbLeftMiddle, btnLeftMiddle,
                                                stringsBundle.getString("label.fingers.middle"),
                                                stringsBundle.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_RING.getPosition(),
                    new FingerprintUiComponents(FingerPosition.LEFT_RING, FingerPosition.LEFT_SLAP, ivLeftRing,
                                                svgLeftRing, tpLeftRing, cbLeftRing, btnLeftRing,
                                                stringsBundle.getString("label.fingers.ring"),
                                                stringsBundle.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
                    new FingerprintUiComponents(FingerPosition.LEFT_LITTLE, FingerPosition.LEFT_SLAP, ivLeftLittle,
                                                svgLeftLittle, tpLeftLittle, cbLeftLittle, btnLeftLittle,
                                                stringsBundle.getString("label.fingers.little"),
                                                stringsBundle.getString("label.leftHand")));
		
		tpLeftHand.prefWidthProperty().bind(spFingerprints.widthProperty().multiply(0.5));
		tpRightHand.prefWidthProperty().bind(spFingerprints.widthProperty().multiply(0.5));
		
		fingerprintUiComponentsMap.forEach((position, components) ->
               components.getTitledPane().disableProperty().bind(components.getCheckBox().selectedProperty().not()));
		
		fingerprintUiComponentsMap.forEach((position, components) ->
               components.getSvgPath().disableProperty().bind(components.getCheckBox().selectedProperty().not()));
		
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
		
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		Map<Integer, FingerprintQualityThreshold> persistedMap = (Map<Integer, FingerprintQualityThreshold>)
									userSession.getAttribute("lookups.fingerprint.qualityThresholdMap");
		
		if(persistedMap != null) fingerprintQualityThresholdMap = persistedMap;
		else
		{
			fingerprintQualityThresholdMap = new HashMap<>();
			
			fingerprintUiComponentsMap.forEach((position, components) ->
                       fingerprintQualityThresholdMap.put(components.getFingerPosition().getPosition(),
                                                  new FingerprintQualityThreshold(components.getFingerPosition())));
			
			userSession.setAttribute("lookups.fingerprint.qualityThresholdMap", fingerprintQualityThresholdMap);
		}
	}
	
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
					
					int position = currentPosition;
					activateFingerIndicators(position);
					
					if(position == FingerPosition.RIGHT_SLAP.getPosition())
					{
						if(cbRightIndex.isSelected())
						{
							String message = stringsBundle.getString("label.tooltip.skipFinger");
							
							// if the root pane is still on the scene
							if(coreFxController.getBodyPane().getChildren().contains(rootPane))
							{
								showMessageTooltip(cbRightIndex, message);
							}
						}
					}
					
					renameCaptureFingerprintsButton(position, false);
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
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
			boolean currentSlap = currentPosition == components.getSlapPosition().getPosition();
			if(currentSlap)
			{
				components.getTitledPane().setCaptured(false);
				components.getCheckBox().setDisable(components.getCheckBox().isSelected());
				components.getCheckBox().setVisible(components.getCheckBox().isSelected() &&
						                            !components.getButton().isVisible());
			}
			
			components.getSvgPath().setCaptured(false);
			components.getSvgPath().setActive(currentSlap);
			GuiUtils.showNode(components.getSvgPath(), currentSlap);
		});
		
		LOGGER.info("capturing the fingerprints (position = " + currentPosition + ")...");
		
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
						tpFingerprintDeviceLivePreview.setCaptured(false);
						tpFingerprintDeviceLivePreview.setActive(true);
					}
					
					String previewImageBase64 = response.getPreviewImage();
					byte[] bytes = Base64.getDecoder().decode(previewImageBase64);
					ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				List<Integer> missingFingers = new ArrayList<>();
				int expectedFingersCount =
									fillMissingFingersAndCalculateExpectedFingersCount(currentPosition, missingFingers);
				
				if(expectedFingersCount == 0)
				{
					// TODO
				}
				
				Future<ServiceResponse<CaptureFingerprintResponse>> future = Context.getBioKitManager()
									.getFingerprintService()
									.startPreviewAndAutoCapture(fingerprintDeviceName, currentPosition,
									                            expectedFingersCount, missingFingers,
									                            responseProcessor);
				return future.get();
			}
		};
		task.setOnSucceeded(e ->
        {
	        ServiceResponse<CaptureFingerprintResponse> serviceResponse = task.getValue();
	        
	        if(serviceResponse.isSuccess())
	        {
		        GuiUtils.showNode(btnStopFingerprintCapturing, false);
		        CaptureFingerprintResponse result = serviceResponse.getResult();
	        	
		        if(result.getReturnCode() == CaptureFingerprintResponse.SuccessCodes.SUCCESS)
		        {
			        tpFingerprintDeviceLivePreview.setCaptured(true);
		        	
			        if(result.isWrongSlap())
			        {
				        tpFingerprintDeviceLivePreview.setValid(false);
			        }
			        else
			        {
				        tpFingerprintDeviceLivePreview.setValid(true);
			        	
				        String capturedImageBase64 = result.getCapturedImage();
				        byte[] bytes = Base64.getDecoder().decode(capturedImageBase64);
				        ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				        
				        boolean[] allAcceptableQuality = {true};
				        List<DMFingerData> fingerData = result.getFingerData();
				
				        fingerData.forEach(dmFingerData ->
				        {
					        String fingerprintImageBase64 = dmFingerData.getFinger();
					        byte[] fingerprintImageBytes = Base64.getDecoder().decode(fingerprintImageBase64);
					
					        boolean acceptableFingerprintNfiq
						                            = isAcceptableFingerprintNfiq(dmFingerData.getPosition(),
					                                                              dmFingerData.getNfiqQuality());
					        boolean acceptableFingerprintMinutiaeCount
					                          = isAcceptableFingerprintMinutiaeCount(dmFingerData.getPosition(),
							                                                         dmFingerData.getMinutiaeCount());
					
					        boolean acceptableFingerprintImageIntensity
					                            = isAcceptableFingerprintImageIntensity(dmFingerData.getPosition(),
					                                                                    dmFingerData.getIntensity());
					
					        boolean acceptableQuality = acceptableFingerprintNfiq &&
						                                acceptableFingerprintMinutiaeCount &&
							                            acceptableFingerprintImageIntensity;
					
					        allAcceptableQuality[0] = allAcceptableQuality[0] && acceptableQuality;
					
					        fingerprintUiComponentsMap.forEach((position, components) ->
                            {
                            	if(dmFingerData.getPosition() == position)
	                            {
		                            components.getImageView().setImage(new Image(
		                            		                        new ByteArrayInputStream(fingerprintImageBytes)));
		                            attachFingerprintResultTooltip(components.getButton(), components.getTitledPane(),
		                                                           dmFingerData.getNfiqQuality(),
		                                                           dmFingerData.getMinutiaeCount(),
		                                                           dmFingerData.getIntensity(),
		                                                           acceptableFingerprintNfiq,
		                                                           acceptableFingerprintMinutiaeCount,
		                                                           acceptableFingerprintImageIntensity);
		                            components.getTitledPane().setCaptured(true);
		                            components.getTitledPane().setValid(acceptableQuality);
		                            components.getSvgPath().setCaptured(true);
		                            components.getSvgPath().setValid(acceptableQuality);
		                            GuiUtils.showNode(components.getCheckBox(), false);
		                            GuiUtils.showNode(components.getButton(), true);
		                            
		                            String dialogTitle = components.getFingerLabel() + " (" +
				                                                                components.getHandLabel() + ")";
		                            attachImageDialog(components.getImageView(), dialogTitle);
		                            capturedFingerprints.put(position,
		                                                     new Fingerprint(dmFingerData, acceptableQuality));
	                            }
                            });
				        });
				
				        int nextPosition;
				        
				        if(allAcceptableQuality[0])
				        {
					        nextPosition = currentPosition + 1;
				        	
					        if(currentPosition == FingerPosition.RIGHT_SLAP.getPosition())
					        {
						        lblStatus.setText(
						        		stringsBundle.getString("label.status.successfullyCapturedRightSlap"));
						        GuiUtils.showNode(btnStartFingerprintCapturing, true);
					        }
					        else if(currentPosition == FingerPosition.LEFT_SLAP.getPosition())
					        {
						        lblStatus.setText(
								        stringsBundle.getString("label.status.successfullyCapturedLeftSlap"));
						        GuiUtils.showNode(btnStartFingerprintCapturing, true);
					        }
					        else if(currentPosition == FingerPosition.TWO_THUMBS.getPosition())
					        {
						        lblStatus.setText(
								        stringsBundle.getString("label.status.successfullyCapturedAllFingers"));
					        }
					
					        renameCaptureFingerprintsButton(nextPosition, false);
					        currentPosition++;
				        }
				        else
				        {
					        nextPosition = currentPosition;
				        	
				        	lblStatus.setText(
							        stringsBundle.getString("label.status.someFingerprintsAreNotAcceptable"));
					
					        GuiUtils.showNode(btnStartFingerprintCapturing, true);
					        renameCaptureFingerprintsButton(nextPosition, true);
				        }
				        
				        activateFingerIndicators(nextPosition);
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
	
	private void activateFingerIndicators(int currentPosition)
	{
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
			
			if(!capturedFingerprints.containsKey(position) && components.getCheckBox().isSelected())
			{
				boolean currentSlap = currentPosition == components.getSlapPosition().getPosition();
				components.getCheckBox().setVisible(currentSlap);
				components.getTitledPane().setActive(currentSlap);
			}
		});
	}
	
	private void renameCaptureFingerprintsButton(int position, boolean retry)
	{
		String buttonLabel = null;
		
		if(position == FingerPosition.RIGHT_SLAP.getPosition())
		{
			if(retry) buttonLabel = stringsBundle.getString("button.recaptureRightSlapFingerprints");
			else buttonLabel = stringsBundle.getString("button.captureRightSlapFingerprints");
		}
		else if(position == FingerPosition.LEFT_SLAP.getPosition())
		{
			if(retry) buttonLabel = stringsBundle.getString("button.recaptureLeftSlapFingerprints");
			else buttonLabel = stringsBundle.getString("button.captureLeftSlapFingerprints");
		}
		else if(position == FingerPosition.TWO_THUMBS.getPosition())
		{
			if(retry) buttonLabel = stringsBundle.getString("button.recaptureThumbsFingerprints");
			else buttonLabel = stringsBundle.getString("button.captureThumbsFingerprints");
		}
		
		btnStartFingerprintCapturing.setText(buttonLabel);
	}
	
	private void attachFingerprintResultTooltip(Button button, Node targetNode, int nfiq, int minutiaeCount,
	                                            int imageIntensity, boolean acceptableNfiq,
	                                            boolean acceptableMinutiaeCount, boolean acceptableImageIntensity)
	{
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);
		
		Label lblNfiq = new Label(stringsBundle.getString("label.tooltip.nfiq"));
		Label lblMinutiaeCount = new Label(stringsBundle.getString("label.tooltip.minutiaeCount"));
		Label lblIntensity = new Label(stringsBundle.getString("label.tooltip.imageIntensity"));
		
		Image successImage = new Image(Thread.currentThread()
				                             .getContextClassLoader()
				                             .getResourceAsStream("sa/gov/nic/bio/bw/client/features/" +
						                                                  "mofaenrollment/images/success.png"));
		Image warningImage = new Image(Thread.currentThread()
				                             .getContextClassLoader()
				                             .getResourceAsStream("sa/gov/nic/bio/bw/client/features/" +
						                                                  "mofaenrollment/images/warning.png"));
		
		lblNfiq.setGraphic(new ImageView(acceptableNfiq ? successImage : warningImage));
		lblMinutiaeCount.setGraphic(new ImageView(acceptableMinutiaeCount ? successImage : warningImage));
		lblIntensity.setGraphic(new ImageView(acceptableImageIntensity ? successImage : warningImage));
		
		gridPane.add(lblNfiq, 0, 0);
		gridPane.add(lblMinutiaeCount, 0, 1);
		gridPane.add(lblIntensity, 0, 2);
		
		String sNfiq = AppUtils.replaceNumbersOnly(String.valueOf(nfiq), Locale.getDefault());
		String sMinutiaeCount = AppUtils.replaceNumbersOnly(String.valueOf(minutiaeCount), Locale.getDefault());
		String sIntensity = AppUtils.replaceNumbersOnly(String.valueOf(imageIntensity), Locale.getDefault()) + "%";
		
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
	
	private boolean isAcceptableFingerprintNfiq(int fingerPosition, int nfiq)
	{
		FingerprintQualityThreshold qualityThreshold = fingerprintQualityThresholdMap.get(fingerPosition);
		return qualityThreshold.getMaximumAcceptableNFIQ() >= nfiq;
	}
	
	private boolean isAcceptableFingerprintMinutiaeCount(int fingerPosition, int minutiaeCount)
	{
		FingerprintQualityThreshold qualityThreshold = fingerprintQualityThresholdMap.get(fingerPosition);
		return qualityThreshold.getMinimumAcceptableMinutiaeCount() <= minutiaeCount;
	}
	
	private boolean isAcceptableFingerprintImageIntensity(int fingerPosition, int imageIntensity)
	{
		FingerprintQualityThreshold qualityThreshold = fingerprintQualityThresholdMap.get(fingerPosition);
		return qualityThreshold.getMinimumAcceptableImageIntensity() <= imageIntensity &&
			   qualityThreshold.getMaximumAcceptableImageIntensity() >= imageIntensity;
	}
	
	private int fillMissingFingersAndCalculateExpectedFingersCount(int currentPosition, List<Integer> missingFingers)
	{
		int[] expectedFingersCount = {0};
		
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
			if(currentPosition == components.getSlapPosition().getPosition())
			{
				if(components.getCheckBox().isSelected()) expectedFingersCount[0]++;
				else missingFingers.add(components.getFingerPosition().getPosition());
			}
		});
		
		return expectedFingersCount[0];
	}
}