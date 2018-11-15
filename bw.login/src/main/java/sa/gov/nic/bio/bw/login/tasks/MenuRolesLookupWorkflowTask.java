package sa.gov.nic.bio.bw.login.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
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