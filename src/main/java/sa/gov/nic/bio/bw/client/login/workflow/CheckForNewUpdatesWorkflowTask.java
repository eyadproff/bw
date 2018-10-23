package sa.gov.nic.bio.bw.client.login.workflow;

import com.google.gson.Gson;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.login.utils.LoginErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;
import java.util.logging.Level;

public class CheckForNewUpdatesWorkflowTask implements WorkflowTask
{
	@Override
	public void execute() throws Signal
	{
		if(Context.getRuntimeEnvironment() != RuntimeEnvironment.LOCAL) // if not local, check for updates
		{
			String serverUrl = Context.getServerUrl();
			if(Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV) serverUrl = AppConstants.DEV_SERVER_URL;
			
			boolean newUpdates = BclUtils.checkForAppUpdates(serverUrl, "bw", false, json ->
			{
			    try
			    {
			        @SuppressWarnings("unchecked")
			        Map<String, Map<String, String>> map = new Gson().fromJson(json, Map.class);
			        return map;
			    }
			    catch(Exception e)
			    {
			        LOGGER.log(Level.WARNING, "Failed to parse the JSON while checking for new updates!!", e);
			    }
			
			    return null;
			});
			
			if(newUpdates) resetWorkflowStepIfNegativeOrNullTaskResponse(
														TaskResponse.failure(LoginErrorCodes.N003_00001.getCode()));
		}
	}
}