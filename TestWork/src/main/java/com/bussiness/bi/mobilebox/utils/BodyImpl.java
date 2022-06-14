package com.bussiness.bi.mobilebox.utils;

import java.lang.annotation.*;

/**
 * 解析消息体的注解类
 *
 * @author chenqixu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyImpl {
    String value() default "";
}
