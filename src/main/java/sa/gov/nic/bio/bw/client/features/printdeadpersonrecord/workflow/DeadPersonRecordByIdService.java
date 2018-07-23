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
		// TODO: REPLACE THIS ONCE THE WEBSERVICE IS READY
		if(true) return ServiceResponse.success(new DeadPersonRecord(1122463431L, 1532313770L,
		                                                             0L, null, null,
		                                                             null, "2135465"));
		
		DeadPersonRecordById deadPersonRecordById = Context.getWebserviceManager().getApi(DeadPersonRecordById.class);
		String url = System.getProperty("jnlp.bio.bw.service.getDeadPersonRecordById");
		
		Call<DeadPersonRecord> apiCall = deadPersonRecordById.getDeadPersonRecordById(url, recordId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}