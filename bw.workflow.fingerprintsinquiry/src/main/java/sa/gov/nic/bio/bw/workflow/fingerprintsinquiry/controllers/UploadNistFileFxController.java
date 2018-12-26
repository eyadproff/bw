package sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.utils.FingerprintsInquiryErrorCodes;

import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;

@FxmlFile("uploadNist.fxml")
public class UploadNistFileFxController extends WizardStepFxControllerBase
{
	@Output private String filePath;
	
	@FXML private Pane paneFilePath;
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtFilePath;
	@FXML private Button btnSelectFile;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	protected void onAttachedToScene()
	{
		btnNext.setOnAction(event -> continueWorkflow());
		fileChooser.setTitle(resources.getString("fileChooser.selectFile.title"));
		
		if(filePath != null)
		{
			txtFilePath.setText(filePath);
			paneFilePath.setVisible(true);
			btnNext.setDisable(false);
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(btnSelectFile, !bShow);
		btnPrevious.setDisable(bShow);
		btnNext.setDisable(bShow);
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse) goNext();
	}
	
	@FXML
	private void onSelectFileButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showOpenDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			try
			{
				long fileSizeBytes = Files.size(selectedFile.toPath());
				double fileSizeKB = fileSizeBytes / 1024.0;
				String maxFileSizeKbProperty = Context.getConfigManager().getProperty("uploadNistFile.fileMaxSizeKB");
				
				double maxFileSizeKb = Double.parseDouble(maxFileSizeKbProperty);
				if(fileSizeKB > maxFileSizeKb)
				{
					DecimalFormat df = new DecimalFormat("#.00"); // 2 decimal places
					showWarningNotification(String.format(resources.getString(
														"selectNistFile.fileChooser.exceedMaxFileSize"),
					                                      df.format(fileSizeKB), df.format(maxFileSizeKb)));
					return;
				}
			}
			catch(Exception e)
			{
				String errorCode = FingerprintsInquiryErrorCodes.C013_00003.getCode();
				String[] errorDetails = {"Failed to retrieve the file size (" + selectedFile.getAbsolutePath() + ")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
			}
			
			filePath = selectedFile.getAbsolutePath();
			txtFilePath.setText(filePath);
			paneFilePath.setVisible(true);
			btnNext.setDisable(false);
		}
	}
}