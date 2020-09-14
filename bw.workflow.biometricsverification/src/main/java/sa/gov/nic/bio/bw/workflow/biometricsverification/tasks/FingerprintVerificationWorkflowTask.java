package sa.gov.nic.bio.bw.workflow.biometricsverification.tasks;

import com.google.gson.Gson;
import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.biometricsverification.webservice.FingerprintVerificationAPI;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.biometricsverification.beans.MatchingResponse;
import sa.gov.nic.bio.commons.TaskResponse;

public class FingerprintVerificationWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true) private Long personId;
    @Input(alwaysRequired = true) private Finger fingerprint;
    @Input(alwaysRequired = true) private Integer fingerPosition;
    @Output private MatchingResponse matchingResponse;

    @Override
    public void execute() throws Signal {

        var api = Context.getWebserviceManager().getApi(FingerprintVerificationAPI.class);
        Call<PersonInfo> apiCall = api.verifyFingerprint(workflowId, workflowTcn, personId,
                fingerprint.getImage(), fingerPosition);

        TaskResponse<PersonInfo> taskResponse = Context.getWebserviceManager().executeApi(apiCall);


        if (taskResponse.isSuccess()) {
            PersonInfo personInfo = taskResponse.getResult();
            matchingResponse = new MatchingResponse(true, personInfo);
        }
        else {
			if ("B003-0005".equals(taskResponse.getErrorCode())) // not matched
			{
                matchingResponse = new MatchingResponse(false, null);
			}
			else { resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse); }
        }
    }
}