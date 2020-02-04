package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers;

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
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("fingerprintsSource.fxml")
public class FingerprintsSourceFxController extends WizardStepFxControllerBase
{
	public enum Source
	{
		SCANNING_FINGERPRINTS_CARD,
		UPLOADING_NIST_FILE,
	}
	
	@Output private Source fingerprintsSource;
	
	@FXML private RadioButton rbByScanningFingerprintsCard;
	@FXML private RadioButton rbByUploadingNistFile;
	@FXML private Button btnNext;
	
	private boolean minusOneStep = false;
	
	@Override
	protected void onAttachedToScene()
	{
		if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
		{
			rbByScanningFingerprintsCard.setSelected(true);
			rbByScanningFingerprintsCard.requestFocus();
		}
		else
		{
			rbByUploadingNistFile.setSelected(true);
			rbByUploadingNistFile.requestFocus();
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
		
		rbByScanningFingerprintsCard.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByUploadingNistFile.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		
		String scanFingerprintCardTitle = resources.getString("wizard.scanFingerprintCard");
		String uploadNistFileTitle = resources.getString("wizard.uploadNistFile");
		String showPersonInformationTitle = resources.getString("wizard.showPersonInformation");
		String specifyFingerprintCoordinatesTitle = resources.getString("wizard.specifyFingerprintCoordinates");
		
		rbByScanningFingerprintsCard.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
			    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(1, scanFingerprintCardTitle,
			                                                             "file");
		    	
			    if(minusOneStep)
			    {
				    Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(2,
				                                                          specifyFingerprintCoordinatesTitle,
				                                                          "\\uf247");
				    minusOneStep = false;
			    }
			    else
			    {
				    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(2,
				                                                             specifyFingerprintCoordinatesTitle,
				                                                             "\\uf247");
			    }
		    }
		});
		rbByUploadingNistFile.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(1, uploadNistFileTitle,
		                                                                 "upload");
			
			    if(minusOneStep)
			    {
				    Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(2,
				                                                          showPersonInformationTitle,
				                                                          "\\uf2b9");
				    minusOneStep = false;
			    }
			    else
			    {
				    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(2,
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
		if(rbByScanningFingerprintsCard.isSelected()) fingerprintsSource = Source.SCANNING_FINGERPRINTS_CARD;
		else if(rbByUploadingNistFile.isSelected()) fingerprintsSource = Source.UPLOADING_NIST_FILE;
	}
}