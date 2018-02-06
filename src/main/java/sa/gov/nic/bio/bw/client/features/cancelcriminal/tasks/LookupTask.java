package sa.gov.nic.bio.bw.client.features.cancelcriminal.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice.PersonIdType;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class LookupTask extends Task<ServiceResponse<List<PersonIdType>>>
{
	@Override
	protected ServiceResponse<List<PersonIdType>> call()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked") List<PersonIdType> personIdTypes =
				(List<PersonIdType>) userSession.getAttribute("lookups.cancelCriminal.personIdTypes");
		
		if(personIdTypes != null) return ServiceResponse.success(personIdTypes);
		else
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupPersonIdTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PersonIdType>> personIdTypesCall = lookupAPI.lookupPersonIdTypes(url);
			return Context.getWebserviceManager().executeApi(personIdTypesCall);
		}
	}
}