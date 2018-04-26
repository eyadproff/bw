package sa.gov.nic.bio.bw.client.core.beans;

import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;

import java.io.Serializable;
import java.util.Objects;

public class Fingerprint implements Serializable
{
	private DMFingerData dmFingerData;
	private String slapWsq;
	private String slapImage;
	private boolean acceptableFingerprintNfiq;
	private boolean acceptableFingerprintMinutiaeCount;
	private boolean acceptableFingerprintImageIntensity;
	private boolean acceptableQuality;
	private boolean duplicated;
	private boolean skipped;
	
	public Fingerprint(){}
	
	public Fingerprint(DMFingerData dmFingerData, String slapWsq, String slapImage)
	{
		this.dmFingerData = dmFingerData;
		this.slapWsq = slapWsq;
		this.slapImage = slapImage;
	}
	
	public Fingerprint(DMFingerData dmFingerData, String slapWsq, String slapImage, boolean acceptableFingerprintNfiq,
	                   boolean acceptableFingerprintMinutiaeCount, boolean acceptableFingerprintImageIntensity,
	                   boolean acceptableQuality, boolean duplicated, boolean skipped)
	{
		this.dmFingerData = dmFingerData;
		this.slapWsq = slapWsq;
		this.slapImage = slapImage;
		this.acceptableFingerprintNfiq = acceptableFingerprintNfiq;
		this.acceptableFingerprintMinutiaeCount = acceptableFingerprintMinutiaeCount;
		this.acceptableFingerprintImageIntensity = acceptableFingerprintImageIntensity;
		this.acceptableQuality = acceptableQuality;
		this.duplicated = duplicated;
		this.skipped = skipped;
	}
	
	public DMFingerData getDmFingerData(){return dmFingerData;}
	public void setDmFingerData(DMFingerData dmFingerData){this.dmFingerData = dmFingerData;}
	
	public String getSlapWsq(){return slapWsq;}
	public void setSlapWsq(String slapWsq){this.slapWsq = slapWsq;}
	
	public String getSlapImage(){return slapImage;}
	public void setSlapImage(String slapImage){this.slapImage = slapImage;}
	
	public boolean isAcceptableFingerprintNfiq(){return acceptableFingerprintNfiq;}
	public void setAcceptableFingerprintNfiq(boolean acceptableFingerprintNfiq)
	{this.acceptableFingerprintNfiq = acceptableFingerprintNfiq;}
	
	public boolean isAcceptableFingerprintMinutiaeCount(){return acceptableFingerprintMinutiaeCount;}
	public void setAcceptableFingerprintMinutiaeCount(boolean acceptableFingerprintMinutiaeCount)
	{this.acceptableFingerprintMinutiaeCount = acceptableFingerprintMinutiaeCount;}
	
	public boolean isAcceptableFingerprintImageIntensity(){return acceptableFingerprintImageIntensity;}
	public void setAcceptableFingerprintImageIntensity(boolean acceptableFingerprintImageIntensity)
	{this.acceptableFingerprintImageIntensity = acceptableFingerprintImageIntensity;}
	
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
		return acceptableFingerprintNfiq == that.acceptableFingerprintNfiq &&
			   acceptableFingerprintMinutiaeCount == that.acceptableFingerprintMinutiaeCount &&
			   acceptableFingerprintImageIntensity == that.acceptableFingerprintImageIntensity &&
			   acceptableQuality == that.acceptableQuality && duplicated == that.duplicated &&
			   skipped == that.skipped && Objects.equals(dmFingerData, that.dmFingerData) &&
			   Objects.equals(slapWsq, that.slapWsq) && Objects.equals(slapImage, that.slapImage);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(dmFingerData, slapWsq, slapImage, acceptableFingerprintNfiq,
		                    acceptableFingerprintMinutiaeCount, acceptableFingerprintImageIntensity, acceptableQuality,
		                    duplicated, skipped);
	}
	
	@Override
	public String toString()
	{
		return "Fingerprint{" + "dmFingerData=" + dmFingerData + ", slapWsq='" + slapWsq + '\'' + ", slapImage='" +
			   slapImage + '\'' + ", acceptableFingerprintNfiq=" + acceptableFingerprintNfiq +
			   ", acceptableFingerprintMinutiaeCount=" + acceptableFingerprintMinutiaeCount +
			   ", acceptableFingerprintImageIntensity=" + acceptableFingerprintImageIntensity +
			   ", acceptableQuality=" + acceptableQuality + ", duplicated=" + duplicated + ", skipped=" + skipped + '}';
	}
}