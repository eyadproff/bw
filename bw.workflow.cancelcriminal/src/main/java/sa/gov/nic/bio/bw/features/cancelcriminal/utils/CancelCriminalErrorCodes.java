package sa.gov.nic.bio.bw.features.cancelcriminal.utils;

public enum CancelCriminalErrorCodes
{
	C006_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}