package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 表示一个类的其实例被缓存
 * @author camel
 * @date 2010-5-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CacheInstance
{
    public String value();
}