package sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.utils;

public enum FingerprintsInquiryErrorCodes
{
	C013_00001, C013_00002, C013_00003, C013_00004, C013_00005;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}