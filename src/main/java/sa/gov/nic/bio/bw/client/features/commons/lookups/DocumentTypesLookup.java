package sa.gov.nic.bio.bw.client.features.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class DocumentTypesLookup implements Callable<ServiceResponse<Void>>
{
	public static final String KEY = "lookups.documentTypes";
	private static final Logger LOGGER = Logger.getLogger(DocumentTypesLookup.class.getName());
	
	@Override
	public ServiceResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>) Context.getUserSession().getAttribute(KEY);
		
		if(documentTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<DocumentType>> documentTypesCall = lookupAPI.lookupDocumentTypes();
			ServiceResponse<List<DocumentType>> documentTypesResponse = Context.getWebserviceManager()
																			   .executeApi(documentTypesCall);
			
			if(documentTypesResponse.isSuccess()) documentTypes = documentTypesResponse.getResult();
			else return ServiceResponse.failure(documentTypesResponse.getErrorCode(),
			                                    documentTypesResponse.getException(),
			                                    documentTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, documentTypes);
			LOGGER.info(KEY + " = " + documentTypes);
		}
		
		return ServiceResponse.success(null);
	}
}