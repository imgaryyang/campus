package com.gzzm.sms;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Pattern;
import net.cyan.thunwind.annotation.*;

/**
 * 用户网关，标识每个用户能使用哪些网关，和响应的网关参数
 *
 * @author camel
 * @date 2010-11-5
 */
@Entity(table = "SMUSERGATEWAY", keys = {"userId", "type"})
public class UserGateway
{
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 网关类型
     */
    private GatewayType type;

    /**
     * 网关ID
     */
    private Integer gatewayId;

    /**
     * 关联用户对象
     */
    @NotSerialized
    private SmsUser user;

    /**
     * 关联网关对象
     */
    @NotSerialized
    private Gateway gateway;

    /**
     * 服务代码
     */
    private String serviceCode;

    /**
     * 短信字码，加在特服号后面，必须是两位的数字，也可以为空
     */
    @Pattern("^[0-9]{1,2}$")
    @ColumnDescription(type = "varchar(2)")
    private String subNumber;

    private String userName;

    private String password;

    public UserGateway()
    {
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public GatewayType getType()
    {
        return type;
    }

    public void setType(GatewayType type)
    {
        this.type = type;
    }

    public Integer getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    public SmsUser getUser()
    {
        return user;
    }

    public void setUser(SmsUser user)
    {
        this.user = user;
    }

    public Gateway getGateway()
    {
        return gateway;
    }

    public void setGateway(Gateway gateway)
    {
        this.gateway = gateway;
    }

    public String getServiceCode()
    {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode)
    {
        this.serviceCode = serviceCode;
    }

    public String getSubNumber()
    {
        return subNumber;
    }

    public void setSubNumber(String subNumber)
    {
        this.subNumber = subNumber;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserGateway))
            return false;

        UserGateway that = (UserGateway) o;

        return gatewayId.equals(that.gatewayId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode()
    {
        int result = userId.hashCode();
        result = 31 * result + gatewayId.hashCode();
        return result;
    }
}