package com.gzzm.safecampus.device.card.entity;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author liyabin
 * @date 2018/3/21
 */
@Entity(table = "SCCARDAPPTYPE", keys = "id")
public class CardAppType
{
    @Generatable(length = 9)
    private Integer id;
    /**
     * 应用索引号,Key
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer appIndex;
    /**
     * 应用名称
     */
    @ColumnDescription(type = "VARCHAR2(20)")
    private String appName;
    /**
     * 有效期
     */
    private Date expireDate;
    /**
     * 应用类型
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer cardAppType;
    /**
     * 钱包类型
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer walletType;
    /**
     * 钱包余额类型
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer walletBalType;
    /**
     * 钱包透支限额(分)
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer walletOverLimit;
    /**
     * 联机借贷标记
     0=贷记卡
     1=借记卡
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer debitFlag;
    /**
     * 贷记最高额(分)
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer maxDebit;
    /**
     * 钱包余额有效期
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer balanceValidity;
    /**
     * 钱包余额有效期启用标识1
     */
    @ColumnDescription(type = "NUMBER(8)")
    private Integer balanceValidityFlag;
    /**
     * 操作用户
     */
    @ColumnDescription(type = "VARCHAR2(20)")
    private String opUser;
    /**
     * 操作日期
     */
    private Date opDate;
    /**
     * 创建用户
     */
    @ColumnDescription(type = "VARCHAR2(20)")
    private String createUser;
    /**
     * 创建日期
     */
    private Date createDate;
    /**
     * 备注
     */
    @ColumnDescription(type = "VARCHAR2(50)")
    private String description;


    public CardAppType()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getAppIndex()
    {
        return appIndex;
    }

    public void setAppIndex(Integer appIndex)
    {
        this.appIndex = appIndex;
    }

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public Date getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(Date expireDate)
    {
        this.expireDate = expireDate;
    }

    public Integer getCardAppType()
    {
        return cardAppType;
    }

    public void setCardAppType(Integer cardAppType)
    {
        this.cardAppType = cardAppType;
    }

    public Integer getWalletType()
    {
        return walletType;
    }

    public void setWalletType(Integer walletType)
    {
        this.walletType = walletType;
    }

    public Integer getWalletBalType()
    {
        return walletBalType;
    }

    public void setWalletBalType(Integer walletBalType)
    {
        this.walletBalType = walletBalType;
    }

    public Integer getWalletOverLimit()
    {
        return walletOverLimit;
    }

    public void setWalletOverLimit(Integer walletOverLimit)
    {
        this.walletOverLimit = walletOverLimit;
    }

    public Integer getDebitFlag()
    {
        return debitFlag;
    }

    public void setDebitFlag(Integer debitFlag)
    {
        this.debitFlag = debitFlag;
    }

    public Integer getMaxDebit()
    {
        return maxDebit;
    }

    public void setMaxDebit(Integer maxDebit)
    {
        this.maxDebit = maxDebit;
    }

    public Integer getBalanceValidity()
    {
        return balanceValidity;
    }

    public void setBalanceValidity(Integer balanceValidity)
    {
        this.balanceValidity = balanceValidity;
    }

    public Integer getBalanceValidityFlag()
    {
        return balanceValidityFlag;
    }

    public void setBalanceValidityFlag(Integer balanceValidityFlag)
    {
        this.balanceValidityFlag = balanceValidityFlag;
    }

    public String getOpUser()
    {
        return opUser;
    }

    public void setOpUser(String opUser)
    {
        this.opUser = opUser;
    }

    public Date getOpDate()
    {
        return opDate;
    }

    public void setOpDate(Date opDate)
    {
        this.opDate = opDate;
    }

    public String getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
