package com.gzzm.platform.organ;

/**
 * 用户遍历器，遍历某些符合条件的用户时，对用户进行操作
 *
 * @author camel
 * @date 2009-9-11
 */
public interface UserTraveller
{
    public void dispose(User user) throws Exception;
}