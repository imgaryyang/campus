package com.gzzm.safecampus.device.card.entity;

import com.gzzm.safecampus.device.card.enumtype.UseFlag;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author liyabin
 * @date 2018/3/28
 */
@Entity(table = "SCCARDINFODETAILED", keys = "detailedId")
public class CardInfoDetailed
{
    @Generatable(length = 8)
    private Integer detailedId;

    @ColumnDescription(type = "NUMBER(10)")
    private Integer cardId;
    /**
     * 用户编号
     */
    private Integer targetId;
    /**
     * 卡号，Key
     */
    private Integer cardNo;
    /**
     * 卡序列号
     */
    @ColumnDescription(type = "varchar(20)")
    private String cardSn;
    /**
     * 卡格式种类标识
     */
    private Integer cardType;
    /**
     * 手机号
     */
    @ColumnDescription(type = "varchar(13)")
    private String mobilePhone;
    /**
     * 卡种标识
     */
    @ColumnDescription(type = "varchar(4)")
    private String cardTypeCode;
    /**
     * 地区编码
     */
    @ColumnDescription(type = "varchar(4)")
    private String areaCode;
    /**
     * 启用标识 0=未启用(默认值) 1=已启用
     */
    private UseFlag useFlag;
    /**
     * 客户类型 01=政企客户 02=家庭客户 03=个人客户(默认值) 04=其它客户
     */
    private Integer customerType;
    /**
     * 职工标识 00=外部职工01=内部职工(默认值)
     */
    private Integer empFlag;
    /**
     * 卡状态 00=未启用01=已启用(默认值) 02=已停用 03=已退卡 04=黑名单卡
     */
    private Integer cardState;
    /**
     * 卡认证码（由卡号计算得出）
     */
    @ColumnDescription(type = "varchar(32)")
    private String cardVerifyCode;
    /**
     * 卡启用日期
     */
    private Date cardBegDate;
    /**
     * 卡结束日期
     */
    private Date cardEndDate;
    /**
     * 卡成本(分)
     */
    private Integer depositValue;
    /**
     * 管理费(分)
     */
    private Integer maintenance;
    /**
     * 卡密码，对应卡上的超额密码,
     默认6个0
     */
    private String password;
    /**
     * 启用个人密码，0不启用，1启用
     */
    private Integer usePwd;
    /**
     * 单次限额，单位为分
     */
    private Integer limitOneTime;
    /**
     * 每日限额，单位为分
     */
    private Integer limitDay;
    /**
     * 权限类别，取值0-255 (为2.0增加字段,BY ZHU)
     */
    private Integer privillege;
    /**
     * 读写器物理ID
     */
    private String machID;
    /**
     * 当前登录员的企业ID
     */
    private String  empUserCode;
    /**
     * 操作人
     */
    @ColumnDescription(type = "varchar(20)")
    private String  opuser;

    public CardInfoDetailed()
    {
    }

    public Integer getDetailedId()
    {
        return detailedId;
    }

    public void setDetailedId(Integer detailedId)
    {
        this.detailedId = detailedId;
    }

    public Integer getCardId()
    {
        return cardId;
    }

    public void setCardId(Integer cardId)
    {
        this.cardId = cardId;
    }

    public Integer getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Integer targetId)
    {
        this.targetId = targetId;
    }

    public Integer getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(Integer cardNo)
    {
        this.cardNo = cardNo;
    }

    public String getCardSn()
    {
        return cardSn;
    }

    public void setCardSn(String cardSn)
    {
        this.cardSn = cardSn;
    }

    public Integer getCardType()
    {
        return cardType;
    }

    public void setCardType(Integer cardType)
    {
        this.cardType = cardType;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getCardTypeCode()
    {
        return cardTypeCode;
    }

    public void setCardTypeCode(String cardTypeCode)
    {
        this.cardTypeCode = cardTypeCode;
    }

    public String getAreaCode()
    {
        return areaCode;
    }

    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    public UseFlag getUseFlag()
    {
        return useFlag;
    }

    public void setUseFlag(UseFlag useFlag)
    {
        this.useFlag = useFlag;
    }

    public Integer getCustomerType()
    {
        return customerType;
    }

    public void setCustomerType(Integer customerType)
    {
        this.customerType = customerType;
    }

    public Integer getEmpFlag()
    {
        return empFlag;
    }

    public void setEmpFlag(Integer empFlag)
    {
        this.empFlag = empFlag;
    }

    public Integer getCardState()
    {
        return cardState;
    }

    public void setCardState(Integer cardState)
    {
        this.cardState = cardState;
    }

    public String getCardVerifyCode()
    {
        return cardVerifyCode;
    }

    public void setCardVerifyCode(String cardVerifyCode)
    {
        this.cardVerifyCode = cardVerifyCode;
    }

    public Date getCardBegDate()
    {
        return cardBegDate;
    }

    public void setCardBegDate(Date cardBegDate)
    {
        this.cardBegDate = cardBegDate;
    }

    public Date getCardEndDate()
    {
        return cardEndDate;
    }

    public void setCardEndDate(Date cardEndDate)
    {
        this.cardEndDate = cardEndDate;
    }

    public Integer getDepositValue()
    {
        return depositValue;
    }

    public void setDepositValue(Integer depositValue)
    {
        this.depositValue = depositValue;
    }

    public Integer getMaintenance()
    {
        return maintenance;
    }

    public void setMaintenance(Integer maintenance)
    {
        this.maintenance = maintenance;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getUsePwd()
    {
        return usePwd;
    }

    public void setUsePwd(Integer usePwd)
    {
        this.usePwd = usePwd;
    }

    public Integer getLimitOneTime()
    {
        return limitOneTime;
    }

    public void setLimitOneTime(Integer limitOneTime)
    {
        this.limitOneTime = limitOneTime;
    }

    public Integer getLimitDay()
    {
        return limitDay;
    }

    public void setLimitDay(Integer limitDay)
    {
        this.limitDay = limitDay;
    }

    public Integer getPrivillege()
    {
        return privillege;
    }

    public void setPrivillege(Integer privillege)
    {
        this.privillege = privillege;
    }

    public String getMachID()
    {
        return machID;
    }

    public void setMachID(String machID)
    {
        this.machID = machID;
    }

    public String getEmpUserCode()
    {
        return empUserCode;
    }

    public void setEmpUserCode(String empUserCode)
    {
        this.empUserCode = empUserCode;
    }

    public String getOpuser()
    {
        return opuser;
    }

    public void setOpuser(String opuser)
    {
        this.opuser = opuser;
    }
}
