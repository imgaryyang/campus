package com.gzzm.portal.datacollection;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author ldp
 * @date 2018/4/26
 */
public abstract class DataCollectionDao extends GeneralDao {

    @OQL("select d from com.gzzm.portal.datacollection.DataList d where d.typeId = ?1 and d.collectTime = ?2 limit 1")
    public abstract DataList getByTypeAndCollectTime(Integer typeId, String collectTime) throws Exception;
}
