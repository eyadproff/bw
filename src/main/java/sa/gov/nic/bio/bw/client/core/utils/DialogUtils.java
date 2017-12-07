package sa.gov.nic.bio.bw.client.core.utils;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.interfaces.IdleMonitorRegisterer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Fouad on 12-Jul-17.
 */
public class DialogUtils
{
	public static void showErrorDialog(IdleMonitorRegisterer idleMonitorRegisterer, Image appIcon, String title, String headerText, String contentText,
	                                   String buttonOkText, String moreDetailsText, String lessDetailsText, Exception exception, boolean rtl)
	{
		Alert alert = new Alert(AlertType.ERROR);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
		if(appIcon != null) stage.getIcons().add(appIcon);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		
		ButtonType buttonTypeCancel = new ButtonType(buttonOkText, ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeCancel);
		
		
		if(exception != null)
		{
			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			String exceptionText = sw.toString();
			
			TextArea textArea = new TextArea(exceptionText);
			textArea.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			textArea.setEditable(false);
			textArea.setWrapText(true);
			
			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);
			
			
			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(textArea, 0, 1);
			
			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);
			
			Hyperlink detailsButton = ( Hyperlink ) alert.getDialogPane().lookup( ".details-button" );
			detailsButton.setText(moreDetailsText);
			
			alert.getDialogPane().expandedProperty().addListener(
				(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue ) -> detailsButton.setText(newValue ? lessDetailsText : moreDetailsText));
		}
		
		stage.sizeToScene();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.registerStageForIdleMonitoring(stage);
		alert.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring(stage);
	}
	
	public static boolean showConfirmationDialog(IdleMonitorRegisterer idleMonitorRegisterer, Image appIcon, String title, String headerText, String contentText,
	                                          String buttonConfirmText, String buttonCancelText, boolean rtl)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
		if(appIcon != null) stage.getIcons().add(appIcon);
		alert.setTitle(title);
		
		if(headerText != null)
		{
			alert.setHeaderText(headerText);
			alert.setContentText(contentText);
		}
		else alert.setHeaderText(contentText);
		
		ButtonType buttonTypeConfirm = new ButtonType(buttonConfirmText, ButtonBar.ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType(buttonCancelText, ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);
		
		Button btnConfirm = (Button) alert.getDialogPane().lookupButton(buttonTypeConfirm);
		Button btnCancel = (Button) alert.getDialogPane().lookupButton(buttonTypeCancel);
		
		btnConfirm.setDefaultButton(false);
		
		final int INITIAL = 0;
		final int PRESSED = 1;
		final int RELEASED = 2;
		
		AtomicInteger enterKeyState = new AtomicInteger(INITIAL);
		
		btnConfirm.setOnKeyPressed(event ->
		{
			if(event.getCode() == KeyCode.ENTER) enterKeyState.set(PRESSED);
		});
		btnConfirm.setOnKeyReleased(event ->
		{
			if(event.getCode() == KeyCode.ENTER && enterKeyState.get() != INITIAL)
			{
				if(enterKeyState.get() == PRESSED) alert.setResult(buttonTypeConfirm);
				enterKeyState.set(RELEASED);
		    }
		});
		
		btnCancel.setOnKeyReleased(event ->
	    {
	        if(event.getCode() == KeyCode.ENTER) alert.setResult(buttonTypeCancel);
	    });
		
		stage.sizeToScene();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.registerStageForIdleMonitoring(stage);
		Optional<ButtonType> buttonType = alert.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring(stage);
		return buttonType.isPresent() && buttonType.get() == buttonTypeConfirm;
	}
	
	public static void showWarningDialog(IdleMonitorRegisterer idleMonitorRegisterer, Image appIcon, String title, String headerText, String contentText, String buttonText, boolean rtl)
	{
		Alert alert = new Alert(AlertType.WARNING);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
		if(appIcon != null) stage.getIcons().add(appIcon);
		alert.setTitle(title);
		
		if(headerText != null)
		{
			alert.setHeaderText(headerText);
			alert.setContentText(contentText);
		}
		else alert.setHeaderText(contentText);
		
		ButtonType buttonType = new ButtonType(buttonText, ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonType);
		
		Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
		
		button.setDefaultButton(false);
		button.setOnKeyReleased(event ->
		{
			if(event.getCode() == KeyCode.ENTER) alert.setResult(buttonType);
		});
		
		stage.sizeToScene();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.registerStageForIdleMonitoring(stage);
		alert.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring(stage);
	}
	
	public static Stage buildCustomDialog(Image appIcon, String title, Pane contentPane, boolean rtl)
	{
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		Scene scene = new Scene(contentPane);
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		if(appIcon != null) stage.getIcons().add(appIcon);
		stage.setTitle(title);
		stage.setScene(scene);
		
		return stage;
	}
	
	public static <T> Dialog<T> buildCustomDialog(Image appIcon, String fxml, ResourceBundle resourceBundle, boolean rtl)
	{
		URL fxmlResource = Thread.currentThread().getContextClassLoader().getResource(fxml);
		
		if(fxmlResource == null)
		{
			// TODO: handle error
			return null;
		}
		
		Dialog<T> dialog;
		try
		{
			dialog = FXMLLoader.load(fxmlResource, resourceBundle);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			// TODO: handle error
			return null;
		}
		
		Scene scene = dialog.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		
		Stage stage = (Stage) scene.getWindow();
		stage.getIcons().add(appIcon);
		
		return dialog;
	}
}