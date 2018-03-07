package sa.gov.nic.bio.bw.client.features.commons.utils;

public enum CommonsErrorCodes
{
	C008_00001, C008_00002, C008_00003, C008_00004, C008_00005, C008_00006, C008_00007;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}