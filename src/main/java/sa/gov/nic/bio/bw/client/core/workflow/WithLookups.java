package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.commons.TaskResponse;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Callable;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithLookups
{
	Class<? extends Callable<TaskResponse<Void>>>[] value();
}