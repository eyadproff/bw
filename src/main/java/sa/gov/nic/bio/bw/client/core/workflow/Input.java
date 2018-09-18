package sa.gov.nic.bio.bw.client.core.workflow;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BooleanSupplier;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Input
{
	boolean required() default false;
	Class<? extends BooleanSupplier> requirementCondition() default AlwaysCondition.class;
}