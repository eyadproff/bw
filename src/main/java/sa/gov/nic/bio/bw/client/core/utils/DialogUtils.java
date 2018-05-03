package sa.gov.nic.bio.bw.client.core.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.IdleMonitorRegisterer;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class DialogUtils
{
	public static void showAlertDialog(AlertType alertType, Window ownerWindow,
	                                   IdleMonitorRegisterer idleMonitorRegisterer, String title, String headerText,
	                                   String contentText, String extraDetailsText, String buttonText,
	                                   String buttonMoreDetailsText, String buttonLessDetailsText, boolean rtl)
	{
		Alert alert = new Alert(alertType);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		
		ButtonType buttonTypeCancel = new ButtonType(buttonText, ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeCancel);
		
		
		if(extraDetailsText != null)
		{
			TextArea textArea = new TextArea(extraDetailsText);
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
			
			alert.getDialogPane().setExpandableContent(expContent);
			
			Hyperlink detailsButton = (Hyperlink) alert.getDialogPane().lookup(".details-button");
			detailsButton.setText(buttonMoreDetailsText);
			
			alert.getDialogPane().expandedProperty().addListener((observable, oldValue, newValue) ->
					             detailsButton.setText(newValue ? buttonLessDetailsText : buttonMoreDetailsText));
		}
		
		stage.sizeToScene();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.registerStageForIdleMonitoring(stage);
		alert.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring(stage);
	}
	
	public static String showChoiceDialog(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer,
	                                      String title, String headerText, String[] choices,
	                                      String buttonConfirmText, boolean alwaysOnTop, boolean rtl)
	{
		ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(choices[0], choices);
		choiceDialog.initOwner(ownerWindow);
		choiceDialog.initStyle(StageStyle.UTILITY);
		Scene scene = choiceDialog.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
		stage.setAlwaysOnTop(alwaysOnTop);
		choiceDialog.setTitle(title);
		choiceDialog.setHeaderText(headerText);
		
		ButtonType buttonTypeConfirm = new ButtonType(buttonConfirmText, ButtonBar.ButtonData.OK_DONE);
		choiceDialog.getDialogPane().getButtonTypes().setAll(buttonTypeConfirm);
		
		Button btnConfirm = (Button) choiceDialog.getDialogPane().lookupButton(buttonTypeConfirm);
		btnConfirm.setDefaultButton(true);
		
		stage.sizeToScene();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.registerStageForIdleMonitoring(stage);
		choiceDialog.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring(stage);
		
		String selectedItem = choiceDialog.getSelectedItem();
		
		return selectedItem != null ? selectedItem : choices[0];
	}
	
	public static boolean showConfirmationDialog(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer,
	                                             String title, String headerText, String contentText,
	                                             String buttonConfirmText, String buttonCancelText, boolean rtl)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
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
	
	public static void showWarningDialog(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer, String title, String headerText, String contentText, String buttonText, boolean rtl)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		Stage stage = (Stage) scene.getWindow();
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
	
	public static Stage buildCustomDialog(Stage ownerStage, String title, Pane contentPane, boolean rtl,
	                                      boolean autoCenter)
	{
		Stage stage = new Stage();
		stage.initOwner(ownerStage);
		stage.getIcons().setAll(ownerStage.getIcons());
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(contentPane);
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		stage.setTitle(title);
		stage.setScene(scene);
		
		
		if(autoCenter) stage.setOnShown(ev ->
		{
			double centerXPosition = ownerStage.getX() + ownerStage.getWidth() / 2.0;
			double centerYPosition = ownerStage.getY() + ownerStage.getHeight() / 2.0;
			
			stage.setX(centerXPosition - stage.getWidth() / 2.0);
			stage.setY(centerYPosition - stage.getHeight() / 2.0);
		});
		
		return stage;
	}
	
	public static <T> T buildCustomDialogByFxml(Stage ownerStage, String fxml, ResourceBundle resourceBundle,
	                                            boolean rtl)
	{
		URL fxmlResource = Thread.currentThread().getContextClassLoader().getResource(fxml);
		
		if(fxmlResource == null)
		{
			// TODO: handle error
			return null;
		}
		
		FXMLLoader loader = new FXMLLoader(fxmlResource, resourceBundle);
		loader.setClassLoader(Context.getFxClassLoader());
		Dialog<ButtonType> dialog;
		try
		{
			dialog = loader.load();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			// TODO: handle error
			return null;
		}
		
		dialog.initOwner(ownerStage);
		Scene scene = dialog.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		
		Stage stage = (Stage) scene.getWindow();
		stage.getIcons().setAll(ownerStage.getIcons());
		stage.setOnCloseRequest(event -> stage.close());
		
		return loader.getController();
	}
}