package sa.gov.nic.bio.bw.client.features.foreignenrollment.utils;

public enum ForeignEnrollmentErrorCodes
{
	N010_00001,
	
	C010_00001, C010_00002, C010_00003, C010_00004, C010_00005;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}