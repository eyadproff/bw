package sa.gov.nic.bio.commons;

import java.util.Arrays;

public class TaskResponse<T>
{
	public interface TypeCaster<T1, T2>
	{
		T1 cast(T2 t);
	}
	
	private boolean success;
	private String errorCode;
	private Exception exception;
	private String[] errorDetails;
	private T result;
	
	public TaskResponse(boolean success, String errorCode, Exception exception, String[] errorDetails, T result)
	{
		this.success = success;
		this.errorCode = errorCode;
		this.exception = exception;
		this.errorDetails = errorDetails;
		this.result = result;
	}
	
	public static <T> TaskResponse<T> success()
	{
		return success(null);
	}
	
	public static <T> TaskResponse<T> success(T result)
	{
		return new TaskResponse<T>(true, null, null, null, result);
	}
	
	public static <T> TaskResponse<T> failure(String errorCode)
	{
		return new TaskResponse<T>(false, errorCode, null, null, null);
	}
	
	public static <T> TaskResponse<T> failure(String errorCode, Exception exception)
	{
		return new TaskResponse<T>(false, errorCode, exception, null, null);
	}
	
	public static <T> TaskResponse<T> failure(String errorCode, String[] errorDetails)
	{
		return new TaskResponse<T>(false, errorCode, null, errorDetails, null);
	}
	
	public static <T> TaskResponse<T> failure(String errorCode, Exception exception, String[] errorDetails)
	{
		return new TaskResponse<T>(false, errorCode, exception, errorDetails, null);
	}
	
	public static <T1, T2> TaskResponse<T1> cast(TaskResponse<T2> input,
	                                             TypeCaster<T1, T2> typeCaster)
	{
		return new TaskResponse<T1>(input.success, input.errorCode, input.exception, input.errorDetails,
		                            typeCaster != null ? typeCaster.cast(input.result) : null);
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
		
		TaskResponse<?> that = (TaskResponse<?>) o;
		
		if(success != that.success) return false;
		if(errorCode != null ? !errorCode.equals(that.errorCode) : that.errorCode != null) return false;
		if(exception != null ? !exception.equals(that.exception) : that.exception != null) return false;
		if(!Arrays.equals(errorDetails, that.errorDetails)) return false;
		return result != null ? result.equals(that.result) : that.result == null;
	}
	
	@Override
	public int hashCode()
	{
		int result1 = (success ? 1 : 0);
		result1 = 31 * result1 + (errorCode != null ? errorCode.hashCode() : 0);
		result1 = 31 * result1 + (exception != null ? exception.hashCode() : 0);
		result1 = 31 * result1 + Arrays.hashCode(errorDetails);
		result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
		return result1;
	}
	
	@Override
	public String toString()
	{
		return "TaskResponse{" + "success=" + success + ", errorCode='" + errorCode + '\'' + ", exception=" +
				exception + ", errorDetails=" + Arrays.toString(errorDetails) + ", result=" + result + '}';
	}
}