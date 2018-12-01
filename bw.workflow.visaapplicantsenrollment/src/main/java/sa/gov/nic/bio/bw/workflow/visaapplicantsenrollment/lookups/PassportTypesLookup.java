package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans.PassportTypeBean;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class PassportTypesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.passportTypes";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<PassportTypeBean> passportTypes = (List<PassportTypeBean>) Context.getUserSession().getAttribute(KEY);
		
		if(passportTypes == null)
		{
			LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PassportTypeBean>> call = api.lookupPassportTypes();
			TaskResponse<List<PassportTypeBean>> taskResponse = Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess()) passportTypes = taskResponse.getResult();
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, passportTypes);
		}
		
		return TaskResponse.success();
	}
}