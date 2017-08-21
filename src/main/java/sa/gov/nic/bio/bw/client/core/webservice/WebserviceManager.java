package sa.gov.nic.bio.bw.client.core.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WebserviceManager
{
	private static final Logger LOGGER = Logger.getLogger(WebserviceManager.class.getName());
	private static final String PROTOCOL = "http";
	private Retrofit retrofit;
	private Map<Class<?>, Object> cache = new HashMap<>();
	
	public void init(String baseUrl)
	{
		retrofit = new Retrofit.Builder()
							   .baseUrl(PROTOCOL + "://" + baseUrl)
							   .addConverterFactory(JacksonConverterFactory.create())
							   .build();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getApi(Class<T> apiClass)
	{
		T api = (T) cache.get(apiClass);
		if(api == null)
		{
			api = retrofit.create(apiClass);
			cache.put(apiClass, api);
		}
		
		return api;
	}
	
	public <T> ApiResponse<T> executeApi(Call<T> apiCall)
	{
		String apiUrl = apiCall.request().url().toString();
		
		Response<T> response;
		try
		{
			response = apiCall.execute();
		}
		catch(SocketTimeoutException e)
		{
			String errorCode = "C002-00014";
			return new ApiResponse<>(apiUrl, errorCode, e);
		}
		catch(IOException e)
		{
			String errorCode = "C002-00015";
			return new ApiResponse<>(apiUrl, errorCode, e);
		}
		
		int httpCode = response.code();
		
		if(httpCode == 200)
		{
			T resultBean = response.body(); // can be null?
			if(resultBean == null)
			{
				String errorCode = "C002-00016";
				return new ApiResponse<>(apiUrl, errorCode, httpCode);
			}
			
			LOGGER.info("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode);
			return new ApiResponse<>(apiUrl, resultBean);
		}
		else if(httpCode == 400 || httpCode == 401 || httpCode == 403 || httpCode == 500)
		{
			String errorCode = null;
			
			ResponseBody errorBody = response.errorBody(); // can be null?
			
			if(errorBody == null) LOGGER.warning("cannot retrieve the errorCode because errorBody = null");
			else try
			{
				String sErrorBody = errorBody.string();
				ObjectMapper mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(sErrorBody);
				JsonNode errorCodeJson = json.get("errorCode");
				errorCode = errorCodeJson.asText();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			if(errorCode == null) // the only case errorCode == null
			{
				LOGGER.warning("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode + ", errorCode = null");
			}
			else LOGGER.info("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode + ", errorCode = " + errorCode);
			
			return new ApiResponse<>(apiUrl, errorCode, httpCode);
		}
		else // we don't support other HTTP codes
		{
			String errorCode = "C002-00017";
			return new ApiResponse<>(apiUrl, errorCode, httpCode);
		}
	}
}