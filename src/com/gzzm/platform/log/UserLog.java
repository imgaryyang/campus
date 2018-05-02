package com.gzzm.platform.log;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.*;

/**
 * 用户操作日志，包括用户修改日志和登录日志等
 *
 * @author camel
 * @date 2009-7-30
 */
public class UserLog extends Log
{
    /**
     * 操作者的用户id
     */
    private Integer userId;

    /**
     * 操作者
     */
    private User user;

    /**
     * 操作者的部门id
     */
    @Index
    private Integer deptId;

    /**
     * 部门
     */
    private Dept dept;

    /**
     * 操作者登录使用的证书类型
     */
    @ColumnDescription(type = "varchar(20)")
    private String certType;

    /**
     * 操作者登录使用的证书id
     */
    @ColumnDescription(type = "varchar(250)")
    private String certId;

    /**
     * 操作者登录时使用的证书的名称
     */
    @ColumnDescription(type = "varchar(250)")
    private String certName;

    /**
     * 操作者登录的客户端ip
     */
    @ColumnDescription(type = "varchar(60)")
    private String ip;

    /**
     * 登录时的浏览器版本
     */
    @ColumnDescription(type = "varchar(250)")
    private String navigator;

    public UserLog()
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

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public String getCertType()
    {
        return certType;
    }

    public void setCertType(String certType)
    {
        this.certType = certType;
    }

    public String getCertId()
    {
        return certId;
    }

    public void setCertId(String certId)
    {
        this.certId = certId;
    }

    public String getCertName()
    {
        return certName;
    }

    public void setCertName(String certName)
    {
        this.certName = certName;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getNavigator()
    {
        return navigator;
    }

    public void setNavigator(String navigator)
    {
        this.navigator = navigator;
    }

    /**
     * 根据在线用户信息填充数据
     *
     * @param userOnlineInfo 在线用户信息
     */
    public void fill(UserOnlineInfo userOnlineInfo)
    {
        setUserId(userOnlineInfo.getUserId());
        setCertType(userOnlineInfo.getCertType());
        setCertId(userOnlineInfo.getCertId());
        setCertName(userOnlineInfo.getCertName());
        setDeptId(userOnlineInfo.getDeptId());
        setIp(userOnlineInfo.getIp());
        setNavigator(userOnlineInfo.getNavigator());
    }
}