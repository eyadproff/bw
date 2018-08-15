package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils;

public enum FingerprintCardIdentificationErrorCodes
{
	C013_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}