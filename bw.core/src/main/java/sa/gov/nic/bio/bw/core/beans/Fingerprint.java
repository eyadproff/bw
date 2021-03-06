package sa.gov.nic.bio.bw.core.beans;

import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;

public class Fingerprint extends JavaBean
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
}