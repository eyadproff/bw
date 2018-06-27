package sa.gov.nic.bio.bw.client.features.faceverification.utils;

public enum FaceVerificationErrorCodes
{
	C011_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}