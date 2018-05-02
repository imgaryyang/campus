package com.gzzm.platform.commons.crud;

import net.cyan.arachne.annotation.Service;
import net.cyan.crud.TreeCrud;

/**
 * @author camel
 * @date 13-11-11
 */
@Service
public abstract class BaseTreeShow<N, K> extends TreeCrud<N, K>
{
    public BaseTreeShow()
    {
    }

    @Override
    public String showTree() throws Exception
    {
        return SystemCrudUtils.treeForward(super.showTree(), this);
    }
}
