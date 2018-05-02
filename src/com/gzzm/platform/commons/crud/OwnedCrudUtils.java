package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.log.LogAction;
import net.cyan.commons.util.*;

import java.util.Collection;

/**
 * OwnedCrud相关操作的工具类
 *
 * @author camel
 * @date 2010-3-4
 */
public final class OwnedCrudUtils
{
    private OwnedCrudUtils()
    {
    }

    public static Class getOwnerKeyType(Class<? extends OwnedCrud> crudType) throws Exception
    {
        try
        {
            return BeanUtils.toClass(BeanUtils.getRealType(OwnedCrud.class, "OK", crudType));
        }
        catch (UtilException ex)
        {
            ExceptionUtils.wrapException(ex);
            return null;
        }
    }

    public static <K, E, OK> void moveTo(Collection<K> keys, OK ownerKey, OK oldOwnerKey, OwnedCrud<E, K, OK> crud)
            throws Exception
    {
        Class<E> entityType = crud.getEntityType();

        for (K key : keys)
        {
            E entity = entityType.newInstance();
            crud.setKey(entity, key);

            if (crud instanceof LogableCrud && ((LogableCrud) crud).isLog())
            {
                //记录日志
                try
                {
                    SystemCrudUtils.saveLog(crud.getEntity(key), LogAction.modify, crud,
                            Tools.getMessage("crud.movelog", oldOwnerKey, ownerKey));
                }
                catch (Throwable ex)
                {
                    //记录日志错误不影响主题流程
                    Tools.log(ex);
                }
            }

            crud.setOwnerKey(entity, ownerKey);

            crud.update(entity);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static <K, E, OK> void copyTo(Collection<K> keys, OK ownerKey, OK oldOwnerKey, OwnedCrud<E, K, OK> crud)
            throws Exception
    {
        for (K key : keys)
        {
            E entity = crud.clone(crud.getEntity(key));
            crud.setOwnerKey(entity, ownerKey);

            crud.insert(entity);

            //设置entity到crud对象中，以方便后面的程序调用
            crud.setEntity(entity);

            if (crud instanceof LogableCrud && ((LogableCrud) crud).isLog())
            {
                //记录日志
                try
                {
                    SystemCrudUtils.saveLog(entity, LogAction.add, crud, Tools.getMessage("crud.copylog", key));
                }
                catch (Throwable ex)
                {
                    //记录日志错误不影响主题流程
                    Tools.log(ex);
                }
            }
        }
    }
}