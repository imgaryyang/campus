package com.gzzm.safecampus.device.card.entity;

import com.gzzm.safecampus.campus.device.CardStatus;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 卡记录，补卡
 *
 * @author liyabin
 * @date 2018/3/24
 */
@Entity(table = "SCCARDITEM", keys = "itemId")
public class CardItem
{
    @Generatable(length = 9)
    private Integer itemId;

    private Integer cardId;

    @ToOne("CARDID")
    private CardInfo cardInfo;
    /**
     * 钱包号(应用类型标识) 01=全国钱包 04=省钱包 08=企业钱包，默认值0
     */
    private String walletFlag;
    /**
     * 钱包交易次数
     */
    private Integer cardSequ;
    /**
     * 最后交易时间
     */
    private Date lastDealTime;
    /**
     * 帐户余额(分)
     */
    private Integer balance;
    /**
     * 补贴余额
     */
    private Integer allowMoney;
    /**
     * 钱包状态 00=未启用01=已启用(默认值) 02=已停用 03=已作废04=黑名单卡
     */
    private CardStatus appState;

    public CardItem()
    {
    }

    public String getWalletFlag()
    {
        return walletFlag;
    }

    public void setWalletFlag(String walletFlag)
    {
        this.walletFlag = walletFlag;
    }

    public Integer getCardSequ()
    {
        return cardSequ;
    }

    public void setCardSequ(Integer cardSequ)
    {
        this.cardSequ = cardSequ;
    }

    public Date getLastDealTime()
    {
        return lastDealTime;
    }

    public void setLastDealTime(Date lastDealTime)
    {
        this.lastDealTime = lastDealTime;
    }

    public Integer getBalance()
    {
        return balance;
    }

    public void setBalance(Integer balance)
    {
        this.balance = balance;
    }

    public Integer getAllowMoney()
    {
        return allowMoney;
    }

    public void setAllowMoney(Integer allowMoney)
    {
        this.allowMoney = allowMoney;
    }

    public CardStatus getAppState()
    {
        return appState;
    }

    public void setAppState(CardStatus appState)
    {
        this.appState = appState;
    }

    public Integer getItemId()
    {
        return itemId;
    }

    public void setItemId(Integer itemId)
    {
        this.itemId = itemId;
    }

    public Integer getCardId()
    {
        return cardId;
    }

    public void setCardId(Integer cardId)
    {
        this.cardId = cardId;
    }

    public CardInfo getCardInfo()
    {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo)
    {
        this.cardInfo = cardInfo;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof CardItem)) return false;

        CardItem item = (CardItem) o;

        return itemId != null ? itemId.equals(item.itemId) : item.itemId == null;
    }

    @Override
    public int hashCode()
    {
        return itemId != null ? itemId.hashCode() : 0;
    }
}
