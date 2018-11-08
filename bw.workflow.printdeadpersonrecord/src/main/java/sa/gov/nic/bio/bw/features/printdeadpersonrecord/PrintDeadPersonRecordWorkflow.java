package sa.gov.nic.bio.bw.features.printdeadpersonrecord;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.features.commons.workflow.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.workflow.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.features.printdeadpersonrecord.controllers.FetchingPersonInfoPaneFxController;
import sa.gov.nic.bio.bw.features.printdeadpersonrecord.controllers.RecordIdPaneFxController;
import sa.gov.nic.bio.bw.features.printdeadpersonrecord.controllers.ShowRecordPaneFxController;
import sa.gov.nic.bio.bw.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.features.printdeadpersonrecord.workflow.DeadPersonRecordByIdWorkflowTask;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(id = "menu.query.printDeadPersonRecord", title = "menu.title", order = 3,
				devices = Device.BIO_UTILITIES)
@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2c1", title = "wizard.enteringRecordId"),
		@Step(iconId = "user", title = "wizard.fetchingPersonInformation"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showRecord")})
public class PrintDeadPersonRecordWorkflow extends WizardWorkflowBase
{
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(RecordIdPaneFxController.class);
				
				passData(RecordIdPaneFxController.class, DeadPersonRecordByIdWorkflowTask.class,
				         "recordId");
				
				executeTask(DeadPersonRecordByIdWorkflowTask.class);
				
				break;
			}
			case 1:
			{
				renderUiAndWaitForUserInput(FetchingPersonInfoPaneFxController.class);
				
				DeadPersonRecord deadPersonRecord = getData(DeadPersonRecordByIdWorkflowTask.class,
				                                            "deadPersonRecord");
				Long samisId = deadPersonRecord.getSamisId();
				
				if(samisId != null)
				{
					setData(GetPersonInfoByIdWorkflowTask.class, "personId", samisId);
					executeTask(GetPersonInfoByIdWorkflowTask.class);
				}
				
				setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
				        "fingerprints", deadPersonRecord.getSubjFingers());
				setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
				        "missingFingerprints", deadPersonRecord.getSubjMissingFingers());
				
				executeTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				
				break;
			}
			case 2:
			{
				passData(RecordIdPaneFxController.class, ShowRecordPaneFxController.class,
				         "recordId");
				passData(DeadPersonRecordByIdWorkflowTask.class, ShowRecordPaneFxController.class,
				         "deadPersonRecord");
				passData(GetPersonInfoByIdWorkflowTask.class, ShowRecordPaneFxController.class,
				         "personInfo");
				passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
				         ShowRecordPaneFxController.class, "fingerprintBase64Images");
				
				renderUiAndWaitForUserInput(ShowRecordPaneFxController.class);
				break;
			}
		}
	}
}