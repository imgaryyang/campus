package com.gzzm.safecampus.campus.account;

import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.MaxLen;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 学校商户信息
 * @author yuanfang
 * @date 18-03-06 17:51
 */
@Entity(table = "SCMERCHANT", keys = "merchantId")
public class Merchant extends BaseBean
{
    @Generatable(length = 6)
    private Integer merchantId;

    @ToOne("SCHOOLID")
    @NotSerialized
    private School school;

    private Integer schoolId;

    /**
     * 支行/分行号
     */
    @ColumnDescription(type = "varchar(20)")
    @MaxLen(4)
    private String branchNo;

    /**
     * 商户号
     */
    @ColumnDescription(type = "varchar(20)")
    @MaxLen(6)
    private String merchantNo;

    /**
     * 项目合作编码
     */
    @ColumnDescription(type = "varchar(30)")
    @MaxLen(30)
    private String copCode;

    /**
     * 商户秘钥
     */
    @ColumnDescription(type = "varchar(16)")
    @MaxLen(30)
    private String secretKey;

    public Merchant()
    {
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public String getCopCode()
    {
        return copCode;
    }

    public void setCopCode(String copCode)
    {
        this.copCode = copCode;
    }

    public Integer getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId)
    {
        this.merchantId = merchantId;
    }

    public School getSchool()
    {
        return school;
    }

    public void setSchool(School school)
    {
        this.school = school;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Merchant merchant = (Merchant) o;
        return Objects.equals(merchantId, merchant.merchantId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(merchantId);
    }

    @Override
    public String toString()
    {
        return merchantNo ;
    }
}
