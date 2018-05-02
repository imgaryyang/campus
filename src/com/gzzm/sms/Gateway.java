package com.gzzm.sms;

import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.Map;

/**
 * 短信网关
 *
 * @author camel
 * @date 2010-11-4
 */
@Entity(table = "SMGATEWAY", keys = "gatewayId")
public class Gateway
{
    /**
     * 网关ID，主键，由序列号生成
     */
    @Generatable(length = 6)
    private Integer gatewayId;

    /**
     * 短信网关名
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String gatewayName;

    /**
     * 网关类型
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private GatewayClass gatewayClass;

    /**
     * 网关服务器ip
     */
    @Require
    @ColumnDescription(type = "varchar(20)", nullable = false)
    private String ip;

    /**
     * 端口
     */
    @Range(min = "0", max = "99999")
    @ColumnDescription(type = "number(5)", nullable = false)
    private Integer port;

    private Integer receivePort;

    /**
     * 网关服务器用户名
     */
    @Require
    @ColumnDescription(type = "varchar(20)", nullable = false)
    private String userName;

    /**
     * 网关服务器密码
     */
    @Require
    @ColumnDescription(type = "varchar(20)", nullable = false)
    private String password;

    /**
     * 特服号
     */
    @ColumnDescription(type = "varchar(20)")
    private String spNumber;

    /**
     * 企业编码
     */
    @ColumnDescription(type = "varchar(20)")
    private String spId;

    /**
     * 网关服务器的其它参数
     */
    @ValueMap(table = "SMGATEWAYARG", keyColumn = "ARGNAME", valueColumn = "ARGVALUE")
    private Map<String, String> args;

    /**
     * 是否有效
     */
    @Require
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean available;

    public Gateway()
    {
    }

    public Gateway(Integer gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    public Integer getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    public String getGatewayName()
    {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName)
    {
        this.gatewayName = gatewayName;
    }

    public GatewayClass getGatewayClass()
    {
        return gatewayClass;
    }

    public void setGatewayClass(GatewayClass gatewayClass)
    {
        this.gatewayClass = gatewayClass;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Integer getReceivePort()
    {
        return receivePort;
    }

    public void setReceivePort(Integer receivePort)
    {
        this.receivePort = receivePort;
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

    public String getSpNumber()
    {
        return spNumber;
    }

    public void setSpNumber(String spNumber)
    {
        this.spNumber = spNumber;
    }

    public String getSpId()
    {
        return spId;
    }

    public void setSpId(String spId)
    {
        this.spId = spId;
    }

    public Map<String, String> getArgs()
    {
        return args;
    }

    public void setArgs(Map<String, String> args)
    {
        this.args = args;
    }

    public Boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(Boolean available)
    {
        this.available = available;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof Gateway))
            return false;

        Gateway gateway = (Gateway) o;

        return gatewayId.equals(gateway.gatewayId);

    }

    @Override
    public int hashCode()
    {
        return gatewayId.hashCode();
    }

    @Override
    public String toString()
    {
        return gatewayName;
    }
}
