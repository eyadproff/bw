package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sa.gov.nic.bio.biokit.face.beans.GetIcaoImageResponse;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.workflow.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.PhotoQualityCheckWorkflowTask;

@FxmlFile("PhotoQualityCheckDialog.fxml")
public class PhotoQualityCheckDialogFxController extends BodyFxControllerBase
{
	@FXML private Pane buttonPane;
	@FXML private AutoScalingStackPane subScenePane;
	@FXML private FourStateTitledPane tpUploadedImage;
	@FXML private FourStateTitledPane tpCroppedImage;
	@FXML private ProgressIndicator piIcao;
	@FXML private ProgressIndicator piUploadedImage;
	@FXML private ProgressIndicator piCroppedImage;
	@FXML private ImageView ivSuccessIcao;
	@FXML private ImageView ivWarningIcao;
	@FXML private ImageView ivErrorIcao;
	@FXML private ImageView ivUploadedImagePlaceholder;
	@FXML private ImageView ivUploadedImage;
	@FXML private ImageView ivCroppedImagePlaceholder;
	@FXML private ImageView ivCroppedImage;
	@FXML private Label lblIcaoMessage;
	@FXML private Button btnCancel;
	@FXML private Dialog<ButtonType> dialog;
	
	private String photoBase64;
	
	public void setPhotoBase64(String photoBase64){this.photoBase64 = photoBase64;}
	
	@Override
	protected void initialize()
	{
		dialog.setOnShown(event ->
		{
			setData(PhotoQualityCheckWorkflowTask.class, "photoBase64", photoBase64);
			boolean success = executeUiTask(PhotoQualityCheckWorkflowTask.class, new SuccessHandler()
			{
				@Override
				protected void onSuccess()
				{
					GetIcaoImageResponse result = getData("result");
					System.out.println("result = " + result);
					
				}
			}, throwable ->
			{
			    //GuiUtils.showNode(paneLoadingInProgress, false);
			    //GuiUtils.showNode(paneLoadingError, true);
			
			    String errorCode = CommonsErrorCodes.C008_00030.getCode();
			    String[] errorDetails = {"failed to load the convicted report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
			});
			
			if(!success)
			{
				//GuiUtils.showNode(paneLoadingInProgress, false);
				//GuiUtils.showNode(paneLoadingError, true);
			}
			
			// workaround to center buttons and remove extra spaces
			ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
			buttonBar.getButtons().setAll(buttonPane);
			HBox hBox = (HBox) buttonBar.lookup(".container");
			hBox.setPadding(new Insets(0.0, 10.0, 10.0, 10.0));
			hBox.getChildren().remove(0);
			
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.sizeToScene();
			stage.setMinWidth(stage.getWidth());
			stage.setMinHeight(stage.getHeight());
		});
	}
	
	@FXML
	private void onCancelButtonClicked(ActionEvent actionEvent)
	{
	
	}
	
	public void showDialogAndWait()
	{
		dialog.showAndWait();
	}
}