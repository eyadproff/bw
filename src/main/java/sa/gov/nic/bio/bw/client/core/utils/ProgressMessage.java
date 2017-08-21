package sa.gov.nic.bio.bw.client.core.utils;

import javafx.application.Preloader;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Fouad on 11-Jul-17.
 */
public class ProgressMessage implements Preloader.PreloaderNotification
{
	public static final ProgressMessage SUCCESSFULLY_DONE = new ProgressMessage(true);
	
	private double progress;
	private String message;
	private boolean done;
	private boolean failed;
	private Exception exception;
	private String errorCode;
	private String[] errorMessageValues;
	
	public ProgressMessage(double progress, String message)
	{
		this.progress = progress;
		this.message = message;
		this.done = false;
		this.failed = false;
	}
	
	public ProgressMessage(Exception exception, String errorCode, String... errorMessageValues)
	{
		this.failed = true;
		this.exception = exception;
		this.errorCode = errorCode;
		this.errorMessageValues = errorMessageValues;
	}
	
	private ProgressMessage(boolean done)
	{
		this.done = done;
	}
	
	public double getProgress(){return progress;}
	public String getMessage(){return message;}
	public boolean isDone(){return done;}
	public boolean isFailed(){return failed;}
	public Exception getException(){return exception;}
	public String getErrorCode(){return errorCode;}
	public String[] getErrorMessageValues(){ return errorMessageValues; }
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ProgressMessage that = (ProgressMessage) o;
		return Double.compare(that.progress, progress) == 0 && done == that.done && failed == that.failed && Objects.equals(message, that.message) && Objects.equals(exception, that.exception) && Objects.equals(errorCode, that.errorCode) && Arrays.equals(errorMessageValues, that.errorMessageValues);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(progress, message, done, failed, exception, errorCode,
		                    errorMessageValues);
	}
	
	@Override
	public String toString()
	{
		return "ProgressMessage{" + "progress=" + progress + ", message='" + message + '\'' +
			   ", done=" + done + ", failed=" + failed + ", exception=" + exception +
			   ", errorCode='" + errorCode + '\'' + ", errorMessageValues=" +
			   Arrays.toString(errorMessageValues) + '}';
	}
}