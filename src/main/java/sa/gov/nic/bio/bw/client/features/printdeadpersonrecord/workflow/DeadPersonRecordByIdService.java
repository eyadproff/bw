package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecordById;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class DeadPersonRecordByIdService
{
	public static ServiceResponse<DeadPersonRecord> execute(long recordId)
	{
		DeadPersonRecordById deadPersonRecordById = Context.getWebserviceManager().getApi(DeadPersonRecordById.class);
		String url = System.getProperty("jnlp.bio.bw.service.getDeadPersonRecordById");
		
		Call<DeadPersonRecord> apiCall = deadPersonRecordById.getDeadPersonRecordById(url, recordId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}