package com.gzzm.platform.receiver;

import com.gzzm.platform.organ.UserInfo;

import java.util.Collection;

/**
 * 接收者加载的上下文
 *
 * @author camel
 * @date 2010-4-28
 */
public class ReceiversLoadContext
{
    /**
     * 用户ID
     *
     * @see #type
     */
    public static final String USERID = "userId";

    /**
     * email类型的常量
     *
     * @see #type
     */
    public static final String EMAIL = "email";

    /**
     * 手机类型号码类型的常量
     *
     * @see #type
     */
    public static final String PHONE = "phone";

    /**
     * 岗位ID
     *
     * @see #type
     */
    public static final String STATIONID = "stationId";

    private UserInfo user;

    /**
     * 接收者的类型，默认支持email和手机号码，可扩展
     *
     * @see #USERID
     * @see #EMAIL
     * @see #PHONE
     * @see #STATIONID
     */
    private String type;

    /**
     * 对此功能拥有权限的部门列表
     */
    private Collection<Integer> authDeptIds;

    /**
     * 最多查询多少条记录
     */
    private int maxCount;

    public ReceiversLoadContext()
    {
    }

    public UserInfo getUser()
    {
        return user;
    }

    void setUser(UserInfo user)
    {
        this.user = user;
    }

    public Integer getUserId()
    {
        return user.getUserId();
    }

    public String getType()
    {
        return type;
    }

    void setType(String type)
    {
        this.type = type;
    }

    public Integer getDeptId()
    {
        return user.getDeptId();
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
    }

    public int getMaxCount()
    {
        return maxCount;
    }

    void setMaxCount(int maxCount)
    {
        this.maxCount = maxCount;
    }
}
