package sa.gov.nic.bio.bw.core.utils;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import sa.gov.nic.bio.bcl.utils.CancelCommand;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.IdleMonitorRegisterer;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DialogUtils
{
	public static String showChoiceDialogWithExtraCustomText(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer,
	                                                         String title, String headerText, String[] choices, String selectedChoice,
	                                                         String buttonConfirmText, boolean rtl, String customChoiceLabel, Function<String, Boolean> customChoiceValidator)
	{
		if(selectedChoice == null) selectedChoice = choices[0];
		
		var cboChoices = new ComboBox<String>();
		cboChoices.getItems().addAll(choices);
		cboChoices.getItems().addAll(customChoiceLabel);
		cboChoices.getSelectionModel().select(selectedChoice);
		
		var txtCustomChoice = new TextField();
		txtCustomChoice.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		txtCustomChoice.disableProperty().bind(cboChoices.getSelectionModel().selectedItemProperty().isNotEqualTo(customChoiceLabel));
		
		cboChoices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			txtCustomChoice.clear();
			
			if(customChoiceLabel.equals(newValue)) txtCustomChoice.requestFocus();
		});
		
		var vBox = new VBox(10.0);
		vBox.getChildren().addAll(cboChoices, txtCustomChoice);
		
		var dialog = new Dialog<ButtonType>();
		dialog.initOwner(ownerWindow);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setTitle(title);
		dialog.setHeaderText(headerText);
		dialog.getDialogPane().setContent(vBox);
		dialog.setOnShown(event -> cboChoices.requestFocus());
		
		Scene scene = dialog.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
		var stage = (Stage) scene.getWindow();
		stage.setAlwaysOnTop(true);
		stage.sizeToScene();
		
		var buttonTypeConfirm = new ButtonType(buttonConfirmText, ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().setAll(buttonTypeConfirm);
		
		var btnConfirm = (Button) dialog.getDialogPane().lookupButton(buttonTypeConfirm);
		btnConfirm.setDefaultButton(true);
		
		btnConfirm.disableProperty().bind(txtCustomChoice.disabledProperty().not().and(
						Bindings.createBooleanBinding(() -> customChoiceValidator != null && !customChoiceValidator.apply(txtCustomChoice.getText()), txtCustomChoice.textProperty())));
		
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.registerStageForIdleMonitoring((Stage) scene.getWindow());
		var optional = dialog.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring((Stage) scene.getWindow());
		
		var selectedButtonType = optional.orElse(null);
		
		if(selectedButtonType != buttonTypeConfirm) return null;
		
		String choice = cboChoices.getSelectionModel().getSelectedItem();
		
		if(choice == null) return null;
		
		if(choice.equals(customChoiceLabel)) return txtCustomChoice.getText();
		
		return choice;
	}
	
	public static void showAlertDialog(AlertType alertType, Window ownerWindow,
	                                   IdleMonitorRegisterer idleMonitorRegisterer, String title, String headerText,
	                                   String contentText, String extraDetailsText, String buttonText,
	                                   String buttonMoreDetailsText, String buttonLessDetailsText, boolean rtl)
	{
		Alert alert = new Alert(alertType);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
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
	                                      String title, String headerText, String[] choices, String selectedChoice,
	                                      String buttonConfirmText, boolean alwaysOnTop, boolean rtl)
	{
		if(selectedChoice == null) selectedChoice = choices[0];
		
		
		ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(selectedChoice, choices);
		choiceDialog.initOwner(ownerWindow);
		choiceDialog.initStyle(StageStyle.UTILITY);
		Scene scene = choiceDialog.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
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
		Optional<String> optional = choiceDialog.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring(stage);
		
		return optional.orElse(null);
	}
	
	public static boolean showConfirmationDialog(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer,
	                                             String title, String headerText, String contentText,
	                                             String buttonConfirmText, String buttonCancelText, boolean rtl)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
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
	
	public static String showButtonChoicesDialog(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer,
	                                            String title, String headerText, String contentText,
	                                            String[] buttonTexts, boolean rtl)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
		Stage stage = (Stage) scene.getWindow();
		alert.setTitle(title);
		
		if(headerText != null)
		{
			alert.setHeaderText(headerText);
			alert.setContentText(contentText);
		}
		else alert.setHeaderText(contentText);
		
		String[] selectedOption = new String[1];
		alert.getButtonTypes().clear();
		for(String buttonText : buttonTexts)
		{
			ButtonType buttonType = new ButtonType(buttonText, ButtonType.CANCEL.getButtonData());
			alert.getButtonTypes().add(buttonType);
			
			Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
			button.setOnKeyReleased(event ->
			{
			    if(event.getCode() == KeyCode.ENTER) selectedOption[0] = buttonText;
			});
			button.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> selectedOption[0] = buttonText);
		}
		
		stage.setOnCloseRequest(event -> selectedOption[0] = null);
		stage.sizeToScene();
		
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.registerStageForIdleMonitoring(stage);
		alert.showAndWait();
		if(idleMonitorRegisterer != null) idleMonitorRegisterer.unregisterStageForIdleMonitoring(stage);
		
		return selectedOption[0];
	}
	
	public static void showInformationDialog(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer,
	                                         String title, String headerText, String contentText, String buttonText,
	                                         boolean rtl)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
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
	
	public static void showWarningDialog(Window ownerWindow, IdleMonitorRegisterer idleMonitorRegisterer, String title,
	                                     String headerText, String contentText, String buttonText, boolean rtl)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(ownerWindow);
		Scene scene = alert.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
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
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
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
	
	public static <T> T buildCustomDialogByFxml(Stage ownerStage, Class<?> controllerClass, boolean resizable)
																									throws Exception
	{
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		FxmlFile fxmlFile = controllerClass.getAnnotation(FxmlFile.class);
		String packageName = controllerClass.getPackage().getName().replace('.', '/');
		String parentPackageName = packageName.substring(0, packageName.lastIndexOf('/'));
		URL fxmlUrl = controllerClass.getResource("/" + parentPackageName + "/fxml/" + fxmlFile.value());
		
		String moduleName = controllerClass.getModule().getName();
		CombinedResourceBundle resourceBundle = Context.getStringsResourceBundle();
		resourceBundle.setCurrentResourceBundleProviderModule(moduleName);
		
		FXMLLoader loader = new FXMLLoader(fxmlUrl, resourceBundle);
		Dialog<ButtonType> dialog = loader.load();
		
		dialog.initOwner(ownerStage);
		dialog.setResizable(resizable);
		
		Scene scene = dialog.getDialogPane().getScene();
		scene.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
		});
		
		Stage stage = (Stage) scene.getWindow();
		stage.getIcons().setAll(ownerStage.getIcons());
		stage.setOnCloseRequest(event -> stage.close());
		
		return loader.getController();
	}
	
	public static Stage buildProgressDialog(CancelCommand cancelCommand, String message, Future<?> future,
	                                        String cancelButtonText)
	{
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Button btnCancel = new Button(cancelButtonText);
		btnCancel.setFocusTraversable(false);
		Label lblProgress = new Label(message);
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setMaxHeight(18.0);
		progressIndicator.setMaxWidth(18.0);
		
		VBox inner = new VBox(5.0);
		inner.setAlignment(Pos.CENTER);
		inner.getChildren().addAll(progressIndicator, lblProgress);
		
		VBox outer = new VBox(15.0);
		outer.getStylesheets().setAll("/sa/gov/nic/bio/bw/core/css/style.css");
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
}