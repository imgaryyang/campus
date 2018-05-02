package com.gzzm.mo;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 设备绑定
 *
 * @author camel
 * @date 2014/5/14
 */
@Entity(table = "MOBIND", keys = "bindId")
public class MoBind
{
    @Generatable(length = 9)
    private Integer bindId;

    private Integer moUserId;

    @NotSerialized
    private MoUser user;

    /**
     * 绑定时使用的手机号码，由于后来手机号码可能会变，需要保存下手机号码
     */
    @ColumnDescription(type = "varchar(50)")
    private String phone;

    /**
     * 绑定时间
     */
    private Date bindTime;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * ios即时通讯会传递该参数
     */
    private String token;

    /**
     * 是否有效，true为有效，false为无效
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean valid;

    public MoBind()
    {
    }

    public Integer getBindId()
    {
        return bindId;
    }

    public void setBindId(Integer bindId)
    {
        this.bindId = bindId;
    }

    public Integer getMoUserId()
    {
        return moUserId;
    }

    public void setMoUserId(Integer moUserId)
    {
        this.moUserId = moUserId;
    }

    public MoUser getUser()
    {
        return user;
    }

    public void setUser(MoUser user)
    {
        this.user = user;
    }

    public Date getBindTime()
    {
        return bindTime;
    }

    public void setBindTime(Date bindTime)
    {
        this.bindTime = bindTime;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MoBind))
            return false;

        MoBind moBind = (MoBind) o;

        return bindId.equals(moBind.bindId);
    }

    @Override
    public int hashCode()
    {
        return bindId.hashCode();
    }
}
