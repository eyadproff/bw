package sa.gov.nic.bio.bw.client.features.mofaenrollment.utils;

public enum MofaEnrollmentErrorCodes
{
	C007_00001, C007_00002, C007_00003, C007_00004;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}