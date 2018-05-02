package com.gzzm.platform.commons.crud;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 基于用户的统计
 *
 * @author camel
 * @date 11-8-30
 */
@Service
public class UserStat extends DeptOwnedStat<User>
{
    @Like("userName")
    private String name;

    @Inject
    private OrganDao dao;

    public UserStat()
    {
    }

    public int getPageSize()
    {
        //用户太多，分页统计
        return "exp".equals(getAction()) ? 0 : 30;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @NotSerialized
    @NotCondition
    public String getSpell()
    {
        if (StringUtils.isEmpty(name))
            return null;
        return name + "%";
    }

    @NotCondition
    @Override
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    protected void initStats() throws Exception
    {
        addStat("userId", "u.userId", "null");
        setGroupField("u.userId");

        addOrderBy("u.sortId");
    }

    @Override
    protected String getComplexCondition(boolean total) throws Exception
    {
        StringBuilder buffer = new StringBuilder("u.state=0 and (exists d in u.depts : (d.state=0");

        if (getDeptId() != null)
        {
            buffer.append(" and d.deptId = ?deptId");
        }
        else if (getQueryDeptIds() != null)
        {
            buffer.append(" and d.deptId in ?queryDeptIds");
        }

        buffer.append("))");

        return buffer.toString();
    }

    @Override
    public String getAlias()
    {
        return "u";
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();

        loadUsers();
    }

    private void loadUsers() throws Exception
    {
        List<Map<String, Object>> list = getList();

        List<Integer> userIds = new ArrayList<Integer>(list.size());
        for (Map<String, Object> map : list)
        {
            Number userId = (Number) map.get("userId");
            if (userId != null)
                userIds.add(userId.intValue());
        }

        List<User> users = dao.getUsersWithDepts(userIds);

        for (Map<String, Object> map : list)
        {
            Number userId = (Number) map.get("userId");

            if (userId != null)
            {
                int userId1 = userId.intValue();
                for (User user : users)
                {
                    if (user.getUserId() == userId1)
                    {
                        map.put("u", user);
                    }
                }
            }
            else
            {
                map.put("u", null);
            }
        }
    }
}
