package com.gzzm.platform.group;

import com.gzzm.platform.organ.*;
import net.cyan.commons.util.Value;

import java.util.List;

/**
 * @author camel
 * @date 11-9-21
 */
public class Member implements Value<String>
{
    public static final String DEPT_SELECT_APP = "dept_select";

    public static final String USER_SELECT_APP = "user_select";

    public static final String DEPT_GROUP_SELECT_APP = "dept_group_select";

    public static final String USER_GROUP_SELECT_APP = "user_group_select";

    public static final String STATION_SELECT_APP = "station_select";

    private static final long serialVersionUID = 5152810232894039876L;

    private MemberType type;

    private Integer id;

    private String name;

    /**
     * 成员所属的部门ID
     */
    private Integer deptId;

    private String deptName;

    public Member()
    {
    }

    public Member(MemberType type, Integer id, String name)
    {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public Member(SimpleDeptInfo dept)
    {
        this(MemberType.dept, dept.getDeptId(), dept.getDeptName());
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public Member(User user, Integer deptId)
    {
        this(MemberType.user, user.getUserId(), user.getUserName());

        if (deptId == null)
        {
            List<Dept> depts = user.getDepts();
            for (Dept dept : depts)
            {
                deptId = dept.getDeptId();
                break;
            }
        }

        setDeptId(deptId);
    }

    public Member(DeptGroup deptGroup)
    {
        this(MemberType.deptgroup, deptGroup.getGroupId(), deptGroup.getGroupName());
    }

    public Member(UserGroup userGroup)
    {
        this(MemberType.usergroup, userGroup.getGroupId(), userGroup.getGroupName());
    }

    public Member(Station station)
    {
        this(MemberType.station, station.getStationId(), station.getStationName());
    }

    public MemberType getType()
    {
        return type;
    }

    public void setType(MemberType type)
    {
        this.type = type;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String valueOf()
    {
        return type + ":" + (type == MemberType.custom ? name : id);
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Member member = (Member) o;

        return !(id != null ? !id.equals(member.id) : member.id != null) && type == member.type;
    }

    @Override
    public int hashCode()
    {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
