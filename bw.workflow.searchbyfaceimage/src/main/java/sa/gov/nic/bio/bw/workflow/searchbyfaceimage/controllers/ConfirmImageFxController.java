package sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.utils.SearchByFaceImageErrorCodes;

@FxmlFile("confirmImage.fxml")
public class ConfirmImageFxController extends WizardStepFxControllerBase
{
	@Input private Long personId;
	@Input(alwaysRequired = true) private Image facePhoto;
	@Output private String facePhotoBase64;
	
	@FXML private Pane imagePane;
	@FXML private Pane personIdPane;
	@FXML private ImageView ivFinalImage;
	@FXML private Label lblPersonId;
	@FXML private Button btnPrevious;
	@FXML private Button btnConfirmImage;
	
	@Override
	protected void onAttachedToScene()
	{
		imagePane.maxWidthProperty().bind(Context.getCoreFxController().getBodyPane(getTabIndex()).widthProperty());
		imagePane.maxHeightProperty().bind(Context.getCoreFxController().getBodyPane(getTabIndex()).heightProperty());
		ivFinalImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivFinalImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		
		ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) ->
		{
			if(!newValue) // on un-maximize (workaround to fix JavaFX bug)
			{
				Platform.runLater(() ->
				{
				    imagePane.autosize();
				    Context.getCoreFxController().getBodyPane(getTabIndex()).autosize();
				});
			}
		};
		Context.getCoreFxController().getStage().maximizedProperty().addListener(changeListener);
		imagePane.sceneProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null) Context.getCoreFxController().getStage().maximizedProperty()
				                                                                .removeListener(changeListener);
		});
		
		if(personId != null)
		{
			GuiUtils.showNode(personIdPane, true);
			lblPersonId.setText(AppUtils.localizeNumbers(String.valueOf(personId)));
		}
		
		try
		{
			this.facePhotoBase64 = AppUtils.imageToBase64(facePhoto, "jpg");
		}
		catch(Exception e)
		{
			String errorCode = SearchByFaceImageErrorCodes.C005_00001.getCode();
			String[] errorDetails = {"Failed to convert image to Base64-encoded representation!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
			btnConfirmImage.setDisable(true);
		}
		
		Platform.runLater(() ->
		{
		    ivFinalImage.setImage(facePhoto);
		    GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFinalImage,
		                               resources.getString("label.finalImage"),
		                               resources.getString("label.contextMenu.showImage"), false);
		    imagePane.autosize();
		
		    // workaround to resolve the issue of not resizing sometimes
		    new Thread(() ->
		    {
		        try
		        {
		            Thread.sleep(1);
		        }
		        catch(InterruptedException e){e.printStackTrace();}
		        Platform.runLater(() -> imagePane.autosize());
		    }).start();
		});
	}
}