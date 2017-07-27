package sa.gov.nic.bio.bw.client.core;

import javafx.application.Preloader;

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
	
	public ProgressMessage(double progress, String message)
	{
		this.progress = progress;
		this.message = message;
		this.done = false;
		this.failed = false;
	}
	
	public ProgressMessage(Exception exception, String errorCode)
	{
		this.failed = true;
		this.exception = exception;
		this.errorCode = errorCode;
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
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ProgressMessage message1 = (ProgressMessage) o;
		return Double.compare(message1.progress, progress) == 0 && done == message1.done &&
			   failed == message1.failed && Objects.equals(message, message1.message) &&
			   Objects.equals(exception, message1.exception) && Objects.equals(errorCode, message1.errorCode);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(progress, message, done, failed, exception, errorCode);
	}
	
	@Override
	public String toString()
	{
		return "ProgressMessage{" + "progress=" + progress + ", message='" + message + '\'' +
			   ", done=" + done + ", failed=" + failed + ", exception=" + exception +
			   ", errorCode='" + errorCode + '\'' + '}';
	}
}