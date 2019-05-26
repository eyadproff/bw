package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.utils;

public enum RegisterConvictedPresentErrorCodes
{
	C007_00001, C007_00002, C007_00003, C007_00004, C007_00005, C007_00006, C007_00007, C007_00008, C007_00009,
	C007_00010, C007_00011, C007_00012, C007_00013, C007_00014, C007_00015, C007_00016, C007_00017, C007_00018,
	C007_00019, C007_00020;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}