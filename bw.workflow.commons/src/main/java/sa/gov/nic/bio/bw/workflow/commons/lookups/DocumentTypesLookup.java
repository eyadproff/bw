package sa.gov.nic.bio.bw.workflow.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class DocumentTypesLookup implements Callable<TaskResponse<Void>>, AppLogger
{
	public static final String KEY = "lookups.documentTypes";
	
	@Override
	public TaskResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>) Context.getUserSession().getAttribute(KEY);
		
		if(documentTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<DocumentType>> documentTypesCall = lookupAPI.lookupDocumentTypes();
			TaskResponse<List<DocumentType>> documentTypesResponse = Context.getWebserviceManager()
																			   .executeApi(documentTypesCall);
			
			if(documentTypesResponse.isSuccess()) documentTypes = documentTypesResponse.getResult();
			else return TaskResponse.failure(documentTypesResponse.getErrorCode(),
			                                 documentTypesResponse.getException(),
			                                 documentTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, documentTypes);
			LOGGER.info(KEY + " = " + documentTypes);
		}
		
		return TaskResponse.success(null);
	}
}