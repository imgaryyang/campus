package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 菜单ID
 *
 * @author camel
 * @date 2011-6-9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface MenuId
{
}