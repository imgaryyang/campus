package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.EntityTreeDisplayCrud;

/**
 * 系统中所有树型结构展示的基类
 *
 * @author camel
 * @date 2009-10-10
 */
@Service
public abstract class BaseTreeDisplay<E, K> extends EntityTreeDisplayCrud<E, K>
{
    public BaseTreeDisplay()
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
        return SystemCrudUtils.editForward(super.show(key, forward), this);
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
}
