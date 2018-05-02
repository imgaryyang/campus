package com.gzzm.safecampus.campus.common;

import java.lang.annotation.*;

/**
 * 注入当前学年
 *
 * @author Neo
 * @date 2018/3/24 19:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SchoolYearId
{
}