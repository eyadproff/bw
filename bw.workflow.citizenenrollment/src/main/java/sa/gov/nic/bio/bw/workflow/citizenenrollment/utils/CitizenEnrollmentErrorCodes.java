package sa.gov.nic.bio.bw.workflow.citizenenrollment.utils;

public enum CitizenEnrollmentErrorCodes
{
	B011_00001,C011_00001,C011_00002;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}