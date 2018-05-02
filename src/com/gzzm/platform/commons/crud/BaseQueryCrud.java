package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.NormalQueryCrud;
import net.cyan.crud.exporters.ExportParameters;

/**
 * 系统所有NormalQueryCrud的基类
 *
 * @author camel
 * @date 2009-10-10
 */
@Service
public abstract class BaseQueryCrud<E, K> extends NormalQueryCrud<E, K>
{
    public BaseQueryCrud()
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
    public String show(K key, String forward) throws Exception
    {
        return SystemCrudUtils.editForward(super.show(key, forward), this);
    }

    @Override
    public String createCondition() throws Exception
    {
        return SystemCrudUtils.getCondition(super.createCondition());
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
