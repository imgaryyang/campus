package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.QueryCrud;
import net.cyan.crud.exporters.ExportParameters;

/**
 * @author camel
 * @date 12-4-12
 */
@Service
public abstract class BaseQLQueryCrud<R> extends QueryCrud<R>
{
    public BaseQLQueryCrud()
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
    protected String getCountQueryString() throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
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
