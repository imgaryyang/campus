package com.gzzm.platform.annotation;

import java.lang.annotation.*;

/**
 * 当前局级部门的id，相当于DeptId(level=1)
 * @author camel
 * @date 2009-7-18
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface BureauId
{
}