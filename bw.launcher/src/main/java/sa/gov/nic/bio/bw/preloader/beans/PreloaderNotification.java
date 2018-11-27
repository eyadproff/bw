package sa.gov.nic.bio.bw.preloader.beans;

import javafx.application.Preloader;
import sa.gov.nic.bio.bw.core.beans.JavaBean;

/**
 * The notification that is passed from the preloader to the main application.
 *
 * @author Fouad Almalki
 */
public class PreloaderNotification extends JavaBean implements Preloader.PreloaderNotification
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
}