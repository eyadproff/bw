package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.utils;

public enum PrintDeadPersonRecordPresentErrorCodes
{
	C012_00001, C012_00002;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}