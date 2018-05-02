package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.BeanUtils;

import java.util.*;

/**
 * 用户所属的树形数据的crud
 *
 * @author camel
 * @date 2010-3-13
 */
@Service
public abstract class UserOwnedTreeCrud<E, K> extends BaseTreeCrud<E, K>
{
    //将用户ID作为查询条件
    @UserId
    private Integer userId;

    public UserOwnedTreeCrud()
    {
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    public Integer getUserId()
    {
        return userId;
    }

    protected Integer getUserId(E entity) throws Exception
    {
        return (Integer) BeanUtils.getValue(entity, "userId");
    }

    protected void setUserId(E entity, Integer userId) throws Exception
    {
        BeanUtils.setValue(entity, "userId", userId);
    }

    protected List<Integer> getUserIds(Collection<K> keys) throws Exception
    {
        return getCrudService().getFieldByKeys(getEntityType(), "userId", keys, Integer.class);
    }

    @Override
    public void initEntity(E entity) throws Exception
    {
        super.initEntity(entity);

        //设置数据的用户ID
        setUserId(getEntity(), userId);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        if (userId == null)
            throw new LoginExpireException();

        super.beforeInsert();

        //设置数据的用户ID
        setUserId(getEntity(), userId);

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        //必须登录才能修改用户数据
        checkKey(getKey(getEntity()));

        //不能将数据转给别人
        setUserId(getEntity(), null);

        return super.beforeUpdate();
    }

    @Override
    public boolean beforeDelete(K key) throws Exception
    {
        checkKey(key);

        return super.beforeDelete(key);
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        checkKeys(Arrays.asList(getKeys()));

        return super.beforeDeleteAll();
    }

    private void checkEntity(E entity, K key) throws Exception
    {
        //必须登录才能修改用户数据
        if (this.userId == null)
            throw new LoginExpireException();

        Integer userId = getUserId(entity);

        //检查数据是否是当前用户的，不是抛出异常
        if (!this.userId.equals(userId))
            throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                    "no auth," + getEntityType().getName() + ",key:" + key + ",userId:" + userId);
    }

    protected void checkEntity(E entity) throws Exception
    {
        checkEntity(entity, getKey(entity));
    }

    protected void checkKey(K key) throws Exception
    {
        checkKeys(Collections.singleton(key));
    }

    protected void checkKeys(Collection<K> keys) throws Exception
    {
        //必须登录才能修改用户数据
        if (this.userId == null)
            throw new LoginExpireException();

        for (Integer userId : getUserIds(keys))
        {
            //检查数据是否是当前用户的，不是抛出异常
            if (!this.userId.equals(userId))
                throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                        "no auth," + getEntityType().getName() + ",keys:" + keys + ",userId:" + userId);
        }
    }
}
