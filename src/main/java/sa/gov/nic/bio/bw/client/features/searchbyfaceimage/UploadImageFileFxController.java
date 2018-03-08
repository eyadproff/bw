package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.utils.SearchByFaceImageErrorCodes;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow.SearchByFaceImageWorkflow;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.ResourceBundle;

public class UploadImageFileFxController extends WizardStepFxControllerBase
{
	@FXML private ResourceBundle resources;
	@FXML private HBox imagePane;
	@FXML private ImageView ivUploadedImage;
	@FXML private Button btnSelectImage;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private FileChooser fileChooser = new FileChooser();
	private boolean imageSelected = false;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/uploadImage.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnSelectImage);
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
	}
	
	@Override
	public void onControllerReady()
	{
		fileChooser.setTitle(resources.getString("fileChooser.selectImage.title"));
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.selectImage.types"), "*.jpg");
		fileChooser.getExtensionFilters().addAll(extFilterJPG);
		
		imagePane.maxWidthProperty().bind(coreFxController.getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(coreFxController.getBodyPane().heightProperty());
		ivUploadedImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivUploadedImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		imagePane.autosize();
		
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
			Image uploadedImage = (Image) uiInputData.get(SearchByFaceImageWorkflow.KEY_UPLOADED_IMAGE);
			if(uploadedImage != null)
			{
				ivUploadedImage.setImage(uploadedImage);
				btnNext.setDisable(false);
				btnSelectImage.setText(resources.getString("button.selectNewImage"));
			}
		}
	}
	
	@Override
	protected void onLeaving(Map<String, Object> uiDataMap)
	{
		if(imageSelected) uiDataMap.put(SearchByFaceImageWorkflow.KEY_UPLOADED_IMAGE,
		                                ivUploadedImage.getImage());
	}
	
	@FXML
	private void onSelectImageButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showOpenDialog(coreFxController.getPrimaryStage());
		
		if(selectedFile != null)
		{
			try
			{
				long fileSizeBytes = Files.size(selectedFile.toPath());
				double fileSizeKB = fileSizeBytes / 1024.0;
				String maxFileSizeKbProperty = System.getProperty("jnlp.bio.bw.config.searchByFaceImage.fileMaxSizeKB");
				
				double maxFileSizeKb = Double.parseDouble(maxFileSizeKbProperty);
				if(fileSizeKB > maxFileSizeKb)
				{
					DecimalFormat df = new DecimalFormat("#.00"); // 2 decimal places
					showWarningNotification(String.format(stringsBundle.getString(
							"selectNewFaceImage.fileChooser.exceedMaxFileSize"),
					                                      df.format(fileSizeKB), df.format(maxFileSizeKb)));
					return;
				}
			}
			catch(Exception e)
			{
				String errorCode = SearchByFaceImageErrorCodes.C005_00002.getCode();
				String[] errorDetails = {"Failed to retrieve the file size (" + selectedFile.getAbsolutePath() + ")!"};
				coreFxController.showErrorDialog(errorCode, e, errorDetails);
			}
			
			try
			{
				Image image = new Image("file:///" + selectedFile.getAbsolutePath());
				ivUploadedImage.setImage(image);
				imageSelected = true;
				btnNext.setDisable(false);
				btnSelectImage.setText(resources.getString("button.selectNewImage"));
				
				GuiUtils.attachImageDialog(coreFxController, ivUploadedImage,
				                           resources.getString("label.uploadedImage"),
				                           resources.getString("label.contextMenu.showImage"));
			}
			catch(Exception e)
			{
				String errorCode = SearchByFaceImageErrorCodes.C005_00003.getCode();
				String[] errorDetails = {"Failed to load the image (" + selectedFile.getAbsolutePath() + ")!"};
				coreFxController.showErrorDialog(errorCode, e, errorDetails);
			}
		}
	}
}