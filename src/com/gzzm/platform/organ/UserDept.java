package com.gzzm.platform.organ;

import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 用户部门关系映射
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFUSERDEPT", keys = {"userId", "deptId"})
public class UserDept
{
    private Integer userId;

    private Integer deptId;

    private User user;

    private Dept dept;

    /**
     * 排序id，表示本用户在此部门中的序号
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 排序id，表示本单位对于用户的优先级，在登陆时选中部门时用于对部门进行排序
     */
    @ColumnDescription(type = "number(6)")
    private Integer userOrder;

    @OneToMany
    private List<UserRole> roles;

    @OneToMany
    private List<UserStation> stations;

    /**
     * 是否为默认部门，对于同一个用户，只能有一个默认登录部门
     */
    private Boolean defaultDept;

    public UserDept()
    {
    }

    public UserDept(Integer userId, Integer deptId)
    {
        this.userId = userId;
        this.deptId = deptId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Integer getUserOrder()
    {
        return userOrder;
    }

    public void setUserOrder(Integer userOrder)
    {
        this.userOrder = userOrder;
    }

    public List<UserRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<UserRole> roles)
    {
        this.roles = roles;
    }

    public List<UserStation> getStations()
    {
        return stations;
    }

    public void setStations(List<UserStation> stations)
    {
        this.stations = stations;
    }

    public Boolean isDefaultDept()
    {
        return defaultDept;
    }

    public void setDefaultDept(Boolean defaultDept)
    {
        this.defaultDept = defaultDept;
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserDept))
            return false;

        UserDept userDept = (UserDept) o;

        return deptId.equals(userDept.deptId) && userId.equals(userDept.userId);

    }

    public int hashCode()
    {
        int result;
        result = userId.hashCode();
        result = 31 * result + deptId.hashCode();
        return result;
    }
}
