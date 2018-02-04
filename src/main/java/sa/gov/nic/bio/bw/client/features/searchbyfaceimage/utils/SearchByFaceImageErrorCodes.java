package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.utils;

public enum SearchByFaceImageErrorCodes
{
	C005_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}