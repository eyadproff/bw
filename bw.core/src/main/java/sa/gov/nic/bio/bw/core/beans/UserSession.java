package sa.gov.nic.bio.bw.core.beans;

import java.util.HashMap;
import java.util.Map;

public class UserSession
{
	private Map<String, Object> attributes = new HashMap<>();
	
	public void setAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}
	
	public void removeAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}
}