package com.gzzm.im.entitys;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.image.ImageCover;
import net.cyan.thunwind.annotation.*;

/**
 * 用户即时消息配置
 *
 * @author camel
 * @date 2010-9-7
 */
@Entity(table = "IMUSERCONFIG", keys = "userId")
public class ImUserConfig
{
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 关联用户对象
     */
    private User user;

    /**
     * 在线头像
     */
    @NotSerialized
    private byte[] headImg;

    /**
     * 离线头像
     */
    @NotSerialized
    private byte[] offHeadImg;

    /**
     * 签名档
     */
    private String signature;

    @NotSerialized
    private byte[] msgSound;

    @NotSerialized
    private byte[] onlineSound;

    /**
     * 收到消息时是否弹出窗口
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean windowFocus;

    /**
     * 绑定手机
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean phoneBound;

    /**
     * 是否自动弹出系统信息窗口
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean sysAutoShow;

    public ImUserConfig()
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

    public byte[] getHeadImg()
    {
        return headImg;
    }

    public void setHeadImg(byte[] headImg)
    {
        this.headImg = headImg;
    }

    public byte[] getOffHeadImg()
    {
        return offHeadImg;
    }

    public void setOffHeadImg(byte[] offHeadImg)
    {
        this.offHeadImg = offHeadImg;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public byte[] getMsgSound()
    {
        return msgSound;
    }

    public void setMsgSound(byte[] msgSound)
    {
        this.msgSound = msgSound;
    }

    public byte[] getOnlineSound()
    {
        return onlineSound;
    }

    public void setOnlineSound(byte[] onlineSound)
    {
        this.onlineSound = onlineSound;
    }

    public Boolean getWindowFocus()
    {
        return windowFocus;
    }

    public void setWindowFocus(Boolean windowFocus)
    {
        this.windowFocus = windowFocus;
    }

    public Boolean getPhoneBound()
    {
        return phoneBound;
    }

    public void setPhoneBound(Boolean phoneBound)
    {
        this.phoneBound = phoneBound;
    }

    public Boolean getSysAutoShow()
    {
        return sysAutoShow;
    }

    public void setSysAutoShow(Boolean sysAutoShow)
    {
        this.sysAutoShow = sysAutoShow;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ImUserConfig))
            return false;

        ImUserConfig config = (ImUserConfig) o;

        return userId.equals(config.userId);
    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeSave() throws Exception
    {
        if (headImg != null)
        {
            offHeadImg = getOffHeadImg(headImg);
        }
    }

    public static byte[] getOffHeadImg(byte[] headImg) throws Exception
    {
        ImageCover cover = new ImageCover(25);
        cover.read(headImg);
        return cover.toBytes("gif");
    }
}
