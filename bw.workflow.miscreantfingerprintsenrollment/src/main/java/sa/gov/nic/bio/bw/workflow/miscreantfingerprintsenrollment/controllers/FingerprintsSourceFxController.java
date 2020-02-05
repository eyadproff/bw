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
	
	@FXML private RadioButton rbByUploadingNistFile;
	@FXML private RadioButton rbByScanningFingerprintsCard;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		System.out.println("fingerprintsSource = " + fingerprintsSource);
		
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
		
		rbByUploadingNistFile.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByScanningFingerprintsCard.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		
		String uploadNistFileTitle = resources.getString("wizard.uploadNistFile");
		String showPersonInformationTitle = resources.getString("wizard.showPersonInformation");
		String scanFingerprintCardTitle = resources.getString("wizard.scanFingerprintCard");
		String specifyFingerprintCoordinatesTitle = resources.getString("wizard.specifyFingerprintCoordinates");
		
		rbByUploadingNistFile.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
		        Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(3, uploadNistFileTitle,
		                                                                              "upload");
			    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(4,
			                                                                          showPersonInformationTitle,
			                                                                          "\\uf2b9");
		    }
		});
		rbByScanningFingerprintsCard.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
			    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(3, scanFingerprintCardTitle,
			                                                             "file");
			    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(4,
			                                                                          specifyFingerprintCoordinatesTitle,
			                                                                          "\\uf247");
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
		if(rbByUploadingNistFile.isSelected()) fingerprintsSource = Source.UPLOADING_NIST_FILE;
		else if(rbByScanningFingerprintsCard.isSelected()) fingerprintsSource = Source.SCANNING_FINGERPRINTS_CARD;
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
}