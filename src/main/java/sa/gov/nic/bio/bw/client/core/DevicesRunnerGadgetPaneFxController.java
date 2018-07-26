package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bcl.utils.CancelCommand;
import sa.gov.nic.bio.biokit.beans.InitializeResponse;
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.beans.ShutdownResponse;
import sa.gov.nic.bio.biokit.exceptions.AlreadyConnectedException;
import sa.gov.nic.bio.biokit.websocket.ClosureListener;
import sa.gov.nic.bio.bw.client.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.features.commons.utils.CommonsErrorCodes;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * JavaFX controller for the devices runner gadget. It is shown after login.
 *
 * @author Fouad Almalki
 */
public class DevicesRunnerGadgetPaneFxController extends RegionFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(DevicesRunnerGadgetPaneFxController.class.getName());
	
	enum DeviceStatus
	{
		INITIALIZED,
		NOT_INITIALIZED,
		NOT_CONNECTED
	}
	
	@FXML private TitledPane tpDevicesRunner;
	@FXML private TitledPane tpFingerprintScanner;
	@FXML private TitledPane tpCamera;
	@FXML private TitledPane tpPassportScanner;
	@FXML private Pane paneFingerprintScanner;
	@FXML private Pane paneCamera;
	@FXML private Pane panePassportScanner;
	@FXML private ProgressIndicator piFingerprintScanner;
	@FXML private ProgressIndicator piCamera;
	@FXML private ProgressIndicator piPassportScanner;
	@FXML private Label lblDevicesRunnerNotWorking;
	@FXML private Label lblDevicesRunnerWorking;
	@FXML private Label lblFingerprintScannerNotInitialized;
	@FXML private Label lblFingerprintScannerNotConnected;
	@FXML private Label lblFingerprintScannerInitialized;
	@FXML private Label lblCameraNotInitialized;
	@FXML private Label lblCameraNotConnected;
	@FXML private Label lblCameraInitialized;
	@FXML private Label lblPassportScannerNotInitialized;
	@FXML private Label lblPassportScannerNotConnected;
	@FXML private Label lblPassportScannerInitialized;
	@FXML private Button btnDevicesRunnerAction;
	@FXML private Button btnFingerprintScannerAction;
	@FXML private Button btnCameraAction;
	@FXML private Button btnPassportScannerAction;
	
	private ContextMenu contextMenu;
	private String fingerprintScannerDeviceName;
	private String cameraDeviceName;
	private String passportScannerDeviceName;
	private Consumer<Boolean> devicesRunnerRunningListener;
	private Consumer<Boolean> fingerprintScannerInitializationListener;
	private Consumer<Boolean> cameraInitializationListener;
	private Consumer<Boolean> passportScannerInitializationListener;
	
	private ClosureListener closureListener = closeReason -> Platform.runLater(() ->
                                                                            changeDevicesRunnerStatus(false));
	
	public ClosureListener getClosureListener(){return closureListener;}
	public String getFingerprintScannerDeviceName(){return fingerprintScannerDeviceName;}
	public String getCameraDeviceName(){return cameraDeviceName;}
	public String getPassportScannerDeviceName(){return passportScannerDeviceName;}
	
	public boolean isDevicesRunnerRunning()
	{
		return lblDevicesRunnerWorking.isVisible();
	}
	
	public boolean isFingerprintScannerInitialized()
	{
		return lblFingerprintScannerInitialized.isVisible();
	}
	
	public boolean isCameraInitialized()
	{
		return lblCameraInitialized.isVisible();
	}
	
	public boolean isPassportScannerInitialized()
	{
		return lblPassportScannerInitialized.isVisible();
	}
	
	public void setDevicesRunnerRunningListener(Consumer<Boolean> devicesRunnerRunningListener)
	{
		this.devicesRunnerRunningListener = devicesRunnerRunningListener;
	}
	
	public void setFingerprintScannerInitializationListener(Consumer<Boolean> fingerprintScannerInitializationListener)
	{
		this.fingerprintScannerInitializationListener = fingerprintScannerInitializationListener;
	}
	
	public void setCameraInitializationListener(Consumer<Boolean> cameraInitializationListener)
	{
		this.cameraInitializationListener = cameraInitializationListener;
	}
	
	public void setPassportScannerInitializationListener(Consumer<Boolean> passportScannerInitializationListener)
	{
		this.passportScannerInitializationListener = passportScannerInitializationListener;
	}
	
	public Label getFingerprintScannerNotInitializedLabel(){return lblFingerprintScannerNotInitialized;}
	public Label getFingerprintScannerNotConnectedLabel(){return lblFingerprintScannerNotConnected;}
	public Label getFingerprintScannerInitializedLabel(){return lblFingerprintScannerInitialized;}
	
	public void disableCollapsing(boolean bDisable)
	{
		tpDevicesRunner.setAnimated(!bDisable);
		tpDevicesRunner.setExpanded(true);
		tpDevicesRunner.setCollapsible(!bDisable);
	}
	
	public void showOnlyFingerprintScannerControl(boolean bool)
	{
		GuiUtils.showNode(tpCamera, !bool);
		GuiUtils.showNode(tpPassportScanner, !bool);
	}
	
	@Override
	protected void initialize()
	{
		contextMenu = new ContextMenu();
		
		Glyph devicesIcon = AppUtils.createFontAwesomeIcon('\uf2db');
		tpDevicesRunner.setGraphic(devicesIcon);
		
		Glyph fingerprintScannerIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.CUBE);
		tpFingerprintScanner.setGraphic(fingerprintScannerIcon);
		
		Glyph cameraIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.CAMERA);
		tpCamera.setGraphic(cameraIcon);
		
		Glyph passportScannerIcon = AppUtils.createFontAwesomeIcon('\uf2c2');
		tpPassportScanner.setGraphic(passportScannerIcon);
		
		Glyph gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
		btnDevicesRunnerAction.setGraphic(gearIcon);
		
		gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
		btnFingerprintScannerAction.setGraphic(gearIcon);
		
		gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
		btnCameraAction.setGraphic(gearIcon);
		
		gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
		btnPassportScannerAction.setGraphic(gearIcon);
		
		paneFingerprintScanner.visibleProperty().bind(piFingerprintScanner.visibleProperty().not());
		paneFingerprintScanner.managedProperty().bind(piFingerprintScanner.managedProperty().not());
		paneCamera.visibleProperty().bind(piCamera.visibleProperty().not());
		paneCamera.managedProperty().bind(piCamera.managedProperty().not());
		panePassportScanner.visibleProperty().bind(piPassportScanner.visibleProperty().not());
		panePassportScanner.managedProperty().bind(piPassportScanner.managedProperty().not());
		
		lblDevicesRunnerNotWorking.visibleProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				changeFingerprintScannerStatus(DeviceStatus.NOT_INITIALIZED);
				changeCameraStatus(DeviceStatus.NOT_INITIALIZED);
				changePassportScannerStatus(DeviceStatus.NOT_INITIALIZED);
			}
			
			btnFingerprintScannerAction.setDisable(newValue);
			btnCameraAction.setDisable(newValue);
			btnPassportScannerAction.setDisable(newValue);
		});
	}
	
	private Stage buildProgressDialog(CancelCommand cancelCommand, String message, Future<?> future)
	{
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Button btnCancel = new Button(resources.getString("button.cancel"));
		btnCancel.setFocusTraversable(false);
		Label lblProgress = new Label(message);
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setMaxHeight(18.0);
		progressIndicator.setMaxWidth(18.0);
		
		VBox inner = new VBox(5.0);
		inner.setAlignment(Pos.CENTER);
		inner.getChildren().addAll(progressIndicator, lblProgress);
		
		VBox outer = new VBox(15.0);
		outer.getStylesheets().setAll("sa/gov/nic/bio/bw/client/core/css/style.css");
		outer.setAlignment(Pos.CENTER);
		outer.getChildren().addAll(inner, btnCancel);
		outer.setPadding(new Insets(10.0));
		
		Stage dialogStage = DialogUtils.buildCustomDialog(Context.getCoreFxController().getStage(),
		                                                  null, outer, rtl, true);
		
		dialogStage.setOnCloseRequest(event ->
		{
			cancelCommand.cancel();
			if(future != null) future.cancel(true);
		});
		
		btnCancel.setOnAction(e ->
		{
			cancelCommand.cancel();
			if(future != null) future.cancel(true);
			dialogStage.close();
		});
		
		dialogStage.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if(event.getCode() == KeyCode.ESCAPE)
			{
				cancelCommand.cancel();
				if(future != null) future.cancel(true);
				dialogStage.close();
				event.consume();
			}
		});
		
		return dialogStage;
	}
	
	public void runAndConnectDevicesRunner()
	{
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.runningAndConnectingDevicesRunner");
		Stage dialogStage = buildProgressDialog(cancelCommand, message, null);
		runAndConnectDevicesRunner(cancelCommand, dialogStage);
		
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
	
	private void runAndConnectDevicesRunner(CancelCommand cancelCommand, Stage dialogStage)
	{
		Task<Void> runTask = new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				BioKitManager bioKitManager = Context.getBioKitManager();
				boolean isListening = BclUtils.isLocalhostPortListening(bioKitManager.getWebsocketPort());
				if(cancelCommand.isCanceled()) return null;
				
				if(!isListening)
				{
					LOGGER.info("Bio-Kit is not running! Launching via BCL...");
					int checkEveryMilliSeconds = 1000; // TODO: make it configurable
					
					// TODO: improve it
					String serverUrl = Context.getServerUrl();
					if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL ||
					   Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
								serverUrl = AppConstants.DEV_SERVER_URL;
					
					BclUtils.launchAppByBCL(serverUrl, bioKitManager.getBclId(),
					                        bioKitManager.getWebsocketPort(), checkEveryMilliSeconds, cancelCommand);
				}
				
				if(cancelCommand.isCanceled()) return null;
				bioKitManager.connect();
				return null;
			}
		};
		runTask.setOnSucceeded(e ->
		{
		    dialogStage.close();
		    Context.getCoreFxController().getStage().setIconified(false);
			Context.getCoreFxController().getStage().toFront();
			
		    if(cancelCommand.isCanceled()) return;
		
		    LOGGER.info("successfully connected to the devices runner");
		    changeDevicesRunnerStatus(true);
		});
		runTask.setOnFailed(e ->
		{
		    dialogStage.close();
			Context.getCoreFxController().getStage().setIconified(false);
			Context.getCoreFxController().getStage().toFront();
			
		    Throwable exception = runTask.getException();
		
		    if(exception instanceof AlreadyConnectedException)
		    {
		        LOGGER.warning("already connected to BioKit");
		        changeDevicesRunnerStatus(true);
		    }
		    else
		    {
		        LOGGER.severe("failed to connect to the devices runner!");
		
		        String errorCode = CoreErrorCodes.C002_00017.getCode();
		        String[] errorDetails = {"failed to connect to the devices runner!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(runTask);
	}
	
	private void reconnectDevicesRunner()
	{
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.reconnectingToDevicesRunner");
		Stage dialogStage = buildProgressDialog(cancelCommand, message, null);
		
		Task<Void> runTask = new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				BioKitManager bioKitManager = Context.getBioKitManager();
				bioKitManager.disconnect();
				bioKitManager.connect();
				return null;
			}
		};
		runTask.setOnSucceeded(e ->
		{
		    dialogStage.close();
		    if(cancelCommand.isCanceled()) return;
		
		    LOGGER.info("successfully reconnected to the devices runner");
		    changeDevicesRunnerStatus(true);
		});
		runTask.setOnFailed(e ->
		{
		    dialogStage.close();
		    Throwable exception = runTask.getException();
		
		    if(exception instanceof AlreadyConnectedException)
		    {
		        LOGGER.warning("already connected to the devices runner");
		        changeDevicesRunnerStatus(true);
		    }
		    else
		    {
		        LOGGER.severe("failed to reconnect to the devices runner!");
		
		        String errorCode = CoreErrorCodes.C002_00018.getCode();
		        String[] errorDetails = {"failed to connect to the devices runner!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(runTask);
		
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
	
	private void restartDevicesRunner()
	{
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.restartingDevicesRunner");
		
		Future<ServiceResponse<ShutdownResponse>> future = Context.getBioKitManager().getBiokitCommander().shutdown();
		Stage dialogStage = buildProgressDialog(cancelCommand, message, future);
		
		Task<Void> task = new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				ServiceResponse<ShutdownResponse> serviceResponse = future.get();
				
				if(serviceResponse.isSuccess())
				{
					ShutdownResponse result = serviceResponse.getResult();
					
					if(result.getReturnCode() == ShutdownResponse.SuccessCodes.BIOKIT_IS_SHUTTING_DOWN)
					{
						int checkEveryMilliSeconds = 1000; // TODO: make it configurable
						
						// wait until it's completely shutdown
						while(BclUtils.isLocalhostPortListening(Context.getBioKitManager().getWebsocketPort()) &&
							  !cancelCommand.isCanceled())
						{
							Thread.sleep(checkEveryMilliSeconds);
						}
					}
					else
					{
						cancelCommand.cancel();
						LOGGER.severe("failed to shutdown the devices runner!");
						
						String[] errorDetails = {"failed to shutdown the devices runner!",
												 "returnMessage = " + result.getReturnMessage()};
						Context.getCoreFxController().showErrorDialog(String.valueOf(result.getReturnCode()),
						                                              null, errorDetails);
					}
				}
				else
				{
					cancelCommand.cancel();
					LOGGER.severe("failed to receive a response for shutting down the devices runner!");
					
					String[] errorDetails = {"failed to receive a response for shutting down the devices runner!"};
					Context.getCoreFxController().showErrorDialog(serviceResponse.getErrorCode(),
					                                              serviceResponse.getException(), errorDetails);
				}
				
				return null;
			}
		};
		task.setOnSucceeded(e ->
		{
		    if(cancelCommand.isCanceled()) return;
		    runAndConnectDevicesRunner(cancelCommand, dialogStage);
		});
		task.setOnFailed(e ->
		{
		    dialogStage.close();
		    Throwable exception = task.getException();
			
			if(exception instanceof CancellationException)
			{
				LOGGER.info("shutting down the devices runner is cancelled!");
			}
			else
			{
				LOGGER.severe("failed to shutdown the devices runner!");
				
				String errorCode = CoreErrorCodes.C002_00019.getCode();
				String[] errorDetails = {"failed to shutdown the devices runner!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(task);
		
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
	
	public void initializeFingerprintScanner()
	{
		GuiUtils.showNode(piFingerprintScanner, true);
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.initializingFingerprintScanner");
		
		Future<ServiceResponse<InitializeResponse>> future = Context.getBioKitManager().getFingerprintService()
																.initialize(FingerPosition.RIGHT_THUMB.getPosition());
		Stage dialogStage = buildProgressDialog(cancelCommand, message, future);
		
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
		    GuiUtils.showNode(piFingerprintScanner, false);
		    dialogStage.close();
		    if(cancelCommand.isCanceled()) return;
		
		    ServiceResponse<InitializeResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
		        InitializeResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == InitializeResponse.SuccessCodes.SUCCESS)
		        {
			        fingerprintScannerDeviceName = result.getCurrentDeviceName();
		            changeFingerprintScannerStatus(DeviceStatus.INITIALIZED);
		        }
		        else if(result.getReturnCode() == InitializeResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
		        {
			        changeFingerprintScannerStatus(DeviceStatus.NOT_CONNECTED);
		        }
		        else if(result.getReturnCode() == InitializeResponse.FailureCodes.DRIVER_NOT_INSTALLED)
		        {
			        cancelCommand.cancel();
			
			        String errorCode = CommonsErrorCodes.N008_00001.getCode();
			        String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
			        String logErrorMessage = Context.getErrorsBundle().getString(errorCode + ".internal");
			        
			        LOGGER.severe(logErrorMessage);
			        Context.getCoreFxController().getCurrentBodyController().showErrorNotification(guiErrorMessage);
		        }
		        else
		        {
		            cancelCommand.cancel();
		            LOGGER.severe("failed to initialize the fingerprint scanner!");
		
		            String[] errorDetails = {"failed to initialize the fingerprint scanner!",
		                    "returnMessage = " + result.getReturnMessage()};
		            Context.getCoreFxController().showErrorDialog(String.valueOf(result.getReturnCode()),null,
		                                             errorDetails);
		        }
		    }
		    else
		    {
		        cancelCommand.cancel();
		        LOGGER.severe("failed to receive a response for initializing the fingerprint scanner!");
		
		        String[] errorDetails = {"failed to receive a response for initializing the fingerprint scanner!"};
		        Context.getCoreFxController().showErrorDialog(serviceResponse.getErrorCode(),
		                                                      serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piFingerprintScanner, false);
		    dialogStage.close();
		    Throwable exception = task.getException();
		
		    if(exception instanceof CancellationException)
		    {
		        LOGGER.info("Initializing the fingerprint scanner is cancelled!");
		    }
		    else
		    {
		        LOGGER.severe("failed to initialize the fingerprint scanner!");
		
		        String errorCode = CoreErrorCodes.C002_00020.getCode();
		        String[] errorDetails = {"failed to initialize the fingerprint scanner!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(task);
		
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
	
	public void initializeCamera()
	{
		GuiUtils.showNode(piCamera, true);
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.initializingCamera");
		
		Future<ServiceResponse<InitializeResponse>> future = Context.getBioKitManager().getFaceService().initialize();
		Stage dialogStage = buildProgressDialog(cancelCommand, message, future);
		
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
			GuiUtils.showNode(piCamera, false);
			dialogStage.close();
			if(cancelCommand.isCanceled()) return;
			
		    ServiceResponse<InitializeResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
		        InitializeResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == InitializeResponse.SuccessCodes.SUCCESS)
		        {
			        cameraDeviceName = result.getCurrentDeviceName();
		            changeCameraStatus(DeviceStatus.INITIALIZED);
		        }
		        else if(result.getReturnCode() == InitializeResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
		        {
			        changeCameraStatus(DeviceStatus.NOT_CONNECTED);
		        }
		        else
		        {
			        cancelCommand.cancel();
			        LOGGER.severe("failed to initialize the camera!");
			
			        String[] errorDetails = {"failed to initialize the camera!",
					                         "returnMessage = " + result.getReturnMessage()};
			        Context.getCoreFxController().showErrorDialog(String.valueOf(result.getReturnCode()),null,
			                                         errorDetails);
		        }
		    }
		    else
		    {
			    cancelCommand.cancel();
			    LOGGER.severe("failed to receive a response for initializing the camera!");
			
			    String[] errorDetails = {"failed to receive a response for initializing the camera!"};
			    Context.getCoreFxController().showErrorDialog(serviceResponse.getErrorCode(),
			                                                  serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			GuiUtils.showNode(piCamera, false);
			dialogStage.close();
			Throwable exception = task.getException();
			
			if(exception instanceof CancellationException)
			{
				LOGGER.info("Initializing the camera is cancelled!");
			}
			else
			{
				LOGGER.severe("failed to initialize the camera!");
				
				String errorCode = CoreErrorCodes.C002_00021.getCode();
				String[] errorDetails = {"failed to initialize the camera!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(task);
		
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
	
	public void initializePassportScanner()
	{
		GuiUtils.showNode(piPassportScanner, true);
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.initializingPassportScanner");
		
		Future<ServiceResponse<InitializeResponse>> future = Context.getBioKitManager().getPassportScannerService()
																					   .initialize();
		Stage dialogStage = buildProgressDialog(cancelCommand, message, future);
		
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
		    GuiUtils.showNode(piPassportScanner, false);
		    dialogStage.close();
		    if(cancelCommand.isCanceled()) return;
		
		    ServiceResponse<InitializeResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
		        InitializeResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == InitializeResponse.SuccessCodes.SUCCESS)
		        {
		            passportScannerDeviceName = result.getCurrentDeviceName();
		            changePassportScannerStatus(DeviceStatus.INITIALIZED);
		        }
		        else if(result.getReturnCode() == InitializeResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
		        {
			        changePassportScannerStatus(DeviceStatus.NOT_CONNECTED);
		        }
		        else
		        {
		            cancelCommand.cancel();
		            LOGGER.severe("failed to initialize the passport scanner!");
		
		            String[] errorDetails = {"failed to initialize the passport scanner!",
		                    "returnMessage = " + result.getReturnMessage()};
		            Context.getCoreFxController().showErrorDialog(String.valueOf(result.getReturnCode()),null,
		                                                          errorDetails);
		        }
		    }
		    else
		    {
		        cancelCommand.cancel();
		        LOGGER.severe("failed to receive a response for initializing the passport scanner!");
		
		        String[] errorDetails = {"failed to receive a response for initializing the passport scanner!"};
		        Context.getCoreFxController().showErrorDialog(serviceResponse.getErrorCode(),
		                                                      serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
		    GuiUtils.showNode(piPassportScanner, false);
		    dialogStage.close();
		    Throwable exception = task.getException();
		
		    if(exception instanceof CancellationException)
		    {
		        LOGGER.info("Initializing the passport scanner is cancelled!");
		    }
		    else
		    {
		        LOGGER.severe("failed to initialize the passport scanner!");
		
		        String errorCode = CoreErrorCodes.C002_00022.getCode();
		        String[] errorDetails = {"failed to initialize the passport scanner!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(task);
		
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
	
	private void changeDevicesRunnerStatus(boolean working)
	{
		contextMenu.hide();
		GuiUtils.showNode(lblDevicesRunnerNotWorking, !working);
		GuiUtils.showNode(lblDevicesRunnerWorking, working);
		if(devicesRunnerRunningListener != null) devicesRunnerRunningListener.accept(working);
	}
	
	private void changeFingerprintScannerStatus(DeviceStatus deviceStatus)
	{
		contextMenu.hide();
		GuiUtils.showNode(lblFingerprintScannerNotInitialized, deviceStatus == DeviceStatus.NOT_INITIALIZED);
		GuiUtils.showNode(lblFingerprintScannerInitialized, deviceStatus == DeviceStatus.INITIALIZED);
		GuiUtils.showNode(lblFingerprintScannerNotConnected, deviceStatus == DeviceStatus.NOT_CONNECTED);
		
		if(fingerprintScannerInitializationListener != null)
						fingerprintScannerInitializationListener.accept(deviceStatus == DeviceStatus.INITIALIZED);
	}
	
	private void changeCameraStatus(DeviceStatus deviceStatus)
	{
		contextMenu.hide();
		GuiUtils.showNode(lblCameraNotInitialized, deviceStatus == DeviceStatus.NOT_INITIALIZED);
		GuiUtils.showNode(lblCameraInitialized, deviceStatus == DeviceStatus.INITIALIZED);
		GuiUtils.showNode(lblCameraNotConnected, deviceStatus == DeviceStatus.NOT_CONNECTED);
		
		if(cameraInitializationListener != null)
						cameraInitializationListener.accept(deviceStatus == DeviceStatus.INITIALIZED);
	}
	
	private void changePassportScannerStatus(DeviceStatus deviceStatus)
	{
		contextMenu.hide();
		GuiUtils.showNode(lblPassportScannerNotInitialized, deviceStatus == DeviceStatus.NOT_INITIALIZED);
		GuiUtils.showNode(lblPassportScannerInitialized, deviceStatus == DeviceStatus.INITIALIZED);
		GuiUtils.showNode(lblPassportScannerNotConnected, deviceStatus == DeviceStatus.NOT_CONNECTED);
		
		if(passportScannerInitializationListener != null)
			passportScannerInitializationListener.accept(deviceStatus == DeviceStatus.INITIALIZED);
	}
	
	@FXML
	private void onDevicesRunnerActionButtonClicked(MouseEvent actionEvent)
	{
		Context.getCoreFxController().getNotificationPane().hide();
		
		if(lblDevicesRunnerWorking.isVisible())
		{
			MenuItem menuReconnect = new MenuItem(resources.getString("menu.reconnect"));
			MenuItem menuRestart = new MenuItem(resources.getString("menu.restart"));
			
			menuReconnect.setOnAction(e -> reconnectDevicesRunner());
			menuRestart.setOnAction(e -> restartDevicesRunner());
			
			Glyph plugIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.PLUG);
			menuReconnect.setGraphic(plugIcon);
			
			Glyph restartIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.POWER_OFF);
			menuRestart.setGraphic(restartIcon);
			
			contextMenu.getItems().setAll(menuReconnect, menuRestart);
		}
		else
		{
			MenuItem menuRunAndReconnect = new MenuItem(resources.getString("menu.runAndConnect"));
			menuRunAndReconnect.setOnAction(e -> runAndConnectDevicesRunner());
			
			Glyph playIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.PLAY_CIRCLE_ALT);
			menuRunAndReconnect.setGraphic(playIcon);
			
			contextMenu.getItems().setAll(menuRunAndReconnect);
		}
		
		if(contextMenu.isShowing()) contextMenu.hide();
		
		contextMenu.show(btnDevicesRunnerAction, actionEvent.getScreenX(), actionEvent.getScreenY());
	}
	
	@FXML
	private void onFingerprintScannerActionButtonClicked(MouseEvent actionEvent)
	{
		Context.getCoreFxController().getNotificationPane().hide();
		
		if(lblFingerprintScannerInitialized.isVisible() || lblFingerprintScannerNotConnected.isVisible())
		{
			MenuItem menuReinitialize = new MenuItem(resources.getString("menu.reinitialize"));
			menuReinitialize.setOnAction(e -> initializeFingerprintScanner());
			
			Glyph initializeIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.WRENCH);
			menuReinitialize.setGraphic(initializeIcon);
			
			contextMenu.getItems().setAll(menuReinitialize);
		}
		else
		{
			MenuItem menuInitialize = new MenuItem(resources.getString("menu.initialize"));
			menuInitialize.setOnAction(e -> initializeFingerprintScanner());
			
			Glyph initializeIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.WRENCH);
			menuInitialize.setGraphic(initializeIcon);
			
			contextMenu.getItems().setAll(menuInitialize);
		}
		
		if(contextMenu.isShowing()) contextMenu.hide();
		
		contextMenu.show(btnFingerprintScannerAction, actionEvent.getScreenX(), actionEvent.getScreenY());
	}
	
	@FXML
	private void onCameraActionButtonClicked(MouseEvent actionEvent)
	{
		Context.getCoreFxController().getNotificationPane().hide();
		
		if(lblCameraInitialized.isVisible() || lblCameraNotConnected.isVisible())
		{
			MenuItem menuReinitialize = new MenuItem(resources.getString("menu.reinitialize"));
			menuReinitialize.setOnAction(e -> initializeCamera());
			
			Glyph initializeIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.WRENCH);
			menuReinitialize.setGraphic(initializeIcon);
			
			contextMenu.getItems().setAll(menuReinitialize);
		}
		else
		{
			MenuItem menuInitialize = new MenuItem(resources.getString("menu.initialize"));
			menuInitialize.setOnAction(e -> initializeCamera());
			
			Glyph initializeIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.WRENCH);
			menuInitialize.setGraphic(initializeIcon);
			
			contextMenu.getItems().setAll(menuInitialize);
		}
		
		if(contextMenu.isShowing()) contextMenu.hide();
		
		contextMenu.show(btnFingerprintScannerAction, actionEvent.getScreenX(), actionEvent.getScreenY());
	}
	
	@FXML
	private void onPassportScannerActionButtonClicked(MouseEvent actionEvent)
	{
		Context.getCoreFxController().getNotificationPane().hide();
		
		if(lblPassportScannerInitialized.isVisible() || lblPassportScannerNotConnected.isVisible())
		{
			MenuItem menuReinitialize = new MenuItem(resources.getString("menu.reinitialize"));
			menuReinitialize.setOnAction(e -> initializePassportScanner());
			
			Glyph initializeIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.WRENCH);
			menuReinitialize.setGraphic(initializeIcon);
			
			contextMenu.getItems().setAll(menuReinitialize);
		}
		else
		{
			MenuItem menuInitialize = new MenuItem(resources.getString("menu.initialize"));
			menuInitialize.setOnAction(e -> initializePassportScanner());
			
			Glyph initializeIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.WRENCH);
			menuInitialize.setGraphic(initializeIcon);
			
			contextMenu.getItems().setAll(menuInitialize);
		}
		
		if(contextMenu.isShowing()) contextMenu.hide();
		
		contextMenu.show(btnFingerprintScannerAction, actionEvent.getScreenX(), actionEvent.getScreenY());
	}
}