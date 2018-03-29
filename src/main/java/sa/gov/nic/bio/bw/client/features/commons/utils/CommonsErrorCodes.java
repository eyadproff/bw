package sa.gov.nic.bio.bw.client.features.commons.utils;

public enum CommonsErrorCodes
{
	C008_00001, C008_00002, C008_00003, C008_00004, C008_00005, C008_00006, C008_00007, C008_00008, C008_00009,
	C008_00010, C008_00011, C008_00012, C008_00013, C008_00014, C008_00015,
	
	N008_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}