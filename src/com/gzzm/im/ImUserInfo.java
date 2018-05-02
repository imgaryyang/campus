package com.gzzm.im;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.comet.CometService;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 好友信息
 *
 * @author camel
 * @date 2010-12-30
 */
public class ImUserInfo
{
    @Inject
    private static Provider<CometService> cometServiceProvider;

    @Inject
    private static Provider<ImDao> imDaoProvider;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;

    private String deptName;

    private Boolean online;

    /**
     * 顺序
     */
    private int order;

    private String signature;

    /**
     * 是否绑定了手机
     */
    private boolean phoneBound;

    /**
     * 全拼
     */
    private String spell;

    /**
     * 简拼
     */
    private String simpleSpell;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 办公电话
     */
    private String officePhone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 岗位(职位)
     */
    private String duty;

    /**
     * 用户登录时间
     */
    private String loginTime;

    /**
     * 是否好友
     */
    private boolean myFriend;

    public ImUserInfo(Integer userId, String userName, String deptName)
    {
        this.userId = userId;
        this.userName = userName;
        this.deptName = deptName;
        spell = Chinese.getLetters(userName);
        simpleSpell = Chinese.getFirstLetters(userName);
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

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public int getOrder()
    {
        return order;
    }

    void setOrder(int order)
    {
        this.order = order;
    }

    public Boolean isOnline()
    {
        if (online == null)
            online = cometServiceProvider.get().isOnline(userId);
        return online;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public boolean isPhoneBound()
    {
        return phoneBound;
    }

    public void setPhoneBound(boolean phoneBound)
    {
        this.phoneBound = phoneBound;
    }

    public String getSpell()
    {
        return spell;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getOfficePhone()
    {
        return officePhone;
    }

    public void setOfficePhone(String officePhone)
    {
        this.officePhone = officePhone;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getDuty()
    {
        return duty;
    }

    public void setDuty(String duty)
    {
        this.duty = duty;
    }

    public boolean isMyFriend()
    {
        return myFriend;
    }

    public void setMyFriend(boolean myFriend)
    {
        this.myFriend = myFriend;
    }

    public String getLoginTime()
    {
        if (StringUtils.isBlank(loginTime))
        {
            try
            {
                Date time = imDaoProvider.get().getLoginTime(userId);
                loginTime = time != null ? DateUtils.toString(time, "yyyy年MM月dd日 HH:mm") : "未登录过";
            }
            catch (Throwable ex)
            {
                loginTime = "获取失败";
                Tools.log(ex);
            }
        }

        return loginTime;
    }
}
