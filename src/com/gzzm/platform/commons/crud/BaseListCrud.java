package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.ListCrud;
import net.cyan.crud.exporters.ExportParameters;

/**
 * 系统中所有列表Crud的基类
 *
 * @author camel
 * @date 2009-11-3
 */
@Service
public abstract class BaseListCrud<R> extends ListCrud<R>
{
    public BaseListCrud()
    {
    }

    @Override
    public String forward(String forward)
    {
        return SystemCrudUtils.forward(super.forward(forward), this);
    }

    @Override
    public String showList(Integer pageNo) throws Exception
    {
        return SystemCrudUtils.listForward(super.showList(pageNo), this);
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        ExportParameters parameters = SystemCrudUtils.getExportParameters(this);
        if (parameters == null)
            return super.getExportParameters();
        else
            return parameters;
    }
}
