package sa.gov.nic.bio.bw.client.core.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.client.core.utils.NormalizationStringTypeAdapter;
import sa.gov.nic.bio.bw.client.core.utils.UnixEpochDateTypeAdapter;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
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
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new UnixEpochDateTypeAdapter())
									 .registerTypeAdapter(String.class, new NormalizationStringTypeAdapter())
									 .create();
		
		retrofit = new Retrofit.Builder()
							   .baseUrl(PROTOCOL + "://" + baseUrl)
							   .addConverterFactory(GsonConverterFactory.create(gson))
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
	public synchronized <T> ServiceResponse<T> executeApi(Call<T> apiCall)
	{
		String apiUrl = apiCall.request().url().toString();
		
		Response<T> response;
		try
		{
			response = apiCall.execute();
		}
		catch(SocketTimeoutException e)
		{
			String errorCode = CoreErrorCodes.C002_00011.getCode();
			String[] errorDetails = {"webservice timeout!"};
			return ServiceResponse.failure(errorCode, e, errorDetails);
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00012.getCode();
			String[] errorDetails = {"webservice IOException!"};
			return ServiceResponse.failure(errorCode, e, errorDetails);
		}
		
		int httpCode = response.code();
		
		if(httpCode == 200)
		{
			T resultBean = response.body();
			
			LOGGER.info("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode);
			LOGGER.fine("resultBean = " + resultBean);
			
			return ServiceResponse.success(resultBean);
		}
		else if(httpCode == 400 || httpCode == 401 || httpCode == 403 || httpCode == 500)
		{
			String errorCode;
			ResponseBody errorBody = response.errorBody();
			
			if(errorBody == null)
			{
				errorCode = CoreErrorCodes.C002_00013.getCode();
				String[] errorDetails = {"\"errorBody\" is null!", "apiUrl = " + apiUrl, "httpCode = " + httpCode};
				return ServiceResponse.failure(errorCode, null, errorDetails);
			}
			
			try
			{
				String sErrorBody = errorBody.string();
				
				JsonObject jsonObject = new JsonParser().parse(sErrorBody).getAsJsonObject();
				errorCode = jsonObject.get("errorCode").getAsString(); // Business error, or server error
			}
			catch(Exception e)
			{
				errorCode = CoreErrorCodes.C002_00014.getCode();
				String[] errorDetails = {"failed to extract the error code from the error body!", "apiUrl = " + apiUrl,
										 "httpCode = " + httpCode};
				return ServiceResponse.failure(errorCode, null, errorDetails);
			}
			
			LOGGER.info("webservice = \"" + apiCall.request().url() + "\", responseCode = " + httpCode +
					    ", errorCode = " + errorCode);
			
			String[] errorDetails = {"apiUrl = " + apiUrl, "httpCode = " + httpCode};
			return ServiceResponse.failure(errorCode, null, errorDetails);
		}
		else // we don't support other HTTP codes
		{
			String errorCode = CoreErrorCodes.C002_00015.getCode();
			String[] errorDetails = {"Unsupported HTTP code!", "apiUrl = " + apiUrl, "httpCode = " + httpCode};
			return ServiceResponse.failure(errorCode, null, errorDetails);
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
					ServiceResponse<RefreshTokenBean> response = Context.getWebserviceManager().executeApi(apiCall);
				
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
					    Exception exception = response.getException();
					    String errorMessage = "Failed to refresh the token! errorCode = " + response.getErrorCode() +
							                  ", errorDetails = " + Arrays.toString(response.getErrorDetails());
				    	
					    if(exception != null) LOGGER.log(Level.WARNING, errorMessage, exception);
					    else LOGGER.warning(errorMessage);
				        
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