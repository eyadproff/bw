package sa.gov.nic.bio.bw.client.login.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;
import java.util.Set;

public class MenuRolesLookupWorkflowTask implements WorkflowTask
{
	@Output private Map<String, Set<String>> menusRoles;
	
	@Override
	public void execute() throws Signal
	{
		LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
		Call<Map<String, Set<String>>> menusRolesCall = lookupAPI.lookupMenuRoles(AppConstants.APP_CODE);
		TaskResponse<Map<String, Set<String>>> menusRolesResponse = Context.getWebserviceManager()
																			  .executeApi(menusRolesCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(menusRolesResponse);
		menusRoles = menusRolesResponse.getResult();
	}
}