package com.gzzm.safecampus.wx.user;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 微信登录状态，当服务器重启之后不需要重新登录，可直接使用系统
 *
 * @author Neo
 * @date 2018/4/8 16:55
 */
@Entity(table = "SCWXLOGIN", keys = "loginId")
public class WxLogin
{
    /**
     * 登录ID，由uuid生成
     */
    @ColumnDescription(type = "char(64)")
    private String loginId;

    private Integer wxUserId;

    @NotSerialized
    @ToOne
    private WxUser wxUser;

    /**
     * 登录的时间
     */
    private Date loginTime;

    public WxLogin()
    {
    }

    public String getLoginId()
    {
        return loginId;
    }

    public void setLoginId(String loginId)
    {
        this.loginId = loginId;
    }

    public WxUser getWxUser()
    {
        return wxUser;
    }

    public void setWxUser(WxUser wxUser)
    {
        this.wxUser = wxUser;
    }

    public Integer getWxUserId()
    {
        return wxUserId;
    }

    public void setWxUserId(Integer wxUserId)
    {
        this.wxUserId = wxUserId;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }
}
