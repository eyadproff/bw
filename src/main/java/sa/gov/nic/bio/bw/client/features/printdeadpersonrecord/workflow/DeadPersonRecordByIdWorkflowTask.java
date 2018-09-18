package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecordById;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class DeadPersonRecordByIdWorkflowTask implements WorkflowTask
{
	@Input(required = true) private long recordId;
	
	@Override
	public ServiceResponse<?> execute()
	{
		DeadPersonRecordById deadPersonRecordById = Context.getWebserviceManager().getApi(DeadPersonRecordById.class);
		Call<DeadPersonRecord> apiCall = deadPersonRecordById.getDeadPersonRecordById(recordId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}