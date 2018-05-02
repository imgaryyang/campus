package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 注入当前用户id
 * @author camel
 * @date 2009-7-18
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UserId
{
}