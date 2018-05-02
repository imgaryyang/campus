package com.gzzm.platform.sign;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;

import java.util.Collection;

/**
 * @author camel
 * @date 2015/6/3
 */
@Service(url = "/sign/user")
public class UserSignCrud extends DeptOwnedEditableCrud<UserSign, Integer>
{
    private PageUserSelector userSelector;

    @Like("user.userName")
    private String userName;

    public UserSignCrud()
    {
        addOrderBy("user.sortId");
    }

    @Select(field = "entity.userId")
    public PageUserSelector getUserSelector()
    {
        if (userSelector == null)
        {
            userSelector = new PageUserSelector();
        }

        return userSelector;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    @NotCondition
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @Override
    @NotCondition
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }


    @Override
    public Integer getDeptId(UserSign entity) throws Exception
    {
        User user = entity.getUser();

        if (user == null)
            return null;

        return user.getDepts().get(0).getDeptId();
    }

    @Override
    public void setDeptId(UserSign entity, Integer deptId) throws Exception
    {
    }

    @Override
    public void initEntity(UserSign entity) throws Exception
    {
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        return true;
    }


    @Service(url = "/sign/user/{userId}/sign")
    public byte[] showSign(Integer userId) throws Exception
    {
        UserSign userSign = getEntity(userId);

        return userSign.getSign();
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (getDeptId() != null)
            return "exists d in user.depts : d.deptId=:deptId";

        if (getQueryDeptIds() != null)
            return "exists d in user.depts : d.deptId in :queryDeptIds";

        return null;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        if (isNew$())
            view.addComponent("用户", "userId");
        else
            view.addComponent("用户", "user.userName").setProperty("readOnly", null);

        view.addComponent("手写签名", new CFile("sign"));

        view.addDefaultButtons();

        if (isNew$())
            view.importCss("/platform/sign/usersign.css");

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("用户名", "userName");

        if (!isSelf())
        {
            Collection<Integer> authDeptIds = getAuthDeptIds();

            if (authDeptIds == null || authDeptIds.size() > 1)
                view.addComponent("部门", "topDeptIds");
        }

        view.addColumn("用户名", "user.userName").setWidth("150");
        view.addColumn("所属部门", "user.firstDeptName()").setAutoExpand(true);
        view.addColumn("手写签名", new CHref("查看").setAction("showSign(${userId})"));

        view.defaultInit(false);

        view.importJs("/platform/sign/usersign.js");

        return view;
    }
}
