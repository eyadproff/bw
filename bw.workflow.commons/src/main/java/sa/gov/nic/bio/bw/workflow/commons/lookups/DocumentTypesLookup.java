package sa.gov.nic.bio.bw.workflow.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class DocumentTypesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.documentTypes";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>) Context.getUserSession().getAttribute(KEY);
		
		if(documentTypes == null)
		{
			LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<DocumentType>> call = api.lookupDocumentTypes();
			TaskResponse<List<DocumentType>> taskResponse = Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess()) documentTypes = taskResponse.getResult();
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, documentTypes);
		}
		
		return TaskResponse.success();
	}
}