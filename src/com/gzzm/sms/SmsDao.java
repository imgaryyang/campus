package com.gzzm.sms;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 短信服务相关的数据库操作
 *
 * @author camel
 * @date 2010-11-4
 */
public abstract class SmsDao extends GeneralDao
{
    public SmsDao()
    {
    }

    @OQL("select g from Gateway g where gatewayClass in : order by gatewayName")
    public abstract List<Gateway> getGateways(List<GatewayClass> classes) throws Exception;

    @OQL("select g from Gateway g order by g.gatewayId")
    public abstract List<Gateway> getAllGateways() throws Exception;

    public Gateway getGateway(Integer gatewayId) throws Exception
    {
        return get(Gateway.class, gatewayId);
    }

    public SmsUser getSmsUser(Integer userId) throws Exception
    {
        return get(SmsUser.class, userId);
    }

    @GetByField("loginName")
    public abstract SmsUser getUserByLoginName(String loginName) throws Exception;

    @OQL("select u from SmsUser u where u.deptId=:1 and u.processor is null")
    public abstract SmsUser getUserByDeptId(Integer deptId) throws Exception;

    @OQL("select u from SmsUser u where u.deptId is not null and u.deptId>0 and u.processor is null")
    public abstract List<SmsUser> getAllDeptUsers() throws Exception;

    @OQL("select s from SmsMt s where s.messageId=:1 and s.phone=:2 and s.gatewayId=:3 order by sendTime desc limit 1")
    public abstract SmsMt getSmsByMessageId(String messageId, String phone, Integer gatewayId) throws Exception;

    @OQL("select g from UserGateway g where g.gatewayId=:1")
    public abstract List<UserGateway> getUserGatewaysByGatewayId(Integer gatewayId) throws Exception;

    public UserGateway getUserGateway(Integer userId, GatewayType type) throws Exception
    {
        return get(UserGateway.class, userId, type);
    }

    public void deleteUserGateway(Integer userId, GatewayType type) throws Exception
    {
        delete(UserGateway.class, userId, type);
    }

    /**
     * 获得所有的短信用户
     *
     * @return 短信用户列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select u from SmsUser u order by userName")
    public abstract List<SmsUser> getSmsUsers() throws Exception;

    /**
     * 获得某用户拥有权限的短信用户
     *
     * @param userId 用户ID
     * @return 短信用户列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select u from SmsUser u where exists m in authUsers : m.userId=:1 order by userName")
    public abstract List<SmsUser> getSmsUsers(Integer userId) throws Exception;

    /**
     * 获得某用户拥有权限的短信用户ID
     *
     * @param userId 用户ID列表
     * @return 短信用户列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select u.userId from SmsUser u where exists m in authUsers : m.userId=:1 order by userName")
    public abstract List<Integer> getSmsUserIds(Integer userId) throws Exception;

    /**
     * 读取未阅读的短信
     *
     * @param userId     短信用户ID
     * @param clientCode 客户端编码
     * @return 短信列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select s from SmsMo s where s.userId=:1 and clientCode=?2 and s.state=0 order by s.receiveTime limit 50")
    public abstract List<SmsMo> queryNoReadedSmsMoList(Integer userId, String clientCode) throws Exception;

    /**
     * 读取未阅读的状态报告
     *
     * @param userId     短信用户ID
     * @param clientCode 客户端编码
     * @return 短信列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select s from SmsMt s where s.userId=:1 and clientCode=?2 and s.state=1 order by s.sendTime limit 50")
    public abstract List<SmsMt> queryNoReadedSmsMtList(Integer userId, String clientCode) throws Exception;

    /**
     * 根据某个短信的序号获得回复这个序号的短信
     *
     * @param phone  接收短信的电话号码
     * @param serial 短信序号
     * @return 回复的短信列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select s from SmsMo s where s.phone=:1 and serial=:2 order by s.sendTime limit 50")
    public abstract List<SmsMo> querySmsMoListBySerial(String phone, String serial) throws Exception;

    /**
     * 读取所有未发送的短信
     *
     * @param time_start 只查询某个时间之后的短信
     * @param time_end   只查询某个时间之前的短信
     * @return 短信列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select s.smsId from SmsMt s where s.state=0 and s.messageId is null and s.error is null " +
            "and s.sendTime>=?1 and s.sendTime<=?2 and s.gateway.available=1 order by s.sendTime limit 500")
    public abstract List<Long> queryNoSendedSmsIds(Date time_start, Date time_end) throws Exception;

    public SmsMt getSmsMt(Long smsId) throws Exception
    {
        return load(SmsMt.class, smsId);
    }

    @OQL("select clientCode from SmsMt where serial=:1 and phone=:2")
    public abstract String getClinetCodeBySerialAndPhone(String serial, String phone) throws Exception;

    @OQL("select s from SmsMt s where phone=:1 and gatewayId=:2 and " +
            "serial is not null order by sendTime desc limit 1")
    public abstract SmsMt getLastSmsMtByPhone(String phone, Integer gatewayId) throws Exception;

    @OQL("select s from SmsMt s where phone=:1 and tochar(content)=:2 and sendTime>=addday(sysdate(),-1)" +
            " and state<>3 and error is null order by sendTime desc limit 1")
    public abstract SmsMt getSmsMtByPhoneAndContent(String phone, String content) throws Exception;
}
