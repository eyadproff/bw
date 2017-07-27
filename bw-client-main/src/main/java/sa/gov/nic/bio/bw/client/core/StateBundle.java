package sa.gov.nic.bio.bw.client.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fouad on 17-Jul-17.
 */
public class StateBundle
{
	private Map<String, Object> sMap = new HashMap<>();
	
	public StateBundle(){}
	
	public void putData(String key, Object value)
	{
		sMap.put(key, value);
	}
	
	public <T> T getDate(String key, Class<T> type)
	{
		return type.cast(sMap.get(key));
	}
}