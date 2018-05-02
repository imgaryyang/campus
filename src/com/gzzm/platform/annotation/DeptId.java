package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 注入当前用户的部门id
 * @author camel
 * @date 2009-7-18
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DeptId
{
    /**
     * 部门的级别，默认为－1表示当前部门，不往上找
     * @return 部门的级别
     */
    int level() default -1;
}