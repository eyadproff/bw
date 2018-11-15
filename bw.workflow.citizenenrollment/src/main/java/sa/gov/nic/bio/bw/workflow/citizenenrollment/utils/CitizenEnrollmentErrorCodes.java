package sa.gov.nic.bio.bw.workflow.citizenenrollment.utils;

public enum CitizenEnrollmentErrorCodes
{
	C011_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}