package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.utils.SearchByFaceImageErrorCodes;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow.SearchByFaceImageWorkflow;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.ResourceBundle;

public class ConfirmImageFxController extends WizardStepFxControllerBase
{
	@FXML private ResourceBundle resources;
	@FXML private HBox imagePane;
	@FXML private ImageView ivFinalImage;
	@FXML private Button btnPrevious;
	@FXML private Button btnSearch;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/confirmImage.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnSearch);
		
		btnPrevious.setOnAction(event -> goPrevious());
		btnSearch.setOnAction(event -> goNext());
	}
	
	@Override
	public void onControllerReady()
	{
		imagePane.maxWidthProperty().bind(coreFxController.getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(coreFxController.getBodyPane().heightProperty());
		ivFinalImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivFinalImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		
		ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) ->
		{
			if(!newValue) // on un-maximize (workaround to fix JavaFX bug)
			{
				Platform.runLater(() ->
				{
				    imagePane.autosize();
				    coreFxController.getBodyPane().autosize();
				});
			}
		};
		coreFxController.getPrimaryStage().maximizedProperty().addListener(changeListener);
		imagePane.sceneProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null) coreFxController.getPrimaryStage().maximizedProperty().removeListener(changeListener);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			String imageSource = (String) uiInputData.get(SearchByFaceImageWorkflow.KEY_IMAGE_SOURCE);
			
			Image[] finalImage = new Image[1];
			
			if(SearchByFaceImageWorkflow.VALUE_IMAGE_SOURCE_UPLOAD.equals(imageSource))
			{
				finalImage[0] = (Image) uiInputData.get(SearchByFaceImageWorkflow.KEY_UPLOADED_IMAGE);
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
					ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
					ImageIO.write(SwingFXUtils.fromFXImage(finalImage[0], null), "jpg", byteOutput);
					byte[] bytes = byteOutput.toByteArray();
					String imageBase64 = Base64.getEncoder().encodeToString(bytes);
					uiInputData.put(SearchByFaceImageWorkflow.KEY_FINAL_IMAGE_BASE64, imageBase64);
					uiInputData.put(SearchByFaceImageWorkflow.KEY_FINAL_IMAGE, finalImage[0]);
				}
				catch(Exception e)
				{
					String errorCode = SearchByFaceImageErrorCodes.C005_00001.getCode();
					String[] errorDetails = {"Failed to convert image to Base64-encoded representation!"};
					coreFxController.showErrorDialog(errorCode, e, errorDetails);
					btnSearch.setDisable(true);
				}
				
				Platform.runLater(() ->
				{
					ivFinalImage.setImage(finalImage[0]);
					GuiUtils.attachImageDialog(coreFxController, ivFinalImage,
					                           resources.getString("label.finalImage"),
					                           resources.getString("label.contextMenu.showImage"));
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
	}
}