package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import javafx.application.Platform;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.FingerprintCardIdentificationPaneFxController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class FingerprintCardIdentificationWorkflow extends WorkflowBase<Void, Void>
{
	public FingerprintCardIdentificationWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                             BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> uiInputData = new HashMap<>();
		Platform.runLater(() -> Context.getCoreFxController().clearWizardBar());
		
		while(true)
		{
			formRenderer.get().renderForm(FingerprintCardIdentificationPaneFxController.class, uiInputData);
			waitForUserTask();
		}
	}
}