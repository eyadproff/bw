package sa.gov.nic.bio.bw.client.core.utils;

public enum CoreErrorCodes
{
	C002_00001, C002_00002, C002_00003, C002_00004, C002_00005, C002_00006, C002_00007, C002_00009,
	C002_00010, C002_00011, C002_00012, C002_00013, C002_00014, C002_00015, C002_00016, C002_00017, C002_00018,
	C002_00019, C002_00020, C002_00021, C002_00022;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}