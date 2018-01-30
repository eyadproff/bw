package sa.gov.nic.bio.bw.client.login.workflow;

import java.util.Objects;

public class ServiceResponse<T>
{
	private boolean success;
	private String errorCode;
	private Exception exception;
	private T result;
	
	public ServiceResponse(boolean success, String errorCode, Exception exception, T result)
	{
		this.success = success;
		this.errorCode = errorCode;
		this.exception = exception;
		this.result = result;
	}
	
	public boolean isSuccess(){return success;}
	public void setSuccess(boolean success){this.success = success;}
	
	public String getErrorCode(){return errorCode;}
	public void setErrorCode(String errorCode){this.errorCode = errorCode;}
	
	public Exception getException(){return exception;}
	public void setException(Exception exception){this.exception = exception;}
	
	public T getResult(){return result;}
	public void setResult(T result){this.result = result;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ServiceResponse<?> that = (ServiceResponse<?>) o;
		return success == that.success && Objects.equals(errorCode, that.errorCode)
			   && Objects.equals(exception, that.exception) && Objects.equals(result, that.result);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(success, errorCode, exception, result);
	}
	
	@Override
	public String toString()
	{
		return "ServiceResponse{" + "success=" + success + ", errorCode='" + errorCode + '\'' +
			   ", exception=" + exception + ", result=" + result + '}';
	}
}