package com.gzzm.safecampus.device.card.annotation;

import java.lang.annotation.*;

/**
 * @author liyabin
 * @date 2018/1/25
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ReflectionField
{
    String[] value();
}
