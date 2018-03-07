package sa.gov.nic.bio.bw.client.core.beans;

import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;

import java.io.Serializable;
import java.util.Objects;

public class Fingerprint implements Serializable
{
	private DMFingerData dmFingerData;
	private boolean acceptableQuality;
	
	public Fingerprint(){}
	
	public Fingerprint(DMFingerData dmFingerData, boolean acceptableQuality)
	{
		this.dmFingerData = dmFingerData;
		this.acceptableQuality = acceptableQuality;
	}
	
	public DMFingerData getDmFingerData(){return dmFingerData;}
	public void setDmFingerData(DMFingerData dmFingerData){this.dmFingerData = dmFingerData;}
	
	public boolean isAcceptableQuality(){return acceptableQuality;}
	public void setAcceptableQuality(boolean acceptableQuality){this.acceptableQuality = acceptableQuality;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Fingerprint that = (Fingerprint) o;
		return acceptableQuality == that.acceptableQuality && Objects.equals(dmFingerData, that.dmFingerData);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(dmFingerData, acceptableQuality);
	}
	
	@Override
	public String toString()
	{
		return "Fingerprint{" + "dmFingerData=" + dmFingerData + ", acceptableQuality=" + acceptableQuality + '}';
	}
}