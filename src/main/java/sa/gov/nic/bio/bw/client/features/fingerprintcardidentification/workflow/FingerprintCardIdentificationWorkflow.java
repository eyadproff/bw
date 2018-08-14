package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.FingerprintsAfterCroppingPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.SpecifyFingerprintCoordinatesPaneFxController;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class FingerprintCardIdentificationWorkflow extends WizardWorkflowBase<Void, Void>
{
	public FingerprintCardIdentificationWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                             BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Map<String, Object> onStep(int step) throws InterruptedException, Signal
	{
		Map<String, Object> uiOutputData;
		
		switch(step)
		{
			case 0:
			{
				formRenderer.get().renderForm(ScanFingerprintCardPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 1:
			{
				formRenderer.get().renderForm(SpecifyFingerprintCoordinatesPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 2:
			{
				formRenderer.get().renderForm(FingerprintsAfterCroppingPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 3:
			{
				formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 4:
			{
				formRenderer.get().renderForm(InquiryResultPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			default:
			{
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
		}
		
		return uiOutputData;
	}
}