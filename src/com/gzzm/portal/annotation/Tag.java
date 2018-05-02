package com.gzzm.portal.annotation;

import java.lang.annotation.*;

/**
 * 定义一个门户标签
 *
 * @author camel
 * @date 2011-6-17
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Tag
{
    /**
     * 标签的名称
     *
     * @return 标签名称
     */
    public String name();

    /**
     * 是否单例
     *
     * @return 单例返回true，不是单例返回false，默认为不单例
     */
    public boolean singleton() default false;
}