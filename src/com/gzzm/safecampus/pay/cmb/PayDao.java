package com.gzzm.safecampus.pay.cmb;

import com.gzzm.safecampus.campus.account.Merchant;
import com.gzzm.safecampus.campus.pay.Payment;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * 支付查询
 *
 * @author yuanfang
 * @date 18-04-09 11:03
 */
public abstract class PayDao extends GeneralDao
{
    public PayDao()
    {
    }

    /**
     * 根据微信用户ID查找一网通签约协议号
     *
     * @param deptId 部门ID
     * @return agrNo
     * @throws Exception oql异常
     */
    @OQL("select s.agrNo from UserMerchantAccount  s where s.merchant.deptId =:1")
    public abstract String getAgrNo(Integer deptId) throws Exception;

    /**
     * 根据协议号获取用户一网通账户
     *
     * @param agrNo 协议号
     * @return 一网通账户
     * @throws Exception oql异常
     */
    @GetByField("agrNo")
    public abstract UserMerchantAccount getUserMerchantAccount(String agrNo) throws Exception;

    /**
     * 根据部门Id获取商户
     *
     * @param deptId 部门id
     * @return 商户Merchant
     * @throws Exception oql异常
     */
    @GetByField("deptId")
    public abstract Merchant getMerchant(Integer deptId) throws Exception;

    /**
     * 根据账单号获取支付信息
     *
     * @param orderNo 账单号
     * @return 支付信息Payment
     * @throws Exception oql异常
     */
    @GetByField("orderNo")
    public abstract Payment getPayment(String orderNo) throws Exception;

    @OQL("select distinct s.student.deptId from WxStudent  s where s.wxUserId =:1")
    public abstract List<Integer> getWxDeptIds(Integer wxUserId);

    @GetByField("merchantId")
    public abstract String getAgrNoByMerchat(Integer merchantId);

    /**
     * 查询已成功开通了账户的签约账号
     * @param wxUserId 微信用户id
     * @return
     */
    @OQL("select u from UserMerchantAccount u where u.wxUserId = :1 and u.status =1")
    public abstract List<UserMerchantAccount> getAccount(Integer wxUserId);

    //TODO s.merchant.school.schoolStatus
    @OQL("select s from Merchant  s where  s.deptId is not null")
    public abstract List<Merchant> getMerchants();

    @OQL("select s.deptId from Merchant  s where s.school.schoolStatus = 1 limit 1")
    public abstract Integer getHeadSchoolMerchant();

    @OQLUpdate("delete from Payment where createTime < :1 and billStatus =0")
    public abstract Integer checkTimeoutPayment(Timestamp timestamp);

    @OQLUpdate("delete from UserMerchantAccount where createTime < :1 and status =0")
    public abstract Integer checkTimeoutMerchantAccount(Timestamp timestamp);
}
