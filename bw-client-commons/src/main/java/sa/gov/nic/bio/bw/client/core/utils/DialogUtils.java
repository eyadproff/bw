package sa.gov.nic.bio.bw.client.core.utils;

import javafx.beans.value.ObservableValue;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Fouad on 12-Jul-17.
 */
public class DialogUtils
{
	public static void showErrorDialog(Image appIcon, String title, String headerText, String contentText,
	                            String buttonOkText, String moreDetailsText, String lessDetailsText, Exception exception)
	{
		Alert alert = new Alert(AlertType.ERROR);
		Scene scene = alert.getDialogPane().getScene();
		Stage stage = (Stage) scene.getWindow();
		scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
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
}