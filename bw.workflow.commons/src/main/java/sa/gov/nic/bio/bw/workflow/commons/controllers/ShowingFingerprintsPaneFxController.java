package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GeneratingNistFileWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FxmlFile("showingFingerprints.fxml")
public class ShowingFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	@Input private String facePhotoBase64;
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	@Output private List<Integer> missingFingerprints;
	
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivLeftThumb;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftLittle;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnPrevious;
	@FXML private Button btnGenerateNistFile;
	@FXML private Button btnInquiry;

	private FileChooser fileChooser = new FileChooser();
	
	@Override
	protected void onAttachedToScene()
	{
		missingFingerprints = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
		fingerprintBase64Images.keySet().forEach(missingFingerprints::remove);
		
		fileChooser.setTitle(resources.getString("fileChooser.saveNistFile.title"));
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter(
				resources.getString("fileChooser.saveNistFile.types"), "*.nst");
		fileChooser.getExtensionFilters().addAll(extFilterJPG);

		GuiUtils.attachFingerprintImages(fingerprintBase64Images, ivRightThumb, ivRightIndex, ivRightMiddle,
		                                 ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle,
		                                 ivLeftRing, ivLeftLittle);
		
		boolean disableInquiry = fingerprintBase64Images.isEmpty();
		btnInquiry.setDisable(disableInquiry);
		if(!disableInquiry) btnInquiry.requestFocus();
	}
	
	@FXML
	private void onGenerateNistFileButtonClicked(ActionEvent actionEvent)
	{
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());

		if(selectedFile != null)
		{
			showProgress(true);
			
			setData(GeneratingNistFileWorkflowTask.class, "facePhotoBase64", facePhotoBase64);
			setData(GeneratingNistFileWorkflowTask.class, "fingerprintBase64Images", fingerprintBase64Images);
			setData(GeneratingNistFileWorkflowTask.class, "nistOutputFilePath",
					selectedFile.getAbsolutePath());

			executeUiTask(GeneratingNistFileWorkflowTask.class, new SuccessHandler()
			{
				@Override
				protected void onSuccess()
				{
					showProgress(false);
					showSuccessNotification(resources.getString("generateNistFile.success.message"));
					AppUtils.openFileOrFolder(selectedFile.getParentFile());
				}
			}, throwable ->
			{
				showProgress(false);

				String errorCode = CommonsErrorCodes.C008_00050.getCode();
				String[] errorDetails = {"failed to generate the NIST file!"};
				Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
			});
		}
	}

	private void showProgress(boolean bShow)
	{
		GuiUtils.showNode(btnPrevious, !bShow);
		GuiUtils.showNode(btnGenerateNistFile, !bShow);
		GuiUtils.showNode(btnInquiry, !bShow);
		GuiUtils.showNode(piProgress, bShow);
	}
}