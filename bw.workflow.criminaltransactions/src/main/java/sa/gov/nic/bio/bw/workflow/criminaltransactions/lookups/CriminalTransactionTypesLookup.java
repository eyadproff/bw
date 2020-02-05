package sa.gov.nic.bio.bw.workflow.criminaltransactions.lookups;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransactionType;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class CriminalTransactionTypesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.criminalTransactionTypes";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		var criminalTransactionTypes = (List<CriminalTransactionType>) Context.getUserSession().getAttribute(KEY);
		
		if(criminalTransactionTypes == null)
		{
			var api = Context.getWebserviceManager().getApi(LookupAPI.class);
			var call = api.lookupCriminalTransactionTypes();
			var taskResponse = Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess()) criminalTransactionTypes = taskResponse.getResult();
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, criminalTransactionTypes);
		}
		
		return TaskResponse.success();
	}
}