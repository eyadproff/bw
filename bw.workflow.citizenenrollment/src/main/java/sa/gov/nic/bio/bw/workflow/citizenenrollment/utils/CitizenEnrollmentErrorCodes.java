package sa.gov.nic.bio.bw.workflow.citizenenrollment.utils;

public enum CitizenEnrollmentErrorCodes
{
	B011_00001,B011_00002,B011_00003,C011_00010,C011_00011;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}