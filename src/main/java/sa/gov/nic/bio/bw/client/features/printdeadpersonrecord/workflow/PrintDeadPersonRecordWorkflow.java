package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.workflow.ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.FetchingPersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.RecordIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.ShowRecordPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
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