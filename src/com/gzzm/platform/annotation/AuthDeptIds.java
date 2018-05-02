package com.gzzm.platform.annotation;

import com.gzzm.platform.organ.*;
import net.cyan.commons.util.Filter;

import java.lang.annotation.*;

/**
 * 此功能点拥有权限的部门列表
 *
 * @author camel
 * @date 2009-7-29
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface AuthDeptIds
{
    /**
     * 表示只加载此级别的部门，-1表示加载所有部门
     *
     * @return 部门级别
     */
    public int level() default -1;

    /**
     * 条件
     *
     * @return 条件，为""表示没有条件
     */
    public String condition() default "";

    /**
     * 对应的功能，如果为空表示当前功能
     *
     * @return 表示取哪个功能的权限
     */
    public String app() default "";

    /**
     * 过滤器，DeptFilter表示不使用过滤器，当定义了filter时，condition无效
     *
     * @return 过滤器的类型
     */
    public Class<? extends Filter<DeptInfo>> filter() default AllDeptFilter.class;
}