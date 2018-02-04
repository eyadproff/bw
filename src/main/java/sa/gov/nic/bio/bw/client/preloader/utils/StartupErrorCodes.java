package sa.gov.nic.bio.bw.client.preloader.utils;

public enum StartupErrorCodes
{
	C001_00001,
	C001_00002,
	C001_00003,
	C001_00004,
	C001_00005,
	C001_00006,
	C001_00007,
	C001_00008,
	C001_00009,
	C001_00010,
	C001_00011,
	C001_00012,
	C001_00013;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}