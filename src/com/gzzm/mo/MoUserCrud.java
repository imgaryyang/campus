package com.gzzm.mo;

import com.gzzm.platform.commons.PasswordUtils;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 开动移动办公的用户的管理
 *
 * @author camel
 * @date 2014/5/12
 */
@Service(url = "/mo/user")
public class MoUserCrud extends DeptOwnedEditableCrud<MoUser, Integer>
{
    /**
     * 旧密码，当接收到此密码时表示保留原来的密码
     */
    private static final String OLDPASSWOED = "######$$$$$$";

    @Like("user.userName")
    private String userName;

    @Like
    private String phone;

    private PageUserSelector userSelector;

    @Require
    private String password;

    @Inject
    private MoDao dao;

    @Inject
    private OrganDao organDao;

    public MoUserCrud()
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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
    public Integer getDeptId(MoUser entity) throws Exception
    {
        User user = organDao.getUser(entity.getUserId());

        if (user == null)
            return null;

        Collection<Integer> authDeptIds = getAuthDeptIds();
        List<Dept> depts = user.getDepts();
        for (Dept dept : depts)
        {
            Integer deptId = dept.getDeptId();
            if (authDeptIds == null || authDeptIds.contains(deptId))
                return deptId;
        }

        return depts.get(0).getDeptId();
    }

    @Override
    public List<Integer> getDeptIds(Collection<Integer> keys) throws Exception
    {
        List<Integer> deptIds = new ArrayList<Integer>();
        Collection<Integer> authDeptIds = getAuthDeptIds();
        for (Integer key : keys)
        {
            MoUser moUser = getEntity(key);
            List<Dept> depts = moUser.getUser().getDepts();

            boolean b = false;
            for (Dept dept : depts)
            {
                Integer deptId = dept.getDeptId();
                if (authDeptIds == null || authDeptIds.contains(deptId))
                {
                    deptIds.add(deptId);
                    b = true;
                    break;
                }
            }

            if (!b)
            {
                for (Dept dept : depts)
                {
                    deptIds.add(dept.getDeptId());
                }
            }
        }

        return deptIds;
    }

    @Override
    public void setDeptId(MoUser entity, Integer deptId) throws Exception
    {
    }

    @Override
    public void initEntity(MoUser entity) throws Exception
    {
        if (isSelf())
        {
            entity.setUserId(userOnlineInfo.getUserId());
            entity.setUser(organDao.getUser(entity.getUserId()));
        }
    }

    @Service
    @ObjectResult
    @Transactional
    public void setValid(Integer userId, Boolean valid) throws Exception
    {
        MoUser user = new MoUser();
        user.setUserId(userId);
        user.setValid(valid);

        dao.update(user);
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
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        password = OLDPASSWOED;

        getEntity().setPassword(null);
    }

    @Override
    public void afterInsert() throws Exception
    {
        getEntity().setPassword(PasswordUtils.hashPassword(password, getEntity().getUserId()));

        dao.update(getEntity());
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        if (!StringUtils.isEmpty(password) && !OLDPASSWOED.equals(password))
            getEntity().setPassword(PasswordUtils.hashPassword(password, getEntity().getUserId()));

        return true;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        if (getEntity().getUserId() == null)
            view.addComponent("用户", "userId");
        else
            view.addComponent("用户", "user.userName").setProperty("readOnly", null);

        view.addComponent("手机号码", "phone");
        view.addComponent("密码", new CPassword("this.password"));
        view.addComponent("密码确认", new CPassword(null).setProperty("require", null).
                setProperty("name", "confirmPassword").setProperty("equal", "password")
                .setProperty("value", password));

        view.addDefaultButtons();

        if (isNew$())
            view.importCss("/mo/user.css");

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
        view.addComponent("手机号码", "phone");
        view.addColumn("用户名", "user.userName").setWidth("150");
        view.addColumn("所属部门", "user.allDeptName()").setAutoExpand(true);
        view.addColumn("手机号码", "phone");
        view.addColumn("有效", new CCheckbox("valid").setProperty("onclick", "setValid(${userId},this.checked)"))
                .setWidth("40");

        view.defaultInit(false);

        return view;
    }

    @Override
    public int deleteAll() throws Exception {
        dao.deleteMoBinds(getKeys());
        return super.deleteAll();
    }
}
