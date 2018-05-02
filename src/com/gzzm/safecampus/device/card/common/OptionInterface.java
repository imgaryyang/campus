package com.gzzm.safecampus.device.card.common;


/**
 * @author liyabin
 * @date 2018/3/22
 */
public interface OptionInterface<T,E>
{
    void pushBefore(T bean,E data);
}