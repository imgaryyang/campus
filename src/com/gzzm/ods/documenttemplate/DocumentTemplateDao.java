package com.gzzm.ods.documenttemplate;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 文件模板相关的数据操作
 *
 * @author camel
 * @date 2010-12-17
 */
public abstract class DocumentTemplateDao extends GeneralDao
{
    public DocumentTemplateDao()
    {
    }

    public DocumentTemplate getDocumentTemplate(Integer redHeadId) throws Exception
    {
        return load(DocumentTemplate.class, redHeadId);
    }

    @OQL("select t from DocumentTemplate t where deptId=:1  order by orderId")
    public abstract List<DocumentTemplate> getDocumentTemplates(Integer deptId) throws Exception;
}
