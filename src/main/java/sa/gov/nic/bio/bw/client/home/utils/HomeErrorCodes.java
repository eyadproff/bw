package sa.gov.nic.bio.bw.client.home.utils;

public enum HomeErrorCodes
{
	C004_00001, C004_00002, C004_00003, C004_00004, C004_00005, C004_00006, C004_00007,
	
	N004_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}