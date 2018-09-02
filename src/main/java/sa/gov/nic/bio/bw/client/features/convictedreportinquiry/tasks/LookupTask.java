package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonIdType;
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
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PersonIdType>> personIdTypesCall = lookupAPI.lookupPersonIdTypes();
			return Context.getWebserviceManager().executeApi(personIdTypesCall);
		}
	}
}