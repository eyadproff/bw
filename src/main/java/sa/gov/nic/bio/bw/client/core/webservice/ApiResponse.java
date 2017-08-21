package sa.gov.nic.bio.bw.client.core.webservice;

import java.util.Objects;

public class ApiResponse<T>
{
	private String apiUrl;
	private boolean success;
	private T result;
	private int httpCode;
	private String errorCode;
	private Exception exception;
	
	public ApiResponse(String apiUrl, T result)
	{
		this.success = true;
		this.result = result;
	}
	
	public ApiResponse(String apiUrl, String errorCode, Exception exception)
	{
		this.success = false;
		this.errorCode = errorCode;
		this.exception = exception;
	}
	
	public ApiResponse(String apiUrl, String errorCode, int httpCode)
	{
		this.success = false;
		this.httpCode = httpCode;
		this.errorCode = errorCode;
	}
	
	public String getApiUrl(){ return apiUrl; }
	public void setApiUrl(String apiUrl){ this.apiUrl = apiUrl; }
	
	public boolean isSuccess(){ return success; }
	public void setSuccess(boolean success){ this.success = success; }
	
	public T getResult(){ return result; }
	public void setResult(T result){ this.result = result; }
	
	public int getHttpCode(){ return httpCode; }
	public void setHttpCode(int httpCode){ this.httpCode = httpCode; }
	
	public String getErrorCode(){ return errorCode; }
	public void setErrorCode(String errorCode){ this.errorCode = errorCode; }
	
	public Exception getException(){ return exception; }
	public void setException(Exception exception){ this.exception = exception; }
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ApiResponse<?> that = (ApiResponse<?>) o;
		return success == that.success && httpCode == that.httpCode &&
				Objects.equals(apiUrl, that.apiUrl) &&
				Objects.equals(result, that.result) &&
				Objects.equals(errorCode, that.errorCode) &&
				Objects.equals(exception, that.exception);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(apiUrl, success, result, httpCode, errorCode, exception);
	}
	
	@Override
	public String toString()
	{
		return "ApiResponse{" + "apiUrl='" + apiUrl + '\'' + ", success=" + success +
				", result=" + result + ", httpCode=" + httpCode + ", errorCode='" + errorCode +
				'\'' + ", exception=" + exception + '}';
	}
}