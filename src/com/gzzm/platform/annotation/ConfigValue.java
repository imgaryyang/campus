package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 从数据库读取配置项的值，即从PFCONFIG表读取
 *
 * @author camel
 * @date 2009-7-23
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigValue
{
    /**
     * 配置项的名称
     *
     * @return 配置项的名称
     */
    public String name();

    /**
     * 默认值
     *
     * @return 配置项的默认值
     */
    public String defaultValue() default "";
}