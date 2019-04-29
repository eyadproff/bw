package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("fingerprintsSource.fxml")
public class FingerprintsSourceFxController extends WizardStepFxControllerBase
{
	public enum Source
	{
		ENTERING_PERSON_ID,
		ENTERING_CIVIL_BIOMETRICS_ID,
		ENTERING_CRIMINAL_BIOMETRICS_ID,
		SCANNING_FINGERPRINTS_CARD,
		UPLOADING_NIST_FILE,
		CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER
	}
	
	@Input private Boolean showLiveScanOption;
	@Output private Source fingerprintsSource;
	
	@FXML private RadioButton rbByEnteringCivilBiometricsId;
	@FXML private RadioButton rbByEnteringCriminalBiometricsId;
	@FXML private RadioButton rbByEnteringPersonId;
	@FXML private RadioButton rbByScanningFingerprintsCard;
	@FXML private RadioButton rbByUploadingNistFile;
	@FXML private RadioButton rbByCapturingFingerprintsViaScanner;
	@FXML private Button btnNext;
	
	private boolean minusOneStep = false;
	
	@Override
	protected void onAttachedToScene()
	{
		if(showLiveScanOption != null && showLiveScanOption)
		{
			GuiUtils.showNode(rbByCapturingFingerprintsViaScanner, true);
			rbByCapturingFingerprintsViaScanner.setDisable(false);
		}
		
		if(fingerprintsSource == Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)
		{
			minusOneStep = true;
			rbByCapturingFingerprintsViaScanner.setSelected(true);
			rbByCapturingFingerprintsViaScanner.requestFocus();
		}
		else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
		{
			minusOneStep = true;
			rbByEnteringCivilBiometricsId.setSelected(true);
			rbByEnteringCivilBiometricsId.requestFocus();
		}
		else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
		{
			minusOneStep = true;
			rbByEnteringCriminalBiometricsId.setSelected(true);
			rbByEnteringCriminalBiometricsId.requestFocus();
		}
		else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
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
		rbByEnteringCivilBiometricsId.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByEnteringCriminalBiometricsId.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByScanningFingerprintsCard.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByUploadingNistFile.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByCapturingFingerprintsViaScanner.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		
		String fingerprintCapturingTitle = resources.getString("wizard.fingerprintCapturing");
		String enterPersonIdTitle = resources.getString("wizard.enterPersonId");
		String enterCivilBiometricsId = resources.getString("wizard.enterCivilBiometricsId");
		String enterCriminalBiometricsId = resources.getString("wizard.enterCriminalBiometricsId");
		String scanFingerprintCardTitle = resources.getString("wizard.scanFingerprintCard");
		String uploadNistFileTitle = resources.getString("wizard.uploadNistFile");
		String showPersonInformationTitle = resources.getString("wizard.showPersonInformation");
		String specifyFingerprintCoordinatesTitle = resources.getString("wizard.specifyFingerprintCoordinates");
		
		rbByCapturingFingerprintsViaScanner.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane().updateStep(1, fingerprintCapturingTitle,
		                                                                 "\\uf256");
		
		        if(!minusOneStep)
		        {
		            Context.getCoreFxController().getWizardPane().removeStep(2);
		            minusOneStep = true;
		        }
		    }
		});
		rbByEnteringPersonId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				Context.getCoreFxController().getWizardPane().updateStep(1, enterPersonIdTitle,
				                                                         "\\uf2bb");
				
				if(minusOneStep)
				{
					Context.getCoreFxController().getWizardPane().addStep(2,
					                                                         showPersonInformationTitle,
					                                                         "\\uf2b9");
					minusOneStep = false;
				}
				else
				{
					Context.getCoreFxController().getWizardPane().updateStep(2,
					                                                         showPersonInformationTitle,
					                                                         "\\uf2b9");
				}
			}
		});
		rbByEnteringCivilBiometricsId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane().updateStep(1, enterCivilBiometricsId,
		                                                                 "\\uf2bb");
		
		        if(!minusOneStep)
		        {
		            Context.getCoreFxController().getWizardPane().removeStep(2);
		            minusOneStep = true;
		        }
		    }
		});
		rbByEnteringCriminalBiometricsId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane().updateStep(1, enterCriminalBiometricsId,
		                                                                 "\\uf2bb");
		
		        if(!minusOneStep)
		        {
		            Context.getCoreFxController().getWizardPane().removeStep(2);
		            minusOneStep = true;
		        }
		    }
		});
		rbByScanningFingerprintsCard.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
			    Context.getCoreFxController().getWizardPane().updateStep(1, scanFingerprintCardTitle,
			                                                             "file");
		    	
			    if(minusOneStep)
			    {
				    Context.getCoreFxController().getWizardPane().addStep(2,
				                                                          specifyFingerprintCoordinatesTitle,
				                                                          "\\uf247");
				    minusOneStep = false;
			    }
			    else
			    {
				    Context.getCoreFxController().getWizardPane().updateStep(2,
				                                                             specifyFingerprintCoordinatesTitle,
				                                                             "\\uf247");
			    }
		    }
		});
		rbByUploadingNistFile.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane().updateStep(1, uploadNistFileTitle,
		                                                                 "upload");
			
			    if(minusOneStep)
			    {
				    Context.getCoreFxController().getWizardPane().addStep(2,
				                                                          showPersonInformationTitle,
				                                                          "\\uf2b9");
				    minusOneStep = false;
			    }
			    else
			    {
				    Context.getCoreFxController().getWizardPane().updateStep(2,
				                                                             showPersonInformationTitle,
				                                                             "\\uf2b9");
			    }
		    }
		});
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(rbByEnteringPersonId.isSelected()) fingerprintsSource = Source.ENTERING_PERSON_ID;
		else if(rbByEnteringCivilBiometricsId.isSelected()) fingerprintsSource = Source.ENTERING_CIVIL_BIOMETRICS_ID;
		else if(rbByEnteringCriminalBiometricsId.isSelected()) fingerprintsSource =
																Source.ENTERING_CRIMINAL_BIOMETRICS_ID;
		else if(rbByScanningFingerprintsCard.isSelected()) fingerprintsSource = Source.SCANNING_FINGERPRINTS_CARD;
		else if(rbByUploadingNistFile.isSelected()) fingerprintsSource = Source.UPLOADING_NIST_FILE;
		else if(rbByCapturingFingerprintsViaScanner.isSelected()) fingerprintsSource =
																Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER;
	}
}