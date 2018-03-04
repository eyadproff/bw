package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bcl.utils.CancelCommand;
import sa.gov.nic.bio.biokit.exceptions.AlreadyConnectedException;
import sa.gov.nic.bio.biokit.websocket.ClosureListener;
import sa.gov.nic.bio.bw.client.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * JavaFX controller for the devices runner gadget. It is shown after login.
 *
 * @author Fouad Almalki
 */
public class DevicesRunnerGadgetPaneFxController extends RegionFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(DevicesRunnerGadgetPaneFxController.class.getName());
	
	@FXML private ResourceBundle resources;
	@FXML private Label lblNotWorking;
	@FXML private Label lblWorking;
	@FXML private Button btnAction;
	
	private ContextMenu contextMenu;
	
	private ClosureListener closureListener = closeReason ->
	{
		Platform.runLater(() -> changeStatus(false));
	};
	
	public ClosureListener getClosureListener(){return closureListener;}
	
	@Override
	protected void initialize()
	{
		contextMenu = new ContextMenu();
		
		Glyph gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
		btnAction.setGraphic(gearIcon);
	}
	
	private Stage buildProgressDialog(CancelCommand cancelCommand, String message)
	{
		boolean rtl = coreFxController.getCurrentLanguage()
				.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Button btnCancel = new Button(resources.getString("button.cancel"));
		Label lblProgress = new Label(message);
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setMaxHeight(15.0);
		progressIndicator.setMaxWidth(15.0);
		
		HBox hBox = new HBox(5.0);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(lblProgress, progressIndicator);
		
		VBox vBox = new VBox(10.0);
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().addAll(hBox, btnCancel);
		vBox.setPadding(new Insets(10.0));
		
		Stage dialogStage = DialogUtils.buildCustomDialog(coreFxController.getPrimaryStage(),
		                                                  resources.getString("dialog.inProgressOperation.title"),
		                                                  vBox, rtl);
		btnCancel.setOnAction(e ->
		{
		    cancelCommand.cancel();
		    dialogStage.close();
		});
		
		return dialogStage;
	}
	
	public void runAndConnectDevicesRunner()
	{
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.runningAndConnectingDevicesRunner");
		Stage dialogStage = buildProgressDialog(cancelCommand, message);
		
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
					int checkEverySeconds = 1000; // TODO: make it configurable
					BclUtils.launchAppByBCL(Context.getServerUrl(), bioKitManager.getBclId(),
					                        bioKitManager.getWebsocketPort(), checkEverySeconds, cancelCommand);
				}
				
				if(cancelCommand.isCanceled()) return null;
				bioKitManager.connect();
				return null;
			}
		};
		runTask.setOnSucceeded(e ->
		{
		    dialogStage.close();
		    if(cancelCommand.isCanceled()) return;
		
		    LOGGER.info("successfully connected to the devices runner");
			changeStatus(true);
		});
		runTask.setOnFailed(e ->
		{
		    dialogStage.close();
		    Throwable exception = runTask.getException();
		
		    if(exception instanceof AlreadyConnectedException)
		    {
		        LOGGER.info("already connected to BioKit");
			    changeStatus(true);
		    }
		    else
		    {
		        LOGGER.info("failed to connect to the devices runner!");
		
		        String errorCode = CoreErrorCodes.C002_00017.getCode();
		        String[] errorDetails = {"failed to connect to the devices runner!"};
		        coreFxController.showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(runTask);
		dialogStage.show();
	}
	
	public void reconnectDevicesRunner()
	{
		CancelCommand cancelCommand = new CancelCommand();
		String message = resources.getString("label.reconnectingToDevicesRunner");
		Stage dialogStage = buildProgressDialog(cancelCommand, message);
		
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
		    changeStatus(true);
		});
		runTask.setOnFailed(e ->
		{
		    dialogStage.close();
		    Throwable exception = runTask.getException();
		
		    if(exception instanceof AlreadyConnectedException)
		    {
		        LOGGER.info("already connected to the devices runner");
		        changeStatus(true);
		    }
		    else
		    {
		        LOGGER.info("failed to reconnect to the devices runner!");
		
		        String errorCode = CoreErrorCodes.C002_00018.getCode();
		        String[] errorDetails = {"failed to connect to the devices runner!"};
		        coreFxController.showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(runTask);
		dialogStage.show();
	}
	
	public void restartDevicesRunner()
	{
	
	}
	
	public void changeStatus(boolean working)
	{
		contextMenu.hide();
		GuiUtils.showNode(lblNotWorking, !working);
		GuiUtils.showNode(lblWorking, working);
	}
	
	@FXML
	private void onActionButtonClicked(MouseEvent actionEvent)
	{
		if(lblWorking.isVisible())
		{
			MenuItem menuReconnect = new MenuItem(resources.getString("menu.reconnect"));
			MenuItem menuRestart = new MenuItem(resources.getString("menu.restart"));
			
			menuReconnect.setOnAction(this::onReconnectButtonClicked);
			menuRestart.setOnAction(this::onRestartButtonClicked);
			
			Glyph plugIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.PLUG);
			menuReconnect.setGraphic(plugIcon);
			
			Glyph restartIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.POWER_OFF);
			menuRestart.setGraphic(restartIcon);
			
			contextMenu.getItems().setAll(menuReconnect, menuRestart);
		}
		else
		{
			MenuItem menuRunAndReconnect = new MenuItem(resources.getString("menu.runAndConnect"));
			menuRunAndReconnect.setOnAction(this::onRunAndConnectButtonClicked);
			
			Glyph playIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.PLAY_CIRCLE_ALT);
			menuRunAndReconnect.setGraphic(playIcon);
			
			contextMenu.getItems().setAll(menuRunAndReconnect);
		}
		
		contextMenu.show(btnAction, actionEvent.getScreenX(), actionEvent.getScreenY());
	}
	
	@FXML
	private void onRunAndConnectButtonClicked(ActionEvent actionEvent)
	{
		runAndConnectDevicesRunner();
	}
	
	@FXML
	private void onReconnectButtonClicked(ActionEvent actionEvent)
	{
		reconnectDevicesRunner();
	}
	
	@FXML
	private void onRestartButtonClicked(ActionEvent actionEvent)
	{
		restartDevicesRunner();
	}
}