package sa.gov.nic.bio.bw.client.core.beans;

import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;

import java.io.Serializable;
import java.util.Objects;

public class Fingerprint implements Serializable
{
	private DMFingerData dmFingerData;
	private boolean acceptableQuality;
	private boolean duplicated;
	private boolean skipped;
	
	public Fingerprint(){}
	
	public Fingerprint(DMFingerData dmFingerData, boolean acceptableQuality, boolean duplicated, boolean skipped)
	{
		this.dmFingerData = dmFingerData;
		this.acceptableQuality = acceptableQuality;
		this.duplicated = duplicated;
		this.skipped = skipped;
	}
	
	public DMFingerData getDmFingerData(){return dmFingerData;}
	public void setDmFingerData(DMFingerData dmFingerData){this.dmFingerData = dmFingerData;}
	
	public boolean isAcceptableQuality(){return acceptableQuality;}
	public void setAcceptableQuality(boolean acceptableQuality){this.acceptableQuality = acceptableQuality;}
	
	public boolean isDuplicated(){return duplicated;}
	public void setDuplicated(boolean duplicated){this.duplicated = duplicated;}
	
	public boolean isSkipped(){return skipped;}
	public void setSkipped(boolean skipped){this.skipped = skipped;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Fingerprint that = (Fingerprint) o;
		return acceptableQuality == that.acceptableQuality && duplicated == that.duplicated &&
			   skipped == that.skipped && Objects.equals(dmFingerData, that.dmFingerData);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(dmFingerData, acceptableQuality, duplicated, skipped);
	}
	
	@Override
	public String toString()
	{
		return "Fingerprint{" + "dmFingerData=" + dmFingerData + ", acceptableQuality=" + acceptableQuality +
			   ", duplicated=" + duplicated + ", skipped=" + skipped + '}';
	}
}