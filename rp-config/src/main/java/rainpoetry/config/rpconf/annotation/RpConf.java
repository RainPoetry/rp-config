package rainpoetry.config.rpconf.annotation;


import rainpoetry.config.rpconf.core.Types;

import java.lang.annotation.*;

/**
 * @author RainPoetry
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpConf {

	// key
	String value();

	// 数据类型
	Types type() default Types.JAVATYPE;

	boolean need() default false;

	// 描述
	String desc() default "";

}
