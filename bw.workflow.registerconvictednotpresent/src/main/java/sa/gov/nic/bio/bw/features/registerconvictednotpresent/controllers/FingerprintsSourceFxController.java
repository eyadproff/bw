package sa.gov.nic.bio.bw.features.registerconvictednotpresent.controllers;

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

@FxmlFile("fingerprintsSource.fxml")
public class FingerprintsSourceFxController extends WizardStepFxControllerBase
{
	public static final String KEY_FINGERPRINTS_SOURCE = "FINGERPRINTS_SOURCE";
	public static final String VALUE_FINGERPRINTS_SOURCE_ENTERING_PERSON_ID = "FINGERPRINTS_SOURCE_ENTERING_PERSON_ID";
	public static final String VALUE_FINGERPRINTS_SOURCE_SCANNING_FINGERPRINTS_CARD =
																	"FINGERPRINTS_SOURCE_SCANNING_FINGERPRINTS_CARD";
	public static final String VALUE_FINGERPRINTS_SOURCE_UPLOADING_NIST_FILE =
																			"FINGERPRINTS_SOURCE_UPLOADING_NIST_FILE";
	
	public enum Source
	{
		ENTERING_PERSON_ID,
		SCANNING_FINGERPRINTS_CARD,
		UPLOADING_NIST_FILE
	}
	
	@Output private Source fingerprintsSource;
	
	@FXML private RadioButton rbByEnteringPersonId;
	@FXML private RadioButton rbByScanningFingerprintsCard;
	@FXML private RadioButton rbByUploadingNistFile;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		btnNext.setOnAction(event -> goNext());
		
		// go next on pressing ENTER on the radio buttons
		EventHandler<KeyEvent> eventHandler = event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				btnNext.fire();
				event.consume();
			}
		};
		
		rbByEnteringPersonId.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByScanningFingerprintsCard.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByUploadingNistFile.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		
		String enterPersonIdTitle = resources.getString("wizard.enterPersonId");
		String scanFingerprintCardTitle = resources.getString("wizard.scanFingerprintCard");
		String uploadNistFileTitle = resources.getString("wizard.uploadNistFile");
		String showPersonInformationTitle = resources.getString("wizard.showPersonInformation");
		String specifyFingerprintCoordinatesTitle = resources.getString("wizard.specifyFingerprintCoordinates");
		
		int secondStepIndex = Context.getCoreFxController().getWizardPane().getStepIndexByTitle(enterPersonIdTitle);
		if(secondStepIndex < 0) secondStepIndex = Context.getCoreFxController().getWizardPane()
																   .getStepIndexByTitle(scanFingerprintCardTitle);
		if(secondStepIndex < 0) secondStepIndex = Context.getCoreFxController().getWizardPane()
																		.getStepIndexByTitle(uploadNistFileTitle);
		
		final int finalSecondStepIndex = secondStepIndex;
		
		int thirdStepIndex = Context.getCoreFxController().getWizardPane()
																	.getStepIndexByTitle(showPersonInformationTitle);
		if(thirdStepIndex < 0) thirdStepIndex = Context.getCoreFxController().getWizardPane()
															.getStepIndexByTitle(specifyFingerprintCoordinatesTitle);
		
		final int finalThirdStepIndex = thirdStepIndex;
		
		rbByEnteringPersonId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				Context.getCoreFxController().getWizardPane().updateStep(finalSecondStepIndex, enterPersonIdTitle,
				                                                         "\\uf2bb");
				Context.getCoreFxController().getWizardPane().updateStep(finalThirdStepIndex,
				                                                         showPersonInformationTitle,
				                                                         "\\uf2b9");
				Context.getCoreFxController().getWizardPane().updateStep(finalThirdStepIndex,
				                                                         showPersonInformationTitle,
				                                                         "\\uf2b9");
			}
		});
		rbByScanningFingerprintsCard.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane().updateStep(finalSecondStepIndex, scanFingerprintCardTitle,
		                                                                 "file");
		        Context.getCoreFxController().getWizardPane().updateStep(finalThirdStepIndex,
		                                                                 specifyFingerprintCoordinatesTitle,
		                                                                 "\\uf247");
		    }
		});
		rbByUploadingNistFile.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane().updateStep(finalSecondStepIndex, uploadNistFileTitle,
		                                                                 "upload");
		        Context.getCoreFxController().getWizardPane().updateStep(finalThirdStepIndex,
		                                                                 showPersonInformationTitle,
		                                                                 "\\uf2b9");
		    }
		});
		
		if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
		{
			rbByScanningFingerprintsCard.setSelected(true);
			rbByScanningFingerprintsCard.requestFocus();
		}
		else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
		{
			rbByUploadingNistFile.setSelected(true);
			rbByUploadingNistFile.requestFocus();
		}
		else
		{
			rbByEnteringPersonId.setSelected(true);
			rbByEnteringPersonId.requestFocus();
		}
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(rbByEnteringPersonId.isSelected()) fingerprintsSource = Source.ENTERING_PERSON_ID;
		else if(rbByScanningFingerprintsCard.isSelected()) fingerprintsSource = Source.SCANNING_FINGERPRINTS_CARD;
		else if(rbByUploadingNistFile.isSelected()) fingerprintsSource = Source.UPLOADING_NIST_FILE;
	}
}