package sa.gov.nic.bio.bw.client.features.mofaenrollment.utils;

public enum MofaEnrollmentErrorCodes
{
	C007_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}