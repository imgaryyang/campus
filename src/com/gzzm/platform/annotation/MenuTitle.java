package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 注入当前菜单的标题
 *
 * @author camel
 * @date 2011-5-24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface MenuTitle
{
}