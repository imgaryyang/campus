package com.gzzm.sms;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 短信用户
 *
 * @author camel
 * @date 2010-11-4
 */
@Entity(table = "SMUSER", keys = "userId")
public class SmsUser
{
    /**
     * 用户Id
     */
    @Generatable(length = 6)
    private Integer userId;

    /**
     * 用户名
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String userName;

    /**
     * 登录名
     */
    @Unique
    @Require
    @ColumnDescription(type = "varchar(25)", nullable = false)
    private String loginName;

    /**
     * 密码
     */
    @Require
    @ColumnDescription(type = "varchar(25)", nullable = false)
    private String password;

    /**
     * 此用户允许使用的网关，每种类型的网关仅限一个
     */
    @NotSerialized
    @OneToMany
    private List<UserGateway> gateways;

    /**
     * 处理器对应的className
     */
    private String processor;

    /**
     * 定义哪些用户有权限使用此短信用户
     */
    @NotSerialized
    @ManyToMany(table = "SMUSERAUTH", joinColumn = "SMSUSERID", reverseJoinColumn = "USERID")
    private List<User> authUsers;

    /**
     * 此用户所属的部门，可以为空
     */
    private Integer deptId;

    private Dept dept;

    /**
     * 能否被子部门使用
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean inheritable;

    public SmsUser()
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

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public List<UserGateway> getGateways()
    {
        return gateways;
    }

    public void setGateways(List<UserGateway> gateways)
    {
        this.gateways = gateways;
    }

    public List<User> getAuthUsers()
    {
        return authUsers;
    }

    public void setAuthUsers(List<User> authUsers)
    {
        this.authUsers = authUsers;
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

    public Boolean isInheritable()
    {
        return inheritable;
    }

    public void setInheritable(Boolean inheritable)
    {
        this.inheritable = inheritable;
    }

    @NotSerialized
    public String getGatewayNames()
    {
        return StringUtils.concat(getGateways(), ",");
    }

    /**
     * 获得此用户某个类型的短信使用的网关
     *
     * @param type 网关类型
     * @return 网关对象
     */
    public UserGateway getGateway(GatewayType type)
    {
        if (gateways != null)
        {
            for (UserGateway gateway : gateways)
            {
                if (gateway.getType() == type)
                    return gateway;
            }
        }

        return null;
    }

    public UserGateway getGateway(String type)
    {
        return getGateway(GatewayType.valueOf(type));
    }

    public String getGatewayName(String type)
    {
        UserGateway userGateway = getGateway(type);
        if (userGateway != null)
        {
            Gateway gateway = userGateway.getGateway();
            if (gateway != null)
                return gateway.getGatewayName();
        }

        return "未设置";
    }

    public String getProcessor()
    {
        return processor;
    }

    public void setProcessor(String processor)
    {
        this.processor = processor;
    }

    @SuppressWarnings("unchecked")
    public Processor getProcessorInstance() throws Exception
    {
        if (StringUtils.isEmpty(processor))
            return null;

        Class<Processor> c = (Class<Processor>) Class.forName(this.processor);
        Processor processor = Tools.getBean(c);
        if (processor == null)
            processor = c.newInstance();

        return processor;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsUser))
            return false;

        SmsUser smsUser = (SmsUser) o;

        return userId.equals(smsUser.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }

    @Override
    public String toString()
    {
        return userName;
    }
}
