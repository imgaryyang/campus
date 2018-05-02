package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2017/11/23
 */
public abstract class InformationInvalidLinkClearDao extends GeneralDao
{
    public InformationInvalidLinkClearDao()
    {
    }

    @OQL("select informationId from Information i where informationId>?1 order by informationId limit 300")
    public abstract List<Long> queryInformationIds(Long informationId) throws Exception;

    @OQL("select informationId from InformationEdit i where informationId>?1 order by informationId limit 300")
    public abstract List<Long> queryInformationEditIds(Long informationId) throws Exception;

    @LoadByKey
    public abstract Information getInformation(Long informationId) throws Exception;

    @LoadByKey
    public abstract InformationEdit getInformationEdit(Long informationId) throws Exception;
}
