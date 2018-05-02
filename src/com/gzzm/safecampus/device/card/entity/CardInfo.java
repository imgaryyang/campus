package com.gzzm.safecampus.device.card.entity;

import com.gzzm.safecampus.campus.device.CardType;
import com.gzzm.safecampus.device.card.enumtype.UseFlag;
import net.cyan.thunwind.annotation.*;

/**
 * 卡号号信息
 *
 * @author liyabin
 * @date 2018/3/23
 */
@Entity(table = "SCCARDINFO", keys = "cardId")
public class CardInfo
{
    @Generatable(length = 9)
    private Integer cardId;

    @ColumnDescription(type = "VARCHAR2(9)")
    private String cardNo;

    @ColumnDescription(type = "VARCHAR2(12)")
    private String cardSn;

    @ColumnDescription(type = "VARCHAR2(32)")
    private String passWord;

    private UseFlag passFlag;
    /**
     * 卡类型
     */
    private CardType cardType;
    /**
     * 学校流水号
     */
    @ColumnDescription(type = "varchar2(3)")
    private String schoolNum;

    public CardInfo()
    {
    }

    public Integer getCardId()
    {
        return cardId;
    }

    public void setCardId(Integer cardId)
    {
        this.cardId = cardId;
    }

    public String getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo;
    }


    public String getPassWord()
    {
        return passWord;
    }

    public void setPassWord(String passWord)
    {
        this.passWord = passWord;
    }

    public UseFlag getPassFlag()
    {
        return passFlag;
    }

    public void setPassFlag(UseFlag passFlag)
    {
        this.passFlag = passFlag;
    }

    public CardType getCardType()
    {
        return cardType;
    }

    public void setCardType(CardType cardType)
    {
        this.cardType = cardType;
    }

    public String getSchoolNum()
    {
        return schoolNum;
    }

    public void setSchoolNum(String schoolNum)
    {
        this.schoolNum = schoolNum;
    }

    public String getCardSn()
    {
        return cardSn;
    }

    public void setCardSn(String cardSn)
    {
        this.cardSn = cardSn;
    }
}
