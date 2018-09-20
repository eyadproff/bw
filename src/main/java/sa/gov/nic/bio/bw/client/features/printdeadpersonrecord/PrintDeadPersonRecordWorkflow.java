package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
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

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.query.printDeadPersonRecord", title = "menu.title", order = 3,
				devices = Device.BIO_UTILITIES)
@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2c1", title = "wizard.enteringRecordId"),
		@Step(iconId = "user", title = "wizard.fetchingPersonInformation"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showRecord")})
public class PrintDeadPersonRecordWorkflow extends WizardWorkflowBase
{
	public PrintDeadPersonRecordWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                     BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUi(RecordIdPaneFxController.class);
				waitForUserInput();
				
				passData(RecordIdPaneFxController.class, DeadPersonRecordByIdWorkflowTask.class,
				         "recordId");
				
				executeTask(DeadPersonRecordByIdWorkflowTask.class);
				
				return true;
			}
			case 1:
			{
				renderUi(FetchingPersonInfoPaneFxController.class);
				waitForUserInput();
				
				DeadPersonRecord deadPersonRecord = getData(RecordIdPaneFxController.class,
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
				
				return true;
			}
			case 2:
			{
				passData(RecordIdPaneFxController.class, ShowRecordPaneFxController.class,
				         "recordId");
				passData(RecordIdPaneFxController.class, ShowRecordPaneFxController.class,
				         "deadPersonRecord");
				passData(GetPersonInfoByIdWorkflowTask.class, ShowRecordPaneFxController.class,
				         "personInfo");
				passData(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class,
				         ShowRecordPaneFxController.class, "fingerprintImages");
				
				renderUi(ShowRecordPaneFxController.class);
				waitForUserInput();
				return true;
			}
			default: return false;
		}
	}
}