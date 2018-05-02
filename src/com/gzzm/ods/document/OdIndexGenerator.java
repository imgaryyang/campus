package com.gzzm.ods.document;

import com.gzzm.platform.commons.IndexGenerator;

/**
 * 公文全文索引
 *
 * @author camel
 * @date 11-10-19
 */
public class OdIndexGenerator extends IndexGenerator
{
    public OdIndexGenerator()
    {
        setClasses(OfficeDocument.class);
    }
}
