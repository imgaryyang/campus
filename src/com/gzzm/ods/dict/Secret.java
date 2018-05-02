package com.gzzm.ods.dict;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 秘密等级实体
 *
 * @author db
 * @date 11-12-31
 */
@Entity(table = "ODSECRET", keys = "secretId")
public class Secret
{
    /**
     * 主键
     */
    @Generatable(length = 7)
    private Integer secretId;

    /**
     * 秘密等级名称
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String secretName;

    /**
     * 排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public Secret()
    {
    }

    public Integer getSecretId()
    {
        return secretId;
    }

    public void setSecretId(Integer secretId)
    {
        this.secretId = secretId;
    }

    public String getSecretName()
    {
        return secretName;
    }

    public void setSecretName(String secretName)
    {
        this.secretName = secretName;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Secret))
            return false;

        Secret secret = (Secret) o;

        return secretId.equals(secret.secretId);
    }

    @Override
    public int hashCode()
    {
        return secretId.hashCode();
    }

    @Override
    public String toString()
    {
        return secretName;
    }
}
