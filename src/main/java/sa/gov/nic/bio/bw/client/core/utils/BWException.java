package sa.gov.nic.bio.bw.client.core.utils;

public class BWException extends Exception
{
	private String errorCode;
	
	public BWException(String message, String errorCode)
	{
		super(message);
		this.errorCode = errorCode;
	}
	
	public BWException(String message, Throwable cause, String errorCode)
	{
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	public String getErrorCode()
	{
		return errorCode;
	}
}