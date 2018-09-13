package sa.gov.nic.bio.bw.client.core.wizard;

import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Callable;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithLookups
{
	Class<? extends Callable<ServiceResponse<Void>>>[] value();
}