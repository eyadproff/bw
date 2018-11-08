package sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.utils.SearchByFaceImageErrorCodes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Map;

@FxmlFile("uploadImage.fxml")
public class UploadImageFileFxController extends WizardStepFxControllerBase
{
	@Output private Image uploadedImage;
	
	@FXML private HBox imagePane;
	@FXML private ImageView ivUploadedImage;
	@FXML private Button btnSelectImage;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private FileChooser fileChooser = new FileChooser();
	private boolean imageSelected = false;
	
	@Override
	protected void onAttachedToScene()
	{
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
		
		fileChooser.setTitle(resources.getString("fileChooser.selectImage.title"));
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.selectImage.types"), "*.jpg");
		fileChooser.getExtensionFilters().addAll(extFilterJPG);
		
		imagePane.maxWidthProperty().bind(Context.getCoreFxController().getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(Context.getCoreFxController().getBodyPane().heightProperty());
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
		
		if(uploadedImage != null)
		{
			ivUploadedImage.setImage(uploadedImage);
			btnNext.setDisable(false);
			btnSelectImage.setText(resources.getString("button.selectNewImage"));
		}
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(imageSelected) uploadedImage = ivUploadedImage.getImage();
	}
	
	@FXML
	private void onSelectImageButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showOpenDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			try
			{
				long fileSizeBytes = Files.size(selectedFile.toPath());
				double fileSizeKB = fileSizeBytes / 1024.0;
				String maxFileSizeKbProperty =
										Context.getConfigManager().getProperty("config.uploadFaceImage.fileMaxSizeKB");
				
				double maxFileSizeKb = Double.parseDouble(maxFileSizeKbProperty);
				if(fileSizeKB > maxFileSizeKb)
				{
					DecimalFormat df = new DecimalFormat("#.00"); // 2 decimal places
					showWarningNotification(String.format(resources.getString(
							"selectNewFaceImage.fileChooser.exceedMaxFileSize"),
					                                      df.format(fileSizeKB), df.format(maxFileSizeKb)));
					return;
				}
			}
			catch(Exception e)
			{
				String errorCode = SearchByFaceImageErrorCodes.C005_00002.getCode();
				String[] errorDetails = {"Failed to retrieve the file size (" + selectedFile.getAbsolutePath() + ")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
			}
			
			Image image = new Image("file:///" + selectedFile.getAbsolutePath());
			
			Task<BufferedImage> task = new Task<BufferedImage>()
			{
				@Override
				protected BufferedImage call()
				{
					return SwingFXUtils.fromFXImage(image, null); // test if the file is really an image
				}
			};
			task.setOnSucceeded(event ->
			{
				try
				{
					BufferedImage value = task.getValue();
					
					if(value != null)
					{
						ivUploadedImage.setImage(image);
						imageSelected = true;
						btnNext.setDisable(false);
						btnSelectImage.setText(resources.getString("button.selectNewImage"));
						
						GuiUtils.attachImageDialog(Context.getCoreFxController(), ivUploadedImage,
						                           resources.getString("label.uploadedImage"),
						                           resources.getString("label.contextMenu.showImage"), false);
					}
					else showWarningNotification(resources.getString(
														"selectNewFaceImage.fileChooser.notImageFile"));
				}
				catch(Exception e)
				{
					String errorCode = SearchByFaceImageErrorCodes.C005_00003.getCode();
					String[] errorDetails = {"Failed to load the image (" + selectedFile.getAbsolutePath() + ")!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			});
			task.setOnFailed(event ->
			{
				String errorCode = SearchByFaceImageErrorCodes.C005_00004.getCode();
				String[] errorDetails = {"Failed to convert the selected file into an image (" +
																				selectedFile.getAbsolutePath() + ")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, task.getException(), errorDetails);
			});
			
			Context.getExecutorService().submit(task);
		}
	}
}