package com.gzzm.platform.organ;

import net.cyan.thunwind.annotation.*;

/**
 * UserStation实体，对应PFUSERSTATION表，记录某个用户在某个部门中拥有哪些岗位
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFUSERSTATION", keys = {"userId", "deptId", "stationId"})
public class UserStation
{
    private Integer userId;

    private Integer deptId;

    private Integer stationId;

    private User user;

    private Dept dept;

    @Lazy(false)
    @Cascade
    private Station station;

    public UserStation()
    {
    }

    public UserStation(Integer userId, Integer deptId, Integer stationId)
    {
        this.userId = userId;
        this.deptId = deptId;
        this.stationId = stationId;
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

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
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

    public Station getStation()
    {
        return station;
    }

    public void setStation(Station station)
    {
        this.station = station;
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserStation))
            return false;

        UserStation that = (UserStation) o;

        return deptId.equals(that.deptId) && stationId.equals(that.stationId) && userId.equals(that.userId);
    }

    public int hashCode()
    {
        int result;
        result = userId.hashCode();
        result = 31 * result + deptId.hashCode();
        result = 31 * result + stationId.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return station.toString();
    }
}
