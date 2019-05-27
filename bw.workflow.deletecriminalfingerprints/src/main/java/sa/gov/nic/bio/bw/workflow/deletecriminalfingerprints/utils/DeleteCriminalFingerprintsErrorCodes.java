package sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.utils;

public enum DeleteCriminalFingerprintsErrorCodes
{
	C017_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}