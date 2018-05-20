package sa.gov.nic.bio.bw.client.features.foreignenrollment.utils;

public enum ForeignEnrollmentErrorCodes
{
	C010_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}