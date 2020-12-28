package sa.gov.nic.bio.bw.workflow.searchbyfaceimage.utils;

public enum SearchByFaceImageErrorCodes
{
	C005_00001, C005_00002, C005_00003, C005_00004 , C005_00005 , C005_00006 , C005_00007 , C005_00008 , C005_00009 , C005_00010;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}