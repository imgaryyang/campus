package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.IndexGenerator;

/**
 * 信息发布索引生成器
 *
 * @author camel
 * @date 12-8-30
 */
public class InformationIndexGenerator extends IndexGenerator
{
    public InformationIndexGenerator()
    {
        setClasses(Information.class);
    }
}
