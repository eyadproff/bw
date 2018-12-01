package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.utils;

public enum VisaApplicantsEnrollmentErrorCodes
{
	C010_00001, C010_00002, C010_00003, C010_00004, C010_00005, C010_00006, C010_00007, C010_00008, C010_00009,
	C010_00010,
	
	N010_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}