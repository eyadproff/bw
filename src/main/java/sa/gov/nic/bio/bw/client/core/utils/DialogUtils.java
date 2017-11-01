package sa.gov.nic.bio.bw.client.core.utils;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by Fouad on 12-Jul-17.
 */
public class DialogUtils
{
	public static void showErrorDialog(Image appIcon, String title, String headerText, String contentText,
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
				(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue ) ->
				{
					detailsButton.setText(newValue ? lessDetailsText : moreDetailsText);
				});
		}
		
		stage.sizeToScene();
		alert.showAndWait();
	}
	
	public static boolean showConfirmationDialog(Image appIcon, String title, String headerText, String contentText,
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
		/*btnConfirm.setOnKeyReleased(event ->
		{
			if(event.getCode() == KeyCode.ENTER) alert.setResult(buttonTypeConfirm);
		});
		
		btnCancel.setOnKeyReleased(event ->
	    {
	        if(event.getCode() == KeyCode.ENTER) alert.setResult(buttonTypeCancel);
	    });*/
		
		stage.sizeToScene();
		Optional<ButtonType> buttonType = alert.showAndWait();
		return buttonType.isPresent() && buttonType.get() == buttonTypeConfirm;
	}
	
	public static void showWarningDialog(Image appIcon, String title, String headerText, String contentText, String buttonText, boolean rtl)
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
		alert.showAndWait();
	}
	
	public static /*Dialog<?>*/ Stage buildCustomDialog(Image appIcon, String title, Pane contentPane, String buttonCloseText, boolean rtl)
	{
		/*Alert alert = new Alert(AlertType.NONE);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initStyle(StageStyle.UNIFIED);
		alert.setResizable(true);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
		if(appIcon != null) stage.getIcons().add(appIcon);
		alert.setHeaderText(null);
		alert.setTitle(title);
		
		alert.getDialogPane().setContent(contentPane);
		
		ButtonType buttonTypeClose = new ButtonType(buttonCloseText, ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeClose);
		
		Button btnClose = (Button) alert.getDialogPane().lookupButton(buttonTypeClose);
		btnClose.setDefaultButton(false);
		
		return alert;*/
		
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		Scene scene = new Scene(contentPane);
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, t ->
		{
			if(t.getCode() == KeyCode.ESCAPE) stage.close();
		});
		if(appIcon != null) stage.getIcons().add(appIcon);
		stage.setTitle(title);
		stage.setScene(scene);
		
		return stage;
	}
}