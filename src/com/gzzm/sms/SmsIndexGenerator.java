package com.gzzm.sms;

import com.gzzm.platform.commons.IndexGenerator;

/**
 * 短信内容的全文索引生成
 *
 * @author camel
 * @date 2010-12-1
 */
public class SmsIndexGenerator extends IndexGenerator
{
    public SmsIndexGenerator()
    {
        setClasses(SmsMt.class, SmsMo.class);
    }
}
