package sa.gov.nic.bio.bw.workflow.registeriris.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("verificationMethod.fxml")
public class VerificationMethodSelectionFxController extends WizardStepFxControllerBase
{
	public enum VerificationMethod
	{
		FINGERPRINT,
		FACE_PHOTO
	}
	
	@Output private VerificationMethod verificationMethod;
	
	@FXML private RadioButton rbByFingerprint;
	@FXML private RadioButton rbByFacePhoto;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		// go next on pressing ENTER on the radio buttons
		EventHandler<KeyEvent> eventHandler = event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				btnNext.fire();
				event.consume();
			}
		};
		rbByFingerprint.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByFacePhoto.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		
		String fingerprintCapturingTitle = resources.getString("wizard.singleFingerprintCapturing");
		String facePhotoCapturingTitle = resources.getString("wizard.facePhotoCapturing");
		
		// change the wizard-step-indicator upon changing the verification method
		int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
		                       .getStepIndexByTitle(fingerprintCapturingTitle);
		if(stepIndex < 0) stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
											 .getStepIndexByTitle(facePhotoCapturingTitle);
		
		final int finalStepIndex = stepIndex;
		
		rbByFingerprint.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue) Context.getCoreFxController()
		                        .getWizardPane(getTabIndex()).updateStep(finalStepIndex,
		                                                                 fingerprintCapturingTitle,
		                                                                 "\\uf25a");
		    else Context.getCoreFxController()
		                .getWizardPane(getTabIndex()).updateStep(finalStepIndex, facePhotoCapturingTitle,
		                                                         "camera");
		});
		
		// load the old state, if exists
		if(VerificationMethod.FACE_PHOTO.equals(verificationMethod))
		{
			rbByFacePhoto.setSelected(true);
			rbByFacePhoto.requestFocus();
		}
		else
		{
			rbByFingerprint.setSelected(true);
			rbByFingerprint.requestFocus();
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
		if(rbByFacePhoto.isSelected()) verificationMethod = VerificationMethod.FACE_PHOTO;
		else verificationMethod = VerificationMethod.FINGERPRINT;
	}
}