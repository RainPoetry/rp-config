package rainpoetry.config.commconf.annotation;


import rainpoetry.config.commconf.core.Types;

import java.lang.annotation.*;

/**
 * @author RainPoetry
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpConf {

	String value();

	// 数据类型
	Types type();

	boolean need() default false;

	String desc() default "";

}
