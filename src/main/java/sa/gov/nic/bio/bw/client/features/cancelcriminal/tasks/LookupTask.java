package sa.gov.nic.bio.bw.client.features.cancelcriminal.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class LookupTask extends Task<ServiceResponse<List<SamisIdType>>>
{
	@Override
	protected ServiceResponse<List<SamisIdType>> call()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked") List<SamisIdType> samisIdTypes =
				(List<SamisIdType>) userSession.getAttribute("lookups.cancelCriminal.samisIdTypes");
		
		if(samisIdTypes != null) return ServiceResponse.success(samisIdTypes);
		else
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<SamisIdType>> samisIdTypesCall = lookupAPI.lookupPersonIdTypes();
			return Context.getWebserviceManager().executeApi(samisIdTypesCall);
		}
	}
}