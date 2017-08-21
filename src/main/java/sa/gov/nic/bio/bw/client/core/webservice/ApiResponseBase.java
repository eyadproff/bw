package sa.gov.nic.bio.bw.client.core.webservice;

import java.util.Objects;

public class ApiResponseBase
{
	private String errorCode;
	
	public ApiResponseBase(){}
	
	public String getErrorCode(){ return errorCode; }
	public void setErrorCode(String errorCode){ this.errorCode = errorCode; }
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ApiResponseBase that = (ApiResponseBase) o;
		return Objects.equals(errorCode, that.errorCode);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(errorCode);
	}
	
	@Override
	public String toString()
	{
		return "ApiResponseBase{" + "errorCode='" + errorCode + '\'' + '}';
	}
}