package sa.gov.nic.bio.bw.workflow.registeriris.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.registeriris.controllers.VerificationMethodSelectionFxController.VerificationMethod;

@FxmlFile("verificationProgress.fxml")
public class VerificationProgressPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private VerificationMethod verificationMethod;
	@Input(requiredOnReturn = true) private Boolean matched;
	
	@FXML private Pane paneNotMatched;
	@FXML private Pane paneError;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblFingerprintVerificationInProgress;
	@FXML private Label lblFaceVerificationInProgress;
	@FXML private Label lblFingerprintVerificationNotMatched;
	@FXML private Label lblFaceVerificationNotMatched;
	@FXML private Label lblFingerprintVerificationError;
	@FXML private Label lblFaceVerificationError;
	@FXML private Button btnRetry;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.showNode(lblFingerprintVerificationInProgress,
		                  verificationMethod == VerificationMethod.FINGERPRINT);
		GuiUtils.showNode(lblFaceVerificationInProgress,
		                  verificationMethod == VerificationMethod.FACE_PHOTO);
		
		continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			if(!matched) goNext();
			else
			{
				GuiUtils.showNode(paneNotMatched, true);
				GuiUtils.showNode(lblFingerprintVerificationNotMatched,
				                  verificationMethod == VerificationMethod.FINGERPRINT);
				GuiUtils.showNode(lblFaceVerificationNotMatched,
				                  verificationMethod == VerificationMethod.FACE_PHOTO);
			}
		}
		else
		{
			GuiUtils.showNode(lblFingerprintVerificationError,
			                  verificationMethod == VerificationMethod.FINGERPRINT);
			GuiUtils.showNode(lblFaceVerificationError,
			                  verificationMethod == VerificationMethod.FACE_PHOTO);
			GuiUtils.showNode(btnRetry, true);
			GuiUtils.showNode(paneError, true);
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(paneError, false);
		GuiUtils.showNode(btnRetry, false);
		GuiUtils.showNode(lblFingerprintVerificationError, false);
		GuiUtils.showNode(lblFaceVerificationError, false);
		
		continueWorkflow();
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(btnStartOver, !bShow);
		GuiUtils.showNode(btnPrevious, !bShow);
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(lblFingerprintVerificationInProgress,
		                  bShow && verificationMethod == VerificationMethod.FINGERPRINT);
		GuiUtils.showNode(lblFaceVerificationInProgress,
		                  bShow && verificationMethod == VerificationMethod.FACE_PHOTO);
	}
}