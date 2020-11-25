package com.sbrf.spring.binding;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SpringBindingInterceptor.SpringBinding.class)
public @interface EnableSpringBinding {
}