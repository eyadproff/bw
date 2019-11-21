package sa.gov.nic.bio.bw.core.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.RefreshTokenBean;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.core.utils.DateTypeAdapter;
import sa.gov.nic.bio.bw.core.utils.LocalDateTypeAdapter;
import sa.gov.nic.bio.bw.core.utils.NormalizedStringTypeAdapter;
import sa.gov.nic.bio.bw.core.utils.ZonedDateTimeTypeAdapter;
import sa.gov.nic.bio.commons.TaskResponse;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WebserviceManager implements AppLogger
{
	public static final String PROTOCOL = "https";
	private static final String SSL_CERTIFICATE_FILE_PATH =
														"/sa/gov/nic/bio/bw/core/config/bio-middleware-certificate.pem";
	private static final String SSL_CERTIFICATE2_FILE_PATH =
														"/sa/gov/nic/bio/bw/core/config/semat-ssl-certificate.pem";
	private OkHttpClient okHttpClient;
	private Gson gson;
	private volatile Retrofit retrofit;
	private ScheduledFuture<?> scheduledRefreshTokenFuture;
	private Map<Class<?>, Object> apiCache = new HashMap<>();
	
	public void init(String baseUrl, int readTimeoutSeconds, int connectTimeoutSeconds) throws Exception
	{
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
			
			String appVersion = Context.getAppVersion();
			String os = System.getProperty("os.name");
			
			requestBuilder.addHeader("Client-Code", AppConstants.APP_CODE);
			if(appVersion != null) requestBuilder.addHeader("Client-Version", appVersion);
			if(os != null) requestBuilder.addHeader("Client-OS", os);
			
			return chain.proceed(requestBuilder.build());
		};
		
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream cert = getClass().getResourceAsStream(SSL_CERTIFICATE_FILE_PATH);
		InputStream cert2 = getClass().getResourceAsStream(SSL_CERTIFICATE2_FILE_PATH);
		
		Certificate certificate = cf.generateCertificate(cert);
		cert.close();
		Certificate certificate2 = cf.generateCertificate(cert2);
		cert2.close();
		
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(null, null);
		keyStore.setCertificateEntry("bio-middleware-certificate", certificate);
		keyStore.setCertificateEntry("semat-ssl-certificate", certificate2);
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);
		
		X509TrustManager x509Tm = null;
		for(TrustManager tm : tmf.getTrustManagers())
		{
			if(tm instanceof X509TrustManager)
			{
				x509Tm = (X509TrustManager) tm;
				break;
			}
		}
		
		if(x509Tm == null) throw new KeyManagementException("The system has no X509TrustManager!");
		
		final X509TrustManager finalTm = x509Tm;
		X509TrustManager customTm = new X509TrustManager()
		{
			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				return finalTm.getAcceptedIssuers();
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				finalTm.checkServerTrusted(chain, authType);
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				finalTm.checkClientTrusted(chain, authType);
			}
		};
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
		
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(LOGGER::fine);
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		
		okHttpClient = new OkHttpClient.Builder()
									   .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
									   .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
									   .addInterceptor(tokenInterceptor)
									   .addInterceptor(httpLoggingInterceptor)
									   .sslSocketFactory(sslContext.getSocketFactory(), customTm)
									   .build();
		
		gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
								.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
								.registerTypeAdapter(Date.class, new DateTypeAdapter())
								.registerTypeAdapter(String.class, new NormalizedStringTypeAdapter())
								.create();
		
		retrofit = new Retrofit.Builder()
							   .baseUrl(baseUrl)
							   .addConverterFactory(GsonConverterFactory.create(gson))
							   .client(okHttpClient)
							   .build();
	}
	
	public synchronized String getServerBaseUrl()
	{
		HttpUrl httpUrl = retrofit.baseUrl();
		return "http" + (httpUrl.isHttps() ? "s" : "") + "://" + httpUrl.host() + ":" + httpUrl.port();
	}
	
	public synchronized void changeServerBaseUrl(String baseUrl)
	{
		apiCache.clear();
		retrofit = new Retrofit.Builder()
				               .baseUrl(baseUrl)
				               .addConverterFactory(GsonConverterFactory.create(gson))
				               .client(okHttpClient)
				               .build();
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T getApi(Class<T> apiClass)
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
	public synchronized <T> TaskResponse<T> executeApi(Call<T> apiCall)
	{
		Request request = apiCall.request();
		String apiUrl = request.url().toString();
		String httpRequestMethod = request.method();
		RequestBody requestBody = request.body();
		String httpRequestBody = null;
		
		if(requestBody instanceof FormBody)
		{
			FormBody formBody = (FormBody) requestBody;
			
			try
			{
				Field encodedNamesField = FormBody.class.getDeclaredField("encodedNames");
				Field encodedValuesField = FormBody.class.getDeclaredField("encodedValues");
				
				encodedNamesField.setAccessible(true);
				encodedValuesField.setAccessible(true);
				
				@SuppressWarnings("unchecked")
				List<String> encodedNames = (List<String>) encodedNamesField.get(formBody);
				
				@SuppressWarnings("unchecked")
				List<String> encodedValues = (List<String>) encodedValuesField.get(formBody);
				
				if(encodedNames != null && encodedValues != null)
				{
					int size = encodedNames.size() > encodedValues.size() ? encodedNames.size() : encodedValues.size();
					
					StringBuilder sb = new StringBuilder("[\n");
					
					for(int i = 0; i < size; i++)
					{
						String key = null;
						String value = null;
						
						if(i < encodedNames.size()) key = URLDecoder.decode(encodedNames.get(i),
						                                                    StandardCharsets.UTF_8);
						if(i < encodedValues.size()) value = URLDecoder.decode(encodedValues.get(i),
						                                                       StandardCharsets.UTF_8);
						
						sb.append("  ");
						sb.append(key);
						sb.append("=");
						sb.append(value);
						sb.append("\n");
					}
					
					sb.append("]");
					
					httpRequestBody = sb.toString();
				}
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		
		Response<T> response;
		try
		{
			response = apiCall.execute();
		}
		catch(SocketTimeoutException e)
		{
			String errorCode = CoreErrorCodes.C002_00009.getCode();
			String[] errorDetails = {"Webservice timeout: " + apiUrl};
			return TaskResponse.failure(errorCode, e, errorDetails);
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00010.getCode();
			String[] errorDetails = {"Webservice IOException: " + apiUrl};
			return TaskResponse.failure(errorCode, e, errorDetails);
		}
		
		int httpResponseCode = response.code();
		LOGGER.info("httpRequestMethod = " + httpRequestMethod);
		LOGGER.info("apiUrl = " + apiUrl);
		LOGGER.fine("httpRequestBody = " + httpRequestBody);
		LOGGER.info("httpResponseCode = " + httpResponseCode);
		
		if(httpResponseCode == 200 || httpResponseCode == 202)
		{
			T resultBean = response.body();
			
			LOGGER.fine("resultBean = " + resultBean);
			
			return TaskResponse.success(resultBean, httpResponseCode);
		}
		else if(httpResponseCode == 400 || httpResponseCode == 401 || httpResponseCode == 403 ||
				httpResponseCode == 404 || httpResponseCode == 500)
		{
			String errorCode;
			ResponseBody errorBody = response.errorBody();
			
			if(errorBody == null)
			{
				errorCode = CoreErrorCodes.C002_00011.getCode();
				String[] errorDetails = {"\"errorBody\" is null!", "apiUrl = " + apiUrl,
										 "httpResponseCode = " + httpResponseCode};
				return TaskResponse.failure(httpResponseCode, errorCode, null, errorDetails);
			}
			
			try
			{
				String sErrorBody = errorBody.string();
				
				JsonObject jsonObject = JsonParser.parseString(sErrorBody).getAsJsonObject();
				errorCode = jsonObject.get("errorCode").getAsString(); // Business error, or server error
			}
			catch(Exception e)
			{
				errorCode = CoreErrorCodes.C002_00012.getCode();
				String[] errorDetails = {"failed to extract the error code from the error body!", "apiUrl = " + apiUrl,
										 "httpResponseCode = " + httpResponseCode};
				return TaskResponse.failure(httpResponseCode, errorCode, null, errorDetails);
			}
			
			LOGGER.info("errorCode = " + errorCode);
			
			String[] errorDetails = {"apiUrl = " + apiUrl, "httpResponseCode = " + httpResponseCode};
			return TaskResponse.failure(httpResponseCode, errorCode, null, errorDetails);
		}
		else // we don't support other HTTP codes
		{
			String errorCode = CoreErrorCodes.C002_00013.getCode();
			String[] errorDetails = {"Unsupported HTTP code!", "apiUrl = " + apiUrl, "httpResponseCode = " + httpResponseCode};
			return TaskResponse.failure(httpResponseCode, errorCode, null, errorDetails);
		}
	}
	
	public void scheduleRefreshToken(String userToken)
	{
		if(userToken == null) LOGGER.warning("userToken = null");
		else
		{
			ZonedDateTime issueDateTime = AppUtils.extractIssueTimeFromJWT(userToken);
			ZonedDateTime expirationDateTime = AppUtils.extractExpirationTimeFromJWT(userToken);
			
			if(issueDateTime != null && expirationDateTime != null)
			{
				long seconds = Math.abs(expirationDateTime.until(issueDateTime, ChronoUnit.SECONDS));
				long delay = seconds - seconds / 10L; // the last tenth of the token lifetime
				
				scheduledRefreshTokenFuture = Context.getScheduledExecutorService().schedule(() ->
				{
					RefreshTokenAPI refreshTokenAPI = Context.getWebserviceManager().getApi(RefreshTokenAPI.class);
				    Call<RefreshTokenBean> apiCall = refreshTokenAPI.refreshToken(userToken);
					TaskResponse<RefreshTokenBean> response = Context.getWebserviceManager().executeApi(apiCall);
				
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