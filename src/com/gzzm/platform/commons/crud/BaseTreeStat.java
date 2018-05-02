package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.EntityTreeStatCrud;
import net.cyan.crud.exporters.ExportParameters;

/**
 * 所有树型结构统计的基类
 *
 * @author camel
 * @date 2009-10-20
 */
@Service
public abstract class BaseTreeStat<E, K> extends EntityTreeStatCrud<E, K>
{
    public BaseTreeStat()
    {
    }

    @Override
    public String showTree() throws Exception
    {
        return SystemCrudUtils.treeForward(super.showTree(), this);
    }

    @Override
    public String createCondition() throws Exception
    {
        return SystemCrudUtils.getCondition(super.createCondition());
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        ExportParameters parameters = SystemCrudUtils.getExportParameters(this, getExportFormat());
        if (parameters == null)
            return super.getExportParameters();
        else
            return parameters;
    }
}
