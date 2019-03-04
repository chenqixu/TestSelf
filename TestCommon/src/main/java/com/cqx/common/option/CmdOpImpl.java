package com.cqx.common.option;

import java.lang.annotation.*;

/**
 * 命令接口
 *
 * @author chenqixu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmdOpImpl {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CmdIOp {
        String opt() default "l";

        String longOpt() default "log";

        boolean hasArg() default true;

        String description() default "log";

        boolean required() default true;
    }
}
