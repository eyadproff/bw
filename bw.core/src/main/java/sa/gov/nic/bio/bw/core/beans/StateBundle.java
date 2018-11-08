package sa.gov.nic.bio.bw.core.beans;

import java.util.HashMap;
import java.util.Map;

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