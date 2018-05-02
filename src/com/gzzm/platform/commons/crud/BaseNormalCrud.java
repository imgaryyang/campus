package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.*;
import net.cyan.crud.NormalCrud;
import net.cyan.crud.arachne.ProgressImportLisenter;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.importers.CrudEntityImportor;

/**
 * 系统所有NormalCrud的基类
 *
 * @author camel
 * @date 2009-7-22
 */
@Service
public abstract class BaseNormalCrud<E, K> extends NormalCrud<E, K> implements LogableCrud
{
    /**
     * 是否记录日志
     */
    @NotSerialized
    private boolean log;

    private boolean readOnly;

    protected BaseNormalCrud()
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
        return editForward(super.show(key, forward));
    }

    @Override
    public String add(String forward) throws Exception
    {
        return editForward(super.add(forward));
    }

    @Override
    public String duplicate(K key, String forward) throws Exception
    {
        return editForward(super.duplicate(key, forward));
    }

    private String editForward(String forward)
    {
        return SystemCrudUtils.editForward(forward, this);
    }

    @Override
    @Forward(page = Pages.SORT)
    public String showSortList(String forward) throws Exception
    {
        return super.showSortList(forward);
    }

    public int getOrderFieldLength()
    {
        return 6;
    }

    public boolean isLog()
    {
        return log;
    }

    protected void setLog(boolean log)
    {
        this.log = log;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    @Override
    public void afterInsert() throws Exception
    {
        if (isLog())
        {
            //新增数据之后记录日志
            SystemCrudUtils.insertLog(this);
        }
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        if (isLog())
        {
            //修改数据之前记录日志
            SystemCrudUtils.updateLog(this);
        }

        return true;
    }

    @Override
    public boolean beforeDelete(K key) throws Exception
    {
        if (isLog())
        {
            //删除数据之前记录日志
            SystemCrudUtils.deleteLog(key, this);
        }

        return true;
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        if (isLog())
        {
            //删除数据之前记录日志
            SystemCrudUtils.deleteAllLog(this);
        }

        return true;
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

    @Forward(page = Pages.IMP)
    @Override
    public String showImp() throws Exception
    {
        return super.showImp();
    }

    @Override
    protected void initImportor(CrudEntityImportor<E, K> importor) throws Exception
    {
        importor.addListiner(ProgressImportLisenter.getInstance());
    }
}