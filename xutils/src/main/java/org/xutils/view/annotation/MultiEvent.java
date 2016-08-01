package org.xutils.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多级事件注解
 *
 * @author DrkCore
 * @since 2016年1月28日13:16:41
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiEvent {

	Event[] value ();

}
