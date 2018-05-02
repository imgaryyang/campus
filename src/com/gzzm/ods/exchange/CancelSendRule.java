package com.gzzm.ods.exchange;

import com.gzzm.platform.organ.UserInfo;

/**
 * @author camel
 * @date 2016/9/19
 */
public interface CancelSendRule
{
    public boolean cancelable(Send send, UserInfo userInfo) throws Exception;
}