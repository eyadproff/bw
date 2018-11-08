package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.utils.Device;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssociatedMenu
{
	String id();
	String title();
	int order() default Integer.MAX_VALUE;
	Device[] devices() default {};
}