package sa.gov.nic.bio.bw.workflow.citizenenrollment.utils;

public enum CitizenEnrollmentErrorCodes
{
	;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}