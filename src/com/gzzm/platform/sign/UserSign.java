package com.gzzm.platform.sign;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;

/**
 * 用户签名图片
 *
 * @author camel
 * @date 2015/6/3
 */
@Entity(table = "PFUSERSIGN", keys = "userId")
public class UserSign
{
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 签名图片
     */
    @NotSerialized
    private byte[] sign;

    public UserSign()
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

    public byte[] getSign()
    {
        return sign;
    }

    public void setSign(byte[] sign)
    {
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserSign))
            return false;

        UserSign userSign = (UserSign) o;

        return userId.equals(userSign.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }
}
