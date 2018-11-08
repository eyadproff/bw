package sa.gov.nic.bio.bw.core.beans;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;

import java.io.Serializable;
import java.util.Objects;

public class FingerprintQualityThreshold implements Serializable
{
	private int maximumAcceptableNFIQ;
	private int minimumAcceptableMinutiaeCount;
	private int minimumAcceptableImageIntensity;
	private int maximumAcceptableImageIntensity;
	
	public FingerprintQualityThreshold(){}
	
	public FingerprintQualityThreshold(int maximumAcceptableNFIQ, int minimumAcceptableMinutiaeCount,
	                                   int minimumAcceptableImageIntensity, int maximumAcceptableImageIntensity)
	{
		this.maximumAcceptableNFIQ = maximumAcceptableNFIQ;
		this.minimumAcceptableMinutiaeCount = minimumAcceptableMinutiaeCount;
		this.minimumAcceptableImageIntensity = minimumAcceptableImageIntensity;
		this.maximumAcceptableImageIntensity = maximumAcceptableImageIntensity;
	}
	
	public FingerprintQualityThreshold(FingerPosition fingerPosition)
	{
		String sFingerPosition = fingerPosition.name().replace("_", ".").toLowerCase();
		
		this.maximumAcceptableNFIQ = Integer.parseInt(Context.getConfigManager().getProperty(
									"fingerprint." + sFingerPosition + ".maximumAcceptableValue.nfiq"));
		this.minimumAcceptableMinutiaeCount = Integer.parseInt(Context.getConfigManager().getProperty(
									"fingerprint." + sFingerPosition + ".minimumAcceptableValue.minutiaeCount"));
		this.minimumAcceptableImageIntensity = Integer.parseInt(Context.getConfigManager().getProperty(
									"fingerprint." + sFingerPosition + ".minimumAcceptableValue.imageIntensity"));
		this.maximumAcceptableImageIntensity = Integer.parseInt(Context.getConfigManager().getProperty(
									"fingerprint." + sFingerPosition + ".maximumAcceptableValue.imageIntensity"));
	}
	
	public int getMaximumAcceptableNFIQ(){return maximumAcceptableNFIQ;}
	public void setMaximumAcceptableNFIQ(int maximumAcceptableNFIQ)
	{this.maximumAcceptableNFIQ = maximumAcceptableNFIQ;}
	
	public int getMinimumAcceptableMinutiaeCount(){return minimumAcceptableMinutiaeCount;}
	public void setMinimumAcceptableMinutiaeCount(int minimumAcceptableMinutiaeCount)
	{this.minimumAcceptableMinutiaeCount = minimumAcceptableMinutiaeCount;}
	
	public int getMinimumAcceptableImageIntensity(){return minimumAcceptableImageIntensity;}
	public void setMinimumAcceptableImageIntensity(int minimumAcceptableImageIntensity)
	{this.minimumAcceptableImageIntensity = minimumAcceptableImageIntensity;}
	
	public int getMaximumAcceptableImageIntensity(){return maximumAcceptableImageIntensity;}
	public void setMaximumAcceptableImageIntensity(int maximumAcceptableImageIntensity)
	{this.maximumAcceptableImageIntensity = maximumAcceptableImageIntensity;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FingerprintQualityThreshold that = (FingerprintQualityThreshold) o;
		return maximumAcceptableNFIQ == that.maximumAcceptableNFIQ &&
			   minimumAcceptableMinutiaeCount == that.minimumAcceptableMinutiaeCount &&
			   minimumAcceptableImageIntensity == that.minimumAcceptableImageIntensity &&
			   maximumAcceptableImageIntensity == that.maximumAcceptableImageIntensity;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(maximumAcceptableNFIQ, minimumAcceptableMinutiaeCount, minimumAcceptableImageIntensity,
		                    maximumAcceptableImageIntensity);
	}
	
	@Override
	public String toString()
	{
		return "FingerprintQualityThreshold{" + "maximumAcceptableNFIQ=" + maximumAcceptableNFIQ +
			   ", minimumAcceptableMinutiaeCount=" + minimumAcceptableMinutiaeCount +
			   ", minimumAcceptableImageIntensity=" + minimumAcceptableImageIntensity +
			   ", maximumAcceptableImageIntensity=" + maximumAcceptableImageIntensity + '}';
	}
}