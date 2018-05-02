package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.log.LogAction;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.*;
import net.cyan.crud.arachne.ProgressImportLisenter;
import net.cyan.crud.importers.CrudEntityImportor;

/**
 * 系统所有树型维护的基类
 *
 * @author camel
 * @date 2009-10-10
 */
@Service
public abstract class BaseTreeCrud<E, K> extends EntityTreeCrud<E, K> implements LogableCrud
{
    /**
     * 是否记录日志
     */
    @NotSerialized
    private boolean log;

    protected BaseTreeCrud()
    {
    }

    @Override
    public String showTree() throws Exception
    {
        return SystemCrudUtils.treeForward(super.showTree(), this);
    }

    @Override
    public String show(K key, String forward) throws Exception
    {
        return editForward(super.show(key, forward));
    }

    @Override
    public String add(K parentKey, String forward) throws Exception
    {
        return editForward(super.add(parentKey, forward));
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
    @Transactional
    public void move(K key, K parentKey) throws Exception
    {
        if (isLog())
        {
            //移动数据之前记录日志
            E entity = getEntity(key);

            SystemCrudUtils.saveLog(entity, LogAction.modify, this,
                    Tools.getMessage("crud.movelog", getParentKey(entity), parentKey));
        }

        super.move(key, parentKey);
    }

    @Override
    @Transactional
    public K copy(K key, K parentKey) throws Exception
    {
        K result = super.copy(key, parentKey);

        if (isLog())
        {
            SystemCrudUtils.saveLog(getEntity(), LogAction.add, this, Tools.getMessage("crud.copylog", key));
        }

        return result;
    }

    @Override
    protected boolean matchText(E entity, String text) throws Exception
    {
        return Tools.matchText(toString(entity), text);
    }

    @Override
    public String createCondition() throws Exception
    {
        return SystemCrudUtils.getCondition(super.createCondition());
    }

    @Forward(page = Pages.IMP)
    @Override
    public String showImp(K rootKey) throws Exception
    {
        return super.showImp(rootKey);
    }

    @Override
    protected void initImportor(CrudEntityImportor<E, K> importor) throws Exception
    {
        importor.addListiner(ProgressImportLisenter.getInstance());
    }

    @Override
    @NotSerialized
    public EntityTreeOrganizer getOrganizer()
    {
        return super.getOrganizer();
    }
}
