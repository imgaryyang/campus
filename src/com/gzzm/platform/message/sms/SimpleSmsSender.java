package com.gzzm.platform.message.sms;

import net.cyan.nest.annotation.*;

/**
 * 直接给出手机号码和信息发送短信的接口
 *
 * @author camel
 * @date 2010-8-23
 */
@Injectable(singleton = true)
@ImplementedByScript(type = "java", path = "/scripts/platform/message/sms.java")
public interface SimpleSmsSender
{
    public String send(String phone, String message, String serial) throws Exception;
}