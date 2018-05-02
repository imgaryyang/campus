package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.BeanUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用户所属的列表数据的crud
 *
 * @author camel
 * @date 2010-3-13
 */
@Service
public abstract class UserOwnedNormalCrud<E, K> extends BaseNormalCrud<E, K>
{
    private static final String[] ORDERWITHFIELDS = {"userId"};

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    public UserOwnedNormalCrud()
    {
    }

    @NotSerialized
    public Integer getUserId()
    {
        if (userOnlineInfo == null)
            return null;
        return userOnlineInfo.getUserId();
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
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

        setUserId(entity, getUserId());
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        //设置数据的用户ID
        setUserId(getEntity(), getUserId());

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        checkKey(getKey(getEntity()));

        //不能将数据转给别人
        setUserId(getEntity(), null);

        return true;
    }

    @Override
    public boolean beforeDelete(K key) throws Exception
    {
        checkKey(key);

        return true;
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        checkKeys(Arrays.asList(getKeys()));

        return true;
    }

    private void checkEntity(E entity, K key) throws Exception
    {
        //必须登录才能修改用户数据
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        if (userOnlineInfo.isAdmin())
            return;

        Integer userId = getUserId(entity);

        //检查数据是否是当前用户的，不是抛出异常
        if (!this.getUserId().equals(userId))
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
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        if (userOnlineInfo.isAdmin())
            return;

        for (Integer userId : getUserIds(keys))
        {
            //检查数据是否是当前用户的，不是抛出异常
            if (!this.getUserId().equals(userId))
                throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                        "no auth," + getEntityType().getName() + ",keys:" + keys + ",userId:" + userId);
        }
    }
}
