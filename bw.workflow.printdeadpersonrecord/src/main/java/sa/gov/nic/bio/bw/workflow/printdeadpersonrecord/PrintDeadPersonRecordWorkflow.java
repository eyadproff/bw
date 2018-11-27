package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers.FetchingPersonInfoPaneFxController;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers.RecordIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers.ShowRecordPaneFxController;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.beans.DeadPersonRecord;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.tasks.DeadPersonRecordByIdWorkflowTask;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(workflowId = 1006, menuId = "menu.query.printDeadPersonRecord", menuTitle = "menu.title", menuOrder = 3,
				devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
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
				
				executeWorkflowTask(DeadPersonRecordByIdWorkflowTask.class);
				
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
					executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
				}
				
				setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
				        "fingerprints", deadPersonRecord.getSubjFingers());
				setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
				        "missingFingerprints", deadPersonRecord.getSubjMissingFingers());
				
				executeWorkflowTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				
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