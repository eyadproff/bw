package sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.utils;

public enum FingerprintCardIdentificationErrorCodes
{
	C013_00001, C013_00002, C013_00003, C013_00004, C013_00005, C013_00006, C013_00007, C013_00008, C013_00009,
	C013_00010;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}