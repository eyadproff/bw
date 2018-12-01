package sa.gov.nic.bio.bw.core.workflow;

import java.util.Map;
import java.util.Objects;

public class Signal extends Throwable
{
	private SignalType signalType;
	private Map<String, Object> payload;
	
	public Signal(SignalType signalType)
	{
		this.signalType = signalType;
	}
	
	public Signal(SignalType signalType, Map<String, Object> payload)
	{
		this.signalType = signalType;
		this.payload = payload;
	}
	
	public SignalType getSignalType(){return signalType;}
	public Map<String, Object> getPayload(){return payload;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Signal signal = (Signal) o;
		return signalType == signal.signalType && Objects.equals(payload, signal.payload);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(signalType, payload);
	}
	
	@Override
	public String toString()
	{
		return "Signal{" + "signalType=" + signalType + ", payload=" + payload + '}';
	}
}