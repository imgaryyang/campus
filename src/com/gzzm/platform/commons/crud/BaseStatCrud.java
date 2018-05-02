package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.EntityStatCrud;
import net.cyan.crud.exporters.ExportParameters;

import java.util.*;

/**
 * 系统中所有列表统计的基类
 *
 * @author camel
 * @date 2009-10-20
 */
@Service
public abstract class BaseStatCrud<E> extends EntityStatCrud<E>
{
    public BaseStatCrud()
    {
    }

    @Override
    public String showList(Integer pageNo) throws Exception
    {
        return SystemCrudUtils.listForward(super.showList(pageNo), this);
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();
    }

    protected void addTotal(List<Map<String, Object>> result) throws Exception
    {
        addTotal(result, getTotal());
    }

    protected void addTotal(List<Map<String, Object>> result, Map<String, Object> total)
    {
        result.add(total);
    }

    @Override
    public String createCondition(boolean total) throws Exception
    {
        return SystemCrudUtils.getCondition(super.createCondition(total));
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
