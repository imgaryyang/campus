package com.gzzm.portal.tag;

import java.util.Map;

/**
 * 门户标签的接口，根据标签的属性及页面的上下文信息，获得需要的数据
 * 此接口的每个实现都是单例，在一个虚拟机进程中只会存在一个实例，被多个线程公用
 * 必须保证线程安全
 *
 * @author camel
 * @date 2011-6-11
 */
public interface PortalTag
{
    public static final String PORTAL = "portal:";

    public static final String TYPE = "portal:type";

    public static final int PREFIXLEN = PORTAL.length();

    /**
     * 根据上下文获取数据，数据可以是任意类型
     *
     * @param context 上下文信息
     * @return 数据
     * @throws Exception 允许实现类抛出异常
     */
    public Object getValue(Map<String, Object> context) throws Exception;
}