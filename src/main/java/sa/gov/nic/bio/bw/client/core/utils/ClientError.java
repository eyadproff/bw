package sa.gov.nic.bio.bw.client.core.utils;

import java.util.Objects;

/**
 * A class that represents the errors that occur at the client-side. Code of the client errors follows the
 * following pattern: CXXX-YYYYY, where XXX is the number of module that the error is belong to, and
 * YYYYY is the sequence number of error inside the module.
 *
 * @author Fouad Almalki
 * @since 1.2.1
 */
public class ClientError
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
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ClientError that = (ClientError) o;
		return Objects.equals(code, that.code) && Objects.equals(message, that.message);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(code, message);
	}
	
	@Override
	public String toString()
	{
		return "ClientError{" + "code='" + code + '\'' + ", message='" + message + '\'' + '}';
	}
}