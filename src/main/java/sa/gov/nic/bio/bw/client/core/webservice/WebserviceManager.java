package sa.gov.nic.bio.bw.client.core.webservice;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.HashMap;
import java.util.Map;

public class WebserviceManager
{
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
}