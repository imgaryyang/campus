package com.gzzm.platform.log;

import com.gzzm.platform.commons.IndexGenerator;

/**
 * 日志表的全文索引建立
 *
 * @author camel
 * @date 2010-7-7
 */
public class LogIndexGenerator extends IndexGenerator
{
    public LogIndexGenerator()
    {
        setClasses(OperationLog.class);
    }
}
