package com.gzzm.ods.exchange;

import net.cyan.thunwind.annotation.GetByField;

/**
 * 和收文相关的数据库操作
 *
 * @author camel
 * @date 11-10-11
 */
public abstract class ExchangeCopyDao extends ExchangeDao
{
    public ExchangeCopyDao()
    {
    }

    @GetByField("stepId")
    public abstract Copy getCopyByStepId(Long stepId) throws Exception;
}
