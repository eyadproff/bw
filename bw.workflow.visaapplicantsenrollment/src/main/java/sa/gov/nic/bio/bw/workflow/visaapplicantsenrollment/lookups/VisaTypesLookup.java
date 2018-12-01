package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans.VisaTypeBean;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class VisaTypesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.visaTypes";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) Context.getUserSession().getAttribute(KEY);
		
		if(visaTypes == null)
		{
			LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<VisaTypeBean>> call = api.lookupVisaTypes();
			TaskResponse<List<VisaTypeBean>> taskResponse = Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess()) visaTypes = taskResponse.getResult();
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, visaTypes);
		}
		
		return TaskResponse.success();
	}
}