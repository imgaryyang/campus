package com.gzzm.safecampus.device.card.common;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public interface TokenInteface
{
    boolean checkToken() throws Exception;

    void initToken()throws Exception;
}