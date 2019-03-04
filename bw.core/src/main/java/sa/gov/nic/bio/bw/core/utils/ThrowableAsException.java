package sa.gov.nic.bio.bw.core.utils;

public class ThrowableAsException extends Exception
{
	private final Throwable wrappedThrowable;
	
	public ThrowableAsException(Throwable wrappedThrowable)
	{
		this.wrappedThrowable = wrappedThrowable;
	}
	
	public Throwable getWrappedThrowable(){return wrappedThrowable;}
}