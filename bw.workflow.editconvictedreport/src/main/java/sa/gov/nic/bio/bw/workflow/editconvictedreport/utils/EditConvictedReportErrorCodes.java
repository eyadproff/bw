package sa.gov.nic.bio.bw.workflow.editconvictedreport.utils;

public enum EditConvictedReportErrorCodes
{
	C016_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}