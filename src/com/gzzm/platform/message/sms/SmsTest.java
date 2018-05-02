package com.gzzm.platform.message.sms;

import net.cyan.commons.test.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2010-8-17
 */
public class SmsTest
{
    @Inject
    private SmsSender sender;

    public SmsTest()
    {
    }

    @TestCase
    public void test() throws Exception
    {
        SmsLog message = new SmsLog();
        message.setPhone("13924230981");
        message.setContent("测试");
        message.setMessageCode("dfdf");
        sender.send(message);
    }

    public static void main(String[] args)
    {
        TestRunner.run();
    }
}
