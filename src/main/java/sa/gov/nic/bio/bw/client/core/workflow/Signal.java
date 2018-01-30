package sa.gov.nic.bio.bw.client.core.workflow;

import java.util.Map;
import java.util.Objects;

public class Signal extends Throwable
{
	private final Map<String, Object> payload;
	
	public Signal(Map<String, Object> payload)
	{
		this.payload = payload;
	}
	
	public Map<String, Object> getPayload(){return payload;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Signal signal = (Signal) o;
		return Objects.equals(payload, signal.payload);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(payload);
	}
	
	@Override
	public String toString()
	{
		return "Signal{" + "payload=" + payload + '}';
	}
}