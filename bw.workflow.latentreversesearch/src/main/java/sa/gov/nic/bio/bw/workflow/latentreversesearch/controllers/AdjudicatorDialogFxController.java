package sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.RuntimeEnvironment;

@FxmlFile("adjudicatorDialog.fxml")
public class AdjudicatorDialogFxController extends ContentFxControllerBase
{
	private static final String ADJUDICATOR_PATH = "/innovatrics/tools/embedded_adjudicator";
	private static final String JAVASCRIPT_COMMAND_INIT_ADJUDICATOR = "window.adjudicatorApp.init({probe:{image:\"%s\"},reference:{image:\"%s\"}})";
	
	@FXML private Dialog<Object> dialog;
	@FXML private WebView wvAdjudicator;
	@FXML private Pane paneButtons;
	@FXML private Button btnCloseWindow;
	
	private String latentImageBase64;
	private String fingerImageBase64;
	
	@Override
	protected void initialize()
	{
		WebEngine webEngine = wvAdjudicator.getEngine();
		webEngine.setJavaScriptEnabled(true);
		webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) ->
		{
			if(newState == Worker.State.SUCCEEDED)
			{
				if(latentImageBase64 != null && fingerImageBase64 != null)
				{
					var command = String.format(JAVASCRIPT_COMMAND_INIT_ADJUDICATOR, latentImageBase64, fingerImageBase64);
					webEngine.executeScript(command);
				}
			}
		});
		
		dialog.setOnShown(event ->
		{
			// workaround to center buttons and remove extra spaces
			ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
			buttonBar.getButtons().setAll(paneButtons);
			HBox hBox = (HBox) buttonBar.lookup(".container");
			hBox.setPadding(new Insets(0.0, 10.0, 10.0, 10.0));
			hBox.getChildren().remove(0);
			
			String serverUrl = Context.getServerUrl();
			if(Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV || Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL) serverUrl = AppConstants.DEV_SERVER_URL;
			if(serverUrl.startsWith("http")) serverUrl = serverUrl.substring(serverUrl.indexOf("://") + 3);
			
			webEngine.load("http://" + serverUrl + ADJUDICATOR_PATH);
		});
	}
	
	public void setLatentImageBase64(String latentImageBase64)
	{
		this.latentImageBase64 = latentImageBase64;
	}
	
	public void setFingerImageBase64(String fingerImageBase64)
	{
		this.fingerImageBase64 = fingerImageBase64;
	}
	
	public void show()
	{
		dialog.showAndWait();
	}
	
	@FXML
	private void onCloseWindowButtonClicked(ActionEvent actionEvent)
	{
		dialog.setResult(new Object()); // the dialog will not be closed unless it has a result
		dialog.close();
	}
}