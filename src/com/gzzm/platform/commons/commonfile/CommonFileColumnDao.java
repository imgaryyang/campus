package com.gzzm.platform.commons.commonfile;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.Tools;
import net.cyan.thunwind.dao.GeneralDao;
import net.cyan.thunwind.map.*;
import net.cyan.thunwind.storage.*;

import java.util.List;

/**
 * @author camel
 * @date 2017/5/22
 */
public abstract class CommonFileColumnDao extends GeneralDao
{
    public CommonFileColumnDao()
    {
    }

    private <T> List<T> getEntitys(EntityMap<T> entityMap) throws Exception
    {
        StringBuilder buffer =
                new StringBuilder("select t from ").append(entityMap.getType().getName()).append(" t where ");
        boolean b = false;
        for (ColumnMap columnMap : entityMap.getMembers(ColumnMap.class))
        {
            ColumnStorage storage = columnMap.getStorage();
            if (storage instanceof CommonFileColumnStorage)
            {
                CommonFileColumnStorage commonFileColumnStorage = (CommonFileColumnStorage) storage;
                if (commonFileColumnStorage.isClear())
                {
                    String pathColumn = commonFileColumnStorage.getPathColumn();

                    if (b)
                    {
                        buffer.append(" or ");
                    }
                    else
                    {
                        b = true;
                    }

                    buffer.append("t.").append(pathColumn).append(" is null");

//                    buffer.append("(t.").append(pathColumn).append(" is null and t.").append(columnMap.getField())
//                            .append(" is not null)");
                }
            }
        }

        if (b)
        {
            buffer.append(" limit 300");
            return oqlQuery(buffer.toString());
        }
        else
        {
            return null;
        }
    }

    public <T> boolean fetch(Class<T> c) throws Exception
    {
        EntityMap<T> entityMap = getSession().getManager().getEntityMap(c);

        List<T> entitys = getEntitys(entityMap);

        if (entitys == null)
            return false;

        for (T entity : entitys)
        {
            try
            {
                initEntity(entityMap, entity);
                update(entity);
            }
            catch (Throwable ex)
            {
                Tools.log(ex);
            }
        }

        return entitys.size() > 0;
    }

    private <T> void initEntity(EntityMap<T> entityMap, T entity) throws Exception
    {
        for (ColumnMap columnMap : entityMap.getMembers(ColumnMap.class))
        {
            ColumnStorage storage = columnMap.getStorage();
            if (storage instanceof CommonFileColumnStorage)
            {
                CommonFileColumnStorage commonFileColumnStorage = (CommonFileColumnStorage) storage;
                if (commonFileColumnStorage.isClear())
                {
                    columnMap.set(entity, columnMap.get(entity));
                }
            }
        }

        if (entity instanceof Attachment)
            ((Attachment) entity).setType(AttachmentType.file);
    }
}
