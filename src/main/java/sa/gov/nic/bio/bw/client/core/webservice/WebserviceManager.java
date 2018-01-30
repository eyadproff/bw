package sa.gov.nic.bio.bw.client.core.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebserviceManager
{
	private static final Logger LOGGER = Logger.getLogger(WebserviceManager.class.getName());
	private static final String PROTOCOL = "http";
	private String baseUrl;
	private Retrofit retrofit;
	private ScheduledFuture<?> scheduledRefreshTokenFuture;
	private Map<Class<?>, Object> apiCache = new HashMap<>();
	
	public void init(String baseUrl, int readTimeoutSeconds, int connectTimeoutSeconds)
	{
		this.baseUrl = baseUrl;
		Interceptor tokenInterceptor = chain ->
		{
			Request.Builder requestBuilder = chain.request().newBuilder();
			
			UserSession userSession = Context.getUserSession();
			if(userSession != null)
			{
				String userToken = (String) userSession.getAttribute("userToken");
				
				if(userToken != null && userToken.length() > 0)
				{
					requestBuilder.addHeader("Authorization", "Bearer " + userToken);
				}
			}
			
			return chain.proceed(requestBuilder.build());
		};
		
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
													.readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
													.connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
													.addInterceptor(tokenInterceptor)
													.build();
		
		retrofit = new Retrofit.Builder()
							   .baseUrl(PROTOCOL + "://" + baseUrl)
							   .addConverterFactory(JacksonConverterFactory.create())
							   .client(okHttpClient)
							   .build();
	}
	
	public String getServerUrl()
	{
		return baseUrl;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getApi(Class<T> apiClass)
	{
		T api = (T) apiCache.get(apiClass);
		if(api == null)
		{
			api = retrofit.create(apiClass);
			apiCache.put(apiClass, api);
		}
		
		return api;
	}
	
	// synchronized because we send only one request at a time
	public synchronized <T> ApiResponse<T> executeApi(Call<T> apiCall)
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
		catch(Exception e)
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
			LOGGER.fine("resultBean = " + resultBean);
			return new ApiResponse<>(apiUrl, resultBean);
		}
		else if(httpCode == 400 || httpCode == 401 || httpCode == 403 || httpCode == 500)
		{
			String errorCode = null;
			
			ResponseBody errorBody = response.errorBody(); // can be null?
			String sErrorBody = null;
			
			if(errorBody == null) LOGGER.warning("cannot retrieve the errorCode because errorBody = null");
			else try
			{
				sErrorBody = errorBody.string();
				ObjectMapper mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(sErrorBody);
				JsonNode errorCodeJson = json.get("errorCode");
				errorCode = errorCodeJson.asText();
			}
			catch(Exception e)
			{
				LOGGER.log(Level.WARNING, "failed to extract the error code from the error body!", e);
			}
			
			if(errorCode == null) // the only case errorCode == null
			{
				LOGGER.warning("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode + ", errorCode = null, errorBody = " + sErrorBody);
			}
			else LOGGER.info("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode + ", errorCode = " + errorCode);
			
			return new ApiResponse<>(apiUrl, errorCode, httpCode);
		}
		else // we don't support other HTTP codes
		{
			LOGGER.warning("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode);
			
			String errorCode = "C002-00017";
			return new ApiResponse<>(apiUrl, errorCode, httpCode);
		}
	}
	
	public void scheduleRefreshToken(String userToken)
	{
		if(userToken == null) LOGGER.warning("userToken = null");
		else
		{
			LocalDateTime expiration = AppUtils.extractExpirationTimeFromJWT(userToken);
			if(expiration != null)
			{
				long seconds = Math.abs(expiration.until(LocalDateTime.now(), ChronoUnit.SECONDS));
				long delay = seconds - seconds / 10L; // the last tenth of the token lifetime
				
				scheduledRefreshTokenFuture = Context.getScheduledExecutorService().schedule(() ->
				{
				    IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
				    String url = System.getProperty("jnlp.bio.bw.service.refreshToken");
				    Call<RefreshTokenBean> apiCall = identityAPI.refreshToken(url, userToken);
				    ApiResponse<RefreshTokenBean> response = Context.getWebserviceManager().executeApi(apiCall);
				
				    if(response.isSuccess())
				    {
				        RefreshTokenBean refreshTokenBean = response.getResult();
				        String newToken = refreshTokenBean.getUserToken();
				
				        UserSession userSession = Context.getUserSession();
				        userSession.setAttribute("userToken", newToken);
				
				        scheduleRefreshToken(newToken);
				    }
				    else
				    {
				        LOGGER.warning("Failed to refresh the token! httpCode = " + response.getHttpCode() + ", errorCode = " + response.getErrorCode());
				    }
				}, delay, TimeUnit.SECONDS);
			}
		}
	}
	
	public void cancelRefreshTokenScheduler()
	{
		if(scheduledRefreshTokenFuture != null) scheduledRefreshTokenFuture.cancel(true);
	}
}