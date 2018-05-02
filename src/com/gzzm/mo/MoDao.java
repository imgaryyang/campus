package com.gzzm.mo;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2014/5/13
 */
public abstract class MoDao extends GeneralDao
{
    public MoDao()
    {
    }

    /**
     * 获得所有客户端类型
     *
     * @return 所有客户端类型的列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select t from MoClientType t order by orderId")
    public abstract List<MoClientType> getTypes() throws Exception;

    @GetByField("typeCode")
    public abstract MoClientType getClientTypeByCode(String code) throws Exception;

    @OQL("select c from MoClient c where typeId=:1 order by publishTime desc")
    public abstract MoClient getLastClient(Integer typeId) throws Exception;

    @GetByField("userId")
    public abstract List<MoUser> getMoUsers(Integer userId);

    @OQLUpdate("delete from MoBind m where m.moUserId in :1")
    public abstract Integer deleteMoBinds(Integer[] MoUserId);

    @OQLUpdate("delete from MoBind m where m.moUserId = :1")
    public abstract Integer deleteMoBind(Integer MoUserId);

    public MoUser getUser(Integer userId) throws Exception
    {
        return load(MoUser.class, userId);
    }

    /**
     * 根据手机号码获得移动办公用户
     *
     * @param phone 手机号码
     * @return 移动办公用户的MoUser对象
     * @throws Exception 数据库查询错误
     */
    @GetByField("phone")
    public abstract MoUser getUserByPhone(String phone) throws Exception;

    /**
     * 根据设备id和电话号码得到绑定的用户信息
     *
     * @param deviceId 设备ID
     * @param phone    电话号码
     * @return 绑定对象，关联用户信息
     * @throws Exception 数据库查询错误
     */
    @OQL("select b from MoBind b where deviceId=:1 and phone=:2 and valid=1 order by bindTime desc")
    public abstract MoBind getBind(String deviceId, String phone) throws Exception;

    /**
     * 根据设备id和电话号码得到绑定的用户信息
     *
     * @param deviceId 设备ID
     * @return 绑定对象，关联用户信息
     * @throws Exception 数据库查询错误
     */
    @OQL("select b from MoBind b where deviceId=:1 and valid=1 order by bindTime desc")
    public abstract MoBind getBindByDeviceId(String deviceId) throws Exception;
}
