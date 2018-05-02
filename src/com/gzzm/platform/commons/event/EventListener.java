package com.gzzm.platform.commons.event;

/**
 * 事件监听器，监听某个事件
 *
 * @author camel
 * @date 2011-4-25
 */
public interface EventListener<T>
{
    /**
     * 触发事件
     *
     * @param obj 事件相关的对象
     * @throws Exception 触发事件异常
     */
    public void invoke(T obj) throws Exception;
}