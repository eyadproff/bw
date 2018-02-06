package sa.gov.nic.bio.bw.client.core.utils;

import java.util.Objects;

/**
 * A class that represents the errors that are related to the business. There are two types of business errors;
 * the business error that is associated with the backend which is returned in the response of the REST API, and
 * the error that happens only on the client side and is related to the business. Code of the business errors
 * follows the following pattern: BXXX-YYYYY (the first type) and NXXX-YYYYY (the first type), where XXX is the
 * number of module that the error is belong to, and YYYYY is the sequence number of error inside the module.
 *
 * @author Fouad Almalki
 * @since 1.2.1
 */
public class BusinessError
{
	private final String code;
	private final String message;
	
	public BusinessError(String code, String message)
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
		BusinessError that = (BusinessError) o;
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
		return "BusinessError{" + "code='" + code + '\'' + ", message='" + message + '\'' + '}';
	}
}