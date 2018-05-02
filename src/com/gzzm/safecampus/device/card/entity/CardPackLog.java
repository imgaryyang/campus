package com.gzzm.safecampus.device.card.entity;

import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/28
 */
@Entity(table="SCCARDPACKLOG",keys = "logId")
public class CardPackLog
{
    @Generatable(length = 8)
    private  Integer  logId;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 卡序列号
     */
    private String cardSq;
    /**
     * 钱包号(应用类型标识) 01=全国钱包 04=省钱包 08=企业钱包，默认值0
     */
    private String walletFlag;
    /**
     * 交易次数
     */
    private String cardSequ;
    /**
     * 初始余额
     */
    private Integer balance;

    public CardPackLog()
    {
    }

    public Integer getLogId()
    {
        return logId;
    }

    public void setLogId(Integer logId)
    {
        this.logId = logId;
    }

    public String getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo;
    }

    public String getCardSq()
    {
        return cardSq;
    }

    public void setCardSq(String cardSq)
    {
        this.cardSq = cardSq;
    }

    public String getWalletFlag()
    {
        return walletFlag;
    }

    public void setWalletFlag(String walletFlag)
    {
        this.walletFlag = walletFlag;
    }

    public String getCardSequ()
    {
        return cardSequ;
    }

    public void setCardSequ(String cardSequ)
    {
        this.cardSequ = cardSequ;
    }

    public Integer getBalance()
    {
        return balance;
    }

    public void setBalance(Integer balance)
    {
        this.balance = balance;
    }
}
