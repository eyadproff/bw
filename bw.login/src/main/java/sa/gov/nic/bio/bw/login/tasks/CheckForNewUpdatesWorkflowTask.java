package sa.gov.nic.bio.bw.login.tasks;

import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.login.utils.LoginErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;
import java.util.logging.Level;

public class CheckForNewUpdatesWorkflowTask extends WorkflowTask
{
	@Override
	public void execute() throws Signal
	{
		if(Context.getRuntimeEnvironment() != RuntimeEnvironment.LOCAL) // if not local, check for updates
		{
			String serverUrl = Context.getServerUrl();
			if(Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV) serverUrl = AppConstants.DEV_SERVER_URL;
			if(serverUrl.startsWith("http")) serverUrl = serverUrl.substring(serverUrl.indexOf("://") + 3);
			
			String jvmArch = System.getProperty("sun.arch.data.model");
			boolean jvmArch32 = "32".equals(jvmArch); // consider everything else as 64-bit
			String appCode = AppConstants.APP_CODE.toLowerCase();
			if(!jvmArch32) appCode = appCode + "64";
			
			String protocol = "http";
			if(serverUrl.startsWith("http"))
			{
				if(serverUrl.startsWith("https")) protocol = "https";
				serverUrl = serverUrl.substring(serverUrl.indexOf("://") + 3);
			}
			
			Boolean newUpdates = BclUtils.checkForAppUpdates(protocol, serverUrl, appCode, false, json ->
			{
			    try
			    {
			        @SuppressWarnings("unchecked")
			        Map<String, Map<String, String>> map = AppUtils.fromJson(json, Map.class);
			        return map;
			    }
			    catch(Exception e)
			    {
			        LOGGER.log(Level.WARNING, "Failed to parse the JSON while checking for new updates!!", e);
			    }
			
			    return null;
			});
			
			if(newUpdates == null)
			{
				LOGGER.warning("newUpdates = null");
				return;
			}
			
			if(newUpdates) resetWorkflowStepIfNegativeOrNullTaskResponse(
														TaskResponse.failure(LoginErrorCodes.N003_00001.getCode()));
		}
	}
}