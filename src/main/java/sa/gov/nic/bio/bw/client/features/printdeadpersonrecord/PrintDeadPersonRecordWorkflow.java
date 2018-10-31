package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord;

import sa.gov.nic.bio.bw.client.core.utils.Device;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.workflow.ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.controllers.FetchingPersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.controllers.RecordIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.controllers.ShowRecordPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.workflow.DeadPersonRecordByIdWorkflowTask;

@AssociatedMenu(id = "menu.query.printDeadPersonRecord", title = "menu.title", order = 3,
				devices = Device.BIO_UTILITIES)
@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2c1", title = "wizard.enteringRecordId"),
		@Step(iconId = "user", title = "wizard.fetchingPersonInformation"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showRecord")})
public class PrintDeadPersonRecordWorkflow extends WizardWorkflowBase
{
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
				
				setData(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class,
				        "fingerprints", deadPersonRecord.getSubjFingers());
				setData(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class,
				        "missingFingerprints", deadPersonRecord.getSubjMissingFingers());
				
				executeTask(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class);
				
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
				passData(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class,
				         ShowRecordPaneFxController.class, "fingerprintImages");
				
				renderUiAndWaitForUserInput(ShowRecordPaneFxController.class);
				break;
			}
		}
	}
}