package com.gzzm.portal.user.crud;

import com.gzzm.platform.commons.crud.BaseNormalCrud;

/**
 * 上鲜师的列表页面
 *
 * @author camel
 * @date 2011-8-10
 */
public class PortalListCrud<E, K> extends BaseNormalCrud<E, K>
{
    public PortalListCrud()
    {
        setPageSize(5);
    }

    /**
     * 将数据转化为展示对象
     *
     * @param entity 实体对象
     * @return 展示的对象
     * @throws Exception 允许子类抛出异常
     */
    protected Object display(E entity) throws Exception
    {
        return entity;
    }
}
