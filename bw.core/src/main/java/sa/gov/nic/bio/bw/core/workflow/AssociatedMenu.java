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
	int workflowId();
	String menuId();
	String menuTitle();
	int menuOrder() default Integer.MAX_VALUE;
	Device[] devices() default {};
}