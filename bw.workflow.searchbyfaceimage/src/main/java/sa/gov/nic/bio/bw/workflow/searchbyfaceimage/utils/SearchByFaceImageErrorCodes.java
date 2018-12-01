package sa.gov.nic.bio.bw.workflow.searchbyfaceimage.utils;

public enum SearchByFaceImageErrorCodes
{
	C005_00001, C005_00002, C005_00003, C005_00004;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}