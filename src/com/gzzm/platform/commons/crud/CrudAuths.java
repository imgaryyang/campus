package com.gzzm.platform.commons.crud;

import com.gzzm.platform.login.Authoritys;
import net.cyan.crud.*;

/**
 * 和crud相关的权限定义
 *
 * @author camel
 * @date 2010-3-3
 */
public final class CrudAuths
{
    private CrudAuths()
    {
    }

    public static boolean isModifiable()
    {
        Crud crud = CrudConfig.getContext().getCrud();

        if (crud instanceof BaseNormalCrud)
        {
            if (((BaseNormalCrud) crud).isReadOnly())
                return false;
        }

        return Authoritys.hasAuth("modify");
    }

    public static boolean isAddable()
    {
        Crud crud = CrudConfig.getContext().getCrud();

        if (crud instanceof BaseNormalCrud)
        {
            if (((BaseNormalCrud) crud).isReadOnly())
                return false;
        }

        return Authoritys.hasAuth("add");
    }

    public static boolean isDeletable()
    {
        Crud crud = CrudConfig.getContext().getCrud();

        if (crud instanceof BaseNormalCrud)
        {
            if (((BaseNormalCrud) crud).isReadOnly())
                return false;
        }

        return Authoritys.hasAuth("delete");
    }

    public static boolean isExportable()
    {
        return Authoritys.hasAuth("export");
    }

    public static boolean isImpable()
    {
        return Authoritys.hasAuth("imp");
    }
}
