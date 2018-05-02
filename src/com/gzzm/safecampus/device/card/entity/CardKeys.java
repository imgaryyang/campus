package com.gzzm.safecampus.device.card.entity;

import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/21
 */
@Entity(table = "SCCARDKEYS", keys = "id")
public class CardKeys
{
    @Generatable(length = 9)
    private Integer id;
    /**
     *CPU卡AID   (ERKeyCPU加密)
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String cpuaid;
    /**
     * CPU卡分散因子  (ERKeyCPU加密)
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String cpukey;
    /**
     * M1充值密钥  (ERKeyV2加密)
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String m1addkey;
    /**
     * M1消费密钥  (ERKeyV2加密)
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String m1deckey;
    /**
     * M1 TAC密钥  (ERKeyV2加密)
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String m1tackey;
    /**
     * 2.0 目录区扇区号
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String sectorv2;
    /**
     * 1.0卡格式扇区编号。
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String sectorv1;
    /**
     * 1.0卡格式密钥  (ERKeyV2加密)
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String keyv1;
    /**
     * 应用密钥对应M1卡密钥
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String erkeyv2;
    /**
     * 应用密钥对应CPU卡密钥
     */
    @ColumnDescription(type = "VARCHAR2(32)")
    private String erkeycpu;

    public CardKeys()
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

    public String getCpuaid()
    {
        return cpuaid;
    }

    public void setCpuaid(String cpuaid)
    {
        this.cpuaid = cpuaid;
    }

    public String getCpukey()
    {
        return cpukey;
    }

    public void setCpukey(String cpukey)
    {
        this.cpukey = cpukey;
    }

    public String getM1addkey()
    {
        return m1addkey;
    }

    public void setM1addkey(String m1addkey)
    {
        this.m1addkey = m1addkey;
    }

    public String getM1deckey()
    {
        return m1deckey;
    }

    public void setM1deckey(String m1deckey)
    {
        this.m1deckey = m1deckey;
    }

    public String getM1tackey()
    {
        return m1tackey;
    }

    public void setM1tackey(String m1tackey)
    {
        this.m1tackey = m1tackey;
    }

    public String getSectorv2()
    {
        return sectorv2;
    }

    public void setSectorv2(String sectorv2)
    {
        this.sectorv2 = sectorv2;
    }

    public String getSectorv1()
    {
        return sectorv1;
    }

    public void setSectorv1(String sectorv1)
    {
        this.sectorv1 = sectorv1;
    }

    public String getKeyv1()
    {
        return keyv1;
    }

    public void setKeyv1(String keyv1)
    {
        this.keyv1 = keyv1;
    }

    public String getErkeyv2()
    {
        return erkeyv2;
    }

    public void setErkeyv2(String erkeyv2)
    {
        this.erkeyv2 = erkeyv2;
    }

    public String getErkeycpu()
    {
        return erkeycpu;
    }

    public void setErkeycpu(String erkeycpu)
    {
        this.erkeycpu = erkeycpu;
    }
}
