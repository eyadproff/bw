package sa.gov.nic.bio.bw.client.features.faceverification;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.UploadImageFileFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.utils.SearchByFaceImageErrorCodes;

import java.net.URL;
import java.util.Map;

public class ConfirmInputFxController extends WizardStepFxControllerBase
{
	public static final String KEY_FINAL_IMAGE = "FINAL_IMAGE";
	public static final String KEY_FINAL_IMAGE_BASE64 = "FINAL_IMAGE_BASE64";
	
	@FXML private Pane imagePane;
	@FXML private ImageView ivFinalImage;
	@FXML private Label lblPersonId;
	@FXML private Button btnPrevious;
	@FXML private Button btnMatch;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/confirmInput.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(event -> goPrevious());
		btnMatch.setOnAction(event -> goNext());
	}
	
	@Override
	protected void onAttachedToScene()
	{
		imagePane.maxWidthProperty().bind(Context.getCoreFxController().getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(Context.getCoreFxController().getBodyPane().heightProperty());
		ivFinalImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivFinalImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		
		ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) ->
		{
			if(!newValue) // on un-maximize (workaround to fix JavaFX bug)
			{
				Platform.runLater(() ->
				{
				    imagePane.autosize();
				    Context.getCoreFxController().getBodyPane().autosize();
				});
			}
		};
		Context.getCoreFxController().getStage().maximizedProperty().addListener(changeListener);
		imagePane.sceneProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null) Context.getCoreFxController().getStage().maximizedProperty()
				                                                                .removeListener(changeListener);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			String imageSource = (String) uiInputData.get(ImageSourceFxController.KEY_IMAGE_SOURCE);
			Long personId = (Long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_ID);
			
			Image[] finalImage = new Image[1];
			
			if(ImageSourceFxController.VALUE_IMAGE_SOURCE_UPLOAD.equals(imageSource))
			{
				finalImage[0] = (Image) uiInputData.get(UploadImageFileFxController.KEY_UPLOADED_IMAGE);
			}
			else
			{
				Image capturedImage = (Image) uiInputData.get(FaceCapturingFxController.KEY_CAPTURED_IMAGE);
				Image croppedImage = (Image) uiInputData.get(FaceCapturingFxController.KEY_CROPPED_IMAGE);
				
				if(croppedImage != null) finalImage[0] = croppedImage;
				else finalImage[0] = capturedImage;
			}
			
			if(finalImage[0] != null)
			{
				try
				{
					String imageBase64 = AppUtils.imageToBase64(finalImage[0], "jpg");
					uiInputData.put(KEY_FINAL_IMAGE_BASE64, imageBase64);
					uiInputData.put(KEY_FINAL_IMAGE, finalImage[0]);
				}
				catch(Exception e)
				{
					String errorCode = SearchByFaceImageErrorCodes.C005_00001.getCode();
					String[] errorDetails = {"Failed to convert image to Base64-encoded representation!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
					btnMatch.setDisable(true);
				}
				
				Platform.runLater(() ->
				{
					ivFinalImage.setImage(finalImage[0]);
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
			
			lblPersonId.setText(String.valueOf(personId));
		}
	}
}