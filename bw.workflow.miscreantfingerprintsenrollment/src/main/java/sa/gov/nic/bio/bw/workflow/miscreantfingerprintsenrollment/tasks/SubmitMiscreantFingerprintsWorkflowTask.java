package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.webservice.MiscreantFingerprintsAPI;

import java.util.List;

public class SubmitMiscreantFingerprintsWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long miscreantId;
	@Input(alwaysRequired = true) private List<Finger> fingerprints;
	@Input(alwaysRequired = true) private List<Integer> missingFingerprints;
	@Output private Long tcn;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(MiscreantFingerprintsAPI.class);
		var apiCall = api.registerCriminalFingerprints(workflowId, workflowTcn, miscreantId,
		                                               AppUtils.toJson(fingerprints),
		                                               AppUtils.toJson(missingFingerprints));
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		tcn = taskResponse.getResult().getTcn();
	}
}