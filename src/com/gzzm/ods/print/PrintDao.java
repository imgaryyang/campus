package com.gzzm.ods.print;

import com.gzzm.ods.business.BusinessModel;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 11-11-10
 */
public abstract class PrintDao extends GeneralDao
{
    public PrintDao()
    {
    }

    public PrintTemplate getPrintTemplate(Integer templateId) throws Exception
    {
        return load(PrintTemplate.class, templateId);
    }

    public BusinessModel getBusiness(Integer businessId) throws Exception
    {
        return load(BusinessModel.class, businessId);
    }

    @OQL("select p from PrintTemplate p where businessId=:1 order by orderId")
    public abstract List<PrintTemplate> getPrintTemplates(Integer businessId) throws Exception;

    @OQL("select p from PrintTemplate p where deptId=:1 and businessType=:2 and businessId is null order by orderId")
    public abstract List<PrintTemplate> getPrintTemplates(Integer deptId, String businessType) throws Exception;
}
