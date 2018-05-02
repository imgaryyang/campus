package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.DeptId;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用户日历视图，显示一系列用户某一周的数据
 *
 * @author camel
 * @date 12-3-5
 */
@Service
public abstract class UserCalendarView<I> extends DeptOwnedCalendarView<I>
{
    @Inject
    protected OrganDao organDao;

    @DeptId
    protected Integer selfDeptId;

    protected List<User> users;

    private String userName;

    public UserCalendarView()
    {
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    protected List<User> loadUsers() throws Exception
    {
        Collection<Integer> deptIds = getQueryDeptIds();
        if (deptIds == null)
            deptIds = Collections.singleton(selfDeptId);

        if (StringUtils.isEmpty(userName))
            return organDao.queryUsersInDepts(deptIds);
        else
            return organDao.queryUsersByName(userName, deptIds, 10000);
    }

    protected List<User> getUsers() throws Exception
    {
        if (users == null)
            users = loadUsers();
        return users;
    }

    protected List<Integer> getUserIds() throws Exception
    {
        //获得所有用户的id，用户查询这些用户的数据
        List<User> users = getUsers();
        List<Integer> userIds = new ArrayList<Integer>(users.size());
        for (User user : users)
            userIds.add(user.getUserId());

        return userIds;
    }

    protected List<CalendarItemList<I>> createList() throws Exception
    {
        List<User> users = getUsers();
        List<CalendarItemList<I>> list = new ArrayList<CalendarItemList<I>>(users.size());
        for (User user : getUsers())
        {
            list.add(new CalendarItemList<I>(user.getUserId(), user.getUserName()));
        }

        return list;
    }

    @Override
    protected PageTableView initView(PageTableView view)
    {
        view.addComponent("姓名", "userName");

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 1)
        {
            view.addComponent("部门", "deptIds");
        }

        view.addButton(Buttons.query());

        super.initView(view);

        return view;
    }
}
