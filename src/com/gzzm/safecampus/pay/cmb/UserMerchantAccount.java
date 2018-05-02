package com.gzzm.safecampus.pay.cmb;

import com.gzzm.safecampus.campus.account.Merchant;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.thunwind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * 用户签约支付信息表 - 一网通账户
 * @author yuanfang
 * @date 18-04-09 10:57
 */
@Entity(table = "SCUSERMERCHANTACCOUNT", keys = "accountId")
public class UserMerchantAccount
{
    @Generatable(length = 6)
    private Integer accountId;

    @ToOne("WXUSERID")
    private WxUser wxUser;

    private Integer wxUserId;

    /**
     * 请求时间
     */
    @ColumnDescription(type = "varchar(14)")
    private String dateTime;

    /**
     * 银行通知序号
     */
    @ColumnDescription(type = "varchar(40)")
    private String noticeSerialNo;

    /**
     * 商户分行号
     */
    @ColumnDescription(type = "varchar(4)")
    private String branchNo;

    /**
     *商户号
     */
    @ColumnDescription(type = "varchar(6)")
    private String merchantNo;

    /**
     * 签约请求时填写的用于标识商户用户的唯一ID
     */
    @ColumnDescription(type = "varchar(20)")
    private String userID;

    /**
     * 支付签约协议号，与用户一一对应
     */
    @ColumnDescription(type = "varchar(32)")
    private String agrNo;

    /**
     * 协议开通流水号
     */
    @ColumnDescription(type = "varchar(20)")
    private String merchantSerialNo;

    /**
     * 请求时间
     */
    private Timestamp createTime;

    /**
     * 创建完成时间
     */
    private Timestamp signTime;

    /**
     * 关联商户
     */
    @ToOne("MERCHANTID")
    private Merchant merchant;

    private Integer merchantId;

    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private Integer status;

    public UserMerchantAccount()
    {
    }

    public WxUser getWxUser()
    {
        return wxUser;
    }

    public void setWxUser(WxUser wxUser)
    {
        this.wxUser = wxUser;
    }

    public Integer getWxUserId()
    {
        return wxUserId;
    }

    public void setWxUserId(Integer wxUserId)
    {
        this.wxUserId = wxUserId;
    }

    public Merchant getMerchant()
    {
        return merchant;
    }

    public void setMerchant(Merchant merchant)
    {
        this.merchant = merchant;
    }

    public Integer getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId)
    {
        this.merchantId = merchantId;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public String getNoticeSerialNo()
    {
        return noticeSerialNo;
    }

    public void setNoticeSerialNo(String noticeSerialNo)
    {
        this.noticeSerialNo = noticeSerialNo;
    }

    public String getBranchNo()
    {
        return branchNo;
    }

    public void setBranchNo(String branchNo)
    {
        this.branchNo = branchNo;
    }

    public String getMerchantNo()
    {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo)
    {
        this.merchantNo = merchantNo;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public Timestamp getSignTime()
    {
        return signTime;
    }

    public void setSignTime(Timestamp signTime)
    {
        this.signTime = signTime;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Integer accountId)
    {
        this.accountId = accountId;
    }

    public String getAgrNo()
    {
        return agrNo;
    }

    public void setAgrNo(String agrNo)
    {
        this.agrNo = agrNo;
    }

    public String getMerchantSerialNo()
    {
        return merchantSerialNo;
    }

    public void setMerchantSerialNo(String merchantSerialNo)
    {
        this.merchantSerialNo = merchantSerialNo;
    }

    public Timestamp getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime)
    {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserMerchantAccount that = (UserMerchantAccount) o;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(accountId);
    }

    @Override
    public String toString()
    {
        return agrNo;
    }
}
