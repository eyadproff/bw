package sa.gov.nic.bio.bw.client.core.beans;

import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;

import java.io.Serializable;
import java.util.Objects;

public class Fingerprint implements Serializable
{
	private DMFingerData dmFingerData;
	private boolean acceptableQuality;
	private boolean duplicated;
	
	public Fingerprint(){}
	
	public Fingerprint(DMFingerData dmFingerData, boolean acceptableQuality, boolean duplicated)
	{
		this.dmFingerData = dmFingerData;
		this.acceptableQuality = acceptableQuality;
		this.duplicated = duplicated;
	}
	
	public DMFingerData getDmFingerData(){return dmFingerData;}
	public void setDmFingerData(DMFingerData dmFingerData){this.dmFingerData = dmFingerData;}
	
	public boolean isAcceptableQuality(){return acceptableQuality;}
	public void setAcceptableQuality(boolean acceptableQuality){this.acceptableQuality = acceptableQuality;}
	
	public boolean isDuplicated(){return duplicated;}
	public void setDuplicated(boolean duplicated){this.duplicated = duplicated;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Fingerprint that = (Fingerprint) o;
		return acceptableQuality == that.acceptableQuality && duplicated == that.duplicated &&
			   Objects.equals(dmFingerData, that.dmFingerData);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(dmFingerData, acceptableQuality, duplicated);
	}
	
	@Override
	public String toString()
	{
		return "Fingerprint{" + "dmFingerData=" + dmFingerData + ", acceptableQuality=" + acceptableQuality +
			   ", duplicated=" + duplicated + '}';
	}
}