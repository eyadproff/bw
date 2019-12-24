package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintsRegistrationResponse;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.CriminalFingerprintsAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class SubmitCriminalFingerprintsWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long criminalBiometricsId;
	@Input(alwaysRequired = true) private List<Finger> fingerprints;
	@Input private List<Finger> palms;
	@Input(alwaysRequired = true) private List<Integer> missingFingerprints;
	@Output private Long tcn;
	
	@Override
	public void execute() throws Signal
	{
		if(palms != null && !palms.isEmpty()) fingerprints.addAll(palms);
		
		CriminalFingerprintsAPI api = Context.getWebserviceManager().getApi(CriminalFingerprintsAPI.class);
		Call<CriminalFingerprintsRegistrationResponse> apiCall = api.registerCriminalFingerprints(
																				  workflowId,
                                                                                  workflowTcn,
                                                                                  criminalBiometricsId,
                                                                                  AppUtils.toJson(fingerprints),
                                                                                  null,
                                                                                  AppUtils.toJson(missingFingerprints));
		TaskResponse<CriminalFingerprintsRegistrationResponse> taskResponse = Context.getWebserviceManager()
																					 .executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		tcn = taskResponse.getResult().getTcn();
	}
}