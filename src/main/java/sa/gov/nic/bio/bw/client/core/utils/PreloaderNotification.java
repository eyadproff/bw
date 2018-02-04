package sa.gov.nic.bio.bw.client.core.utils;

import javafx.application.Preloader;

import java.util.Arrays;
import java.util.Objects;

/**
 * The notification that is passed from the preloader to the main application.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public class PreloaderNotification implements Preloader.PreloaderNotification
{
	private boolean done;
	private boolean success;
	private Exception exception;
	private String errorCode;
	private String[] errorDetails;
	
	public PreloaderNotification(boolean done, boolean success, Exception exception, String errorCode, String[] errorDetails)
	{
		this.done = done;
		this.success = success;
		this.exception = exception;
		this.errorCode = errorCode;
		this.errorDetails = errorDetails;
	}
	
	public static PreloaderNotification success()
	{
		return new PreloaderNotification(true, true, null, null, null);
	}
	
	public static PreloaderNotification failure(Exception exception, String errorCode, String[] errorDetails)
	{
		return new PreloaderNotification(true, false, exception, errorCode, errorDetails);
	}
	
	public boolean isDone(){return done;}
	public void setDone(boolean done){this.done = done;}
	
	public boolean isSuccess(){return success;}
	public void setSuccess(boolean success){this.success = success;}
	
	public Exception getException(){return exception;}
	public void setException(Exception exception){this.exception = exception;}
	
	public String getErrorCode(){return errorCode;}
	public void setErrorCode(String errorCode){this.errorCode = errorCode;}
	
	public String[] getErrorDetails(){return errorDetails;}
	public void setErrorDetails(String[] errorDetails){this.errorDetails = errorDetails;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		PreloaderNotification that = (PreloaderNotification) o;
		return done == that.done && success == that.success && Objects.equals(exception, that.exception) &&
			   Objects.equals(errorCode, that.errorCode) && Arrays.equals(errorDetails, that.errorDetails);
	}
	
	@Override
	public int hashCode()
	{
		int result = Objects.hash(done, success, exception, errorCode);
		result = 31 * result + Arrays.hashCode(errorDetails);
		return result;
	}
	
	@Override
	public String toString()
	{
		return "PreloaderNotification{" + "done=" + done + ", success=" + success + ", exception=" + exception +
			   ", errorCode='" + errorCode + '\'' + ", errorDetails=" + Arrays.toString(errorDetails) + '}';
	}
}