package com.gzzm.platform.menu;

import java.util.*;

/**
 * 菜单权限提供器，定义如何根据一个菜单
 *
 * @author camel
 * @date 2009-12-22
 */
public interface MenuAuthsProvider
{
    /**
     * 获得url对应的权限列表，如果此Provider不适合此url，返回null
     *
     * @param url url
     * @return url对应的权限列表，可以返回空集合，如果此Provider不适合此url，返回null，
     * @throws Exception 允许实现类抛出异常
     */
    public List<MenuAuth> getAuths(String url) throws Exception;
}