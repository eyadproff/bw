package sa.gov.nic.bio.bw.core.beans;

/**
 * A class that represents the errors that occur at the client-side. Code of the client errors follows the
 * following pattern: CXXX-YYYYY, where XXX is the number of module that the error is belong to, and
 * YYYYY is the sequence number of error inside the module.
 *
 * @author Fouad Almalki
 */
public class ClientError extends JavaBean
{
	private final String code;
	private final String message;
	
	public ClientError(String code, String message)
	{
		this.code = code;
		this.message = message;
	}
	
	public String getCode(){return code;}
	public String getMessage(){return message;}
}