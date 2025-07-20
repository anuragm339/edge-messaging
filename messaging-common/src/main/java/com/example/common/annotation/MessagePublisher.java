package com.example.common.annotation;



import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessagePublisher {
    String topic();
    String eventType() default "";  // Optional: derived from method name/class if blank
}

