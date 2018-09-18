package sa.gov.nic.bio.bw.client.login.workflow;

import java.util.Arrays;
import java.util.Objects;

public class ServiceResponse<T>
{
	private boolean success;
	private String errorCode;
	private Exception exception;
	private String[] errorDetails;
	private T result;
	
	public ServiceResponse(boolean success, String errorCode, Exception exception, String[] errorDetails, T result)
	{
		this.success = success;
		this.errorCode = errorCode;
		this.exception = exception;
		this.errorDetails = errorDetails;
		this.result = result;
	}
	
	public static <T> ServiceResponse<T> success()
	{
		return success(null);
	}
	
	public static <T> ServiceResponse<T> success(T result)
	{
		return new ServiceResponse<>(true, null, null, null, result);
	}
	
	public static <T> ServiceResponse<T> failure(String errorCode, Exception exception, String[] errorDetails)
	{
		return new ServiceResponse<>(false, errorCode, exception, errorDetails, null);
	}
	
	public boolean isSuccess(){return success;}
	public void setSuccess(boolean success){this.success = success;}
	
	public String getErrorCode(){return errorCode;}
	public void setErrorCode(String errorCode){this.errorCode = errorCode;}
	
	public Exception getException(){return exception;}
	public void setException(Exception exception){this.exception = exception;}
	
	public String[] getErrorDetails(){return errorDetails;}
	public void setErrorDetails(String[] errorDetails){this.errorDetails = errorDetails;}
	
	public T getResult(){return result;}
	public void setResult(T result){this.result = result;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ServiceResponse<?> that = (ServiceResponse<?>) o;
		return success == that.success && Objects.equals(errorCode, that.errorCode) &&
			   Objects.equals(exception, that.exception) && Arrays.equals(errorDetails, that.errorDetails) &&
			   Objects.equals(result, that.result);
	}
	
	@Override
	public int hashCode()
	{
		int result1 = Objects.hash(success, errorCode, exception, result);
		result1 = 31 * result1 + Arrays.hashCode(errorDetails);
		return result1;
	}
	
	@Override
	public String toString()
	{
		return "ServiceResponse{" + "success=" + success + ", errorCode='" + errorCode + '\'' + ", exception=" +
			   exception + ", errorDetails=" + Arrays.toString(errorDetails) + ", result=" + result + '}';
	}
}