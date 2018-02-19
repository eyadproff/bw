package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
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
import sa.gov.nic.bio.bw.client.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.client.core.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
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
	@FXML private SVGPath svgRightHand;
	@FXML private SVGPath svgLeftHand;
	@FXML private SVGPath svgRightHandLittle;
	@FXML private SVGPath svgRightHandRing;
	@FXML private SVGPath svgRightHandMiddle;
	@FXML private SVGPath svgRightHandIndex;
	@FXML private SVGPath svgRightHandThumb;
	@FXML private SVGPath svgLeftHandLittle;
	@FXML private SVGPath svgLeftHandRing;
	@FXML private SVGPath svgLeftHandMiddle;
	@FXML private SVGPath svgLeftHandIndex;
	@FXML private SVGPath svgLeftHandThumb;
	@FXML private TitledPane tpRightLittle;
	@FXML private TitledPane tpRightRing;
	@FXML private TitledPane tpRightMiddle;
	@FXML private TitledPane tpRightIndex;
	@FXML private TitledPane tpRightThumb;
	@FXML private TitledPane tpLeftLittle;
	@FXML private TitledPane tpLeftRing;
	@FXML private TitledPane tpLeftMiddle;
	@FXML private TitledPane tpLeftIndex;
	@FXML private TitledPane tpLeftThumb;
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
	
	private String fingerprintDeviceName;
	private Map<Integer, String> segmentedFingerTemplateMap = new HashMap<>();
	
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
		
		svgRightHand.setFill(Color.BLACK);
		svgLeftHand.setFill(Color.BLACK);
		
		svgRightHandLittle.setFill(null);
		svgRightHandRing.setFill(null);
		svgRightHandMiddle.setFill(null);
		svgRightHandIndex.setFill(null);
		svgRightHandThumb.setFill(null);
		svgLeftHandLittle.setFill(null);
		svgLeftHandRing.setFill(null);
		svgLeftHandMiddle.setFill(null);
		svgLeftHandIndex.setFill(null);
		svgLeftHandThumb.setFill(null);
		
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
	}
	
	@Override
	public void onControllerReady()
	{
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);
		
		Label lblNfiq = new Label(stringsBundle.getString("label.tooltip.nfiq"));
		Label lblMinutiaeCount = new Label(stringsBundle.getString("label.tooltip.minutiaeCount"));
		Label lblIntensity = new Label(stringsBundle.getString("label.tooltip.intensity"));
		
		Image successImage = new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("sa/gov/nic/bio/bw/client/features/mofaenrollment/images/success.png"));
		Image warningImage = new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("sa/gov/nic/bio/bw/client/features/mofaenrollment/images/warning.png"));
		lblNfiq.setGraphic(new ImageView(successImage));
		lblMinutiaeCount.setGraphic(new ImageView(successImage));
		lblIntensity.setGraphic(new ImageView(warningImage));
		
		gridPane.add(lblNfiq, 0, 0);
		gridPane.add(lblMinutiaeCount, 0, 1);
		gridPane.add(lblIntensity, 0, 2);
		
		String nfiq = AppUtils.replaceNumbersOnly("1", Locale.getDefault());
		String minutiaeCount = AppUtils.replaceNumbersOnly("42", Locale.getDefault());
		String intensity = AppUtils.replaceNumbersOnly("70%", Locale.getDefault());
		
		TextField txtNfiq = new TextField(nfiq);
		TextField txtMinutiaeCount = new TextField(minutiaeCount);
		TextField txtIntensity = new TextField(intensity);
		
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
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		
		GuiUtils.showNode(cbLeftLittle, false);
		GuiUtils.showNode(btnLeftLittle, true);
		
		btnLeftLittle.setOnAction(actionEvent -> popOver.show(tpLeftLittle));
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			btnConnectToDeviceManager.fire();
		}
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
		
		LOGGER.info("capturing the fingerprints...");
		
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
					
					String previewImage = response.getPreviewImage();
					byte[] bytes = Base64.getDecoder().decode(previewImage);
					ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				// TODO: TEMP
				int position = 13;
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
			        	List<DMFingerData> fingerData = result.getFingerData();
				
				
				        String previewImage = fingerData.get(0).getFinger();
				        byte[] bytes = Base64.getDecoder().decode(previewImage);
				
				        Image image = new Image(new ByteArrayInputStream(bytes));
				        ImageView imageView = new ImageView(image);
				        imageView.setPickOnBounds(true);
				        ImageViewPane imageViewPane = new ImageViewPane(imageView);
				
				        Label lblNfiq = new Label("NFIQ: " + fingerData.get(0).getNfiqQuality());
				        Label lblMinutiaeCount = new Label("Minutiae Count: " + fingerData.get(0).getMinutiaeCount());
				        Label lblIntensity = new Label("Intensity: " + fingerData.get(0).getIntensity());
				        VBox vBox = new VBox(lblNfiq, lblMinutiaeCount, lblIntensity);
				        
				        //Create PopOver and add look and feel
				        PopOver popOver = new PopOver(vBox);
				        popOver.setDetachable(false);
				        popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
				        
				        GuiUtils.showNode(cbLeftLittle, false);
				        GuiUtils.showNode(btnLeftLittle, true);
				
				        btnLeftLittle.setOnAction(actionEvent -> popOver.show(imageView));
				
				        tpLeftLittle.setContent(imageViewPane);
				
				        for(DMFingerData dmFingerData : fingerData)
				        {
					        segmentedFingerTemplateMap.put(dmFingerData.getPosition(), dmFingerData.getTemplate());
					
					        
				        }
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
}