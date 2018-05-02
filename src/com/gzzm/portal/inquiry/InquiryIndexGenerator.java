package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.IndexGenerator;

/**
 * 信息发布索引生成器
 *
 * @author camel
 * @date 12-8-30
 */
public class InquiryIndexGenerator extends IndexGenerator
{
    public InquiryIndexGenerator()
    {
        setClasses(Inquiry.class);
    }
}
