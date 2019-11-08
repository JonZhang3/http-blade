package com.httpblade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.PARAMETER)
public @interface Field {

    String value();

    boolean encoded() default false;

}
