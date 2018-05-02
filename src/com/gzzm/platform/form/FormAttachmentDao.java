package com.gzzm.platform.form;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2017/12/29
 */
public abstract class FormAttachmentDao extends GeneralDao
{
    @OQL("select bodyId from FormBody b where bodyId>?1 order by bodyId limit 300")
    public abstract List<Long> queryFormBodyIds(Long bodyId) throws Exception;
}
