package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 监听器，标识某个类用于监听某些事件
 *
 * @author camel
 * @date 2011-4-25
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Listener
{
    /**
     * 要监听的事件的名称
     *
     * @return 事件的名称
     */
    public String value() default "";
}