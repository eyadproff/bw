package sa.gov.nic.bio.bw.client.features.foreignenrollment.utils;

public enum ForeignEnrollmentErrorCodes
{
	;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}