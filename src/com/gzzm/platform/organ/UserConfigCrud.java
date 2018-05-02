package com.gzzm.platform.organ;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 所有用户配置crud的超类，实现展示部门及用户的功能
 *
 * @author camel
 * @date 2010-4-10
 */
@Service
public abstract class UserConfigCrud extends DeptOwnedNormalCrud<User, Integer>
{
    /**
     * 用户名查询条件
     */
    @Like
    private String userName;

    /**
     * 是否包括下级部门的用户
     */
    private boolean all;

    @Inject
    protected DeptService deptService;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    /**
     * 批量修改时选择的用户ID，由于原来的keys设置了不序列化，用此实现userIds的传递
     *
     * @see #batchShow(String) ()
     */
    private Integer[] userIds;

    public UserConfigCrud()
    {
        setLog(false);
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public boolean isAll()
    {
        return all;
    }

    public void setAll(boolean all)
    {
        this.all = all;
    }

    public Integer[] getUserIds()
    {
        return userIds;
    }

    public void setUserIds(Integer[] userIds)
    {
        this.userIds = userIds;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected String getQueryString() throws Exception
    {
        StringBuilder buffer = new StringBuilder();

        if (all)
        {
            buffer.append("select u.user from (select user,min(select leftValue from user.depts d where d.state=0) " +
                    "as leftValue from User user where userId in (select d.userId from UserDept d " +
                    "join Dept d1 on d.dept.leftValue>=d1.leftValue and d.dept.leftValue<d1.rightValue " +
                    "where d.deptId in ?authDeptIds and d1.deptId=:deptId and d.dept.state=0)");

            String condition = getCondition();

            if (condition != null)
                buffer.append(" and (").append(condition).append(")");

            buffer.append(") u order by u.leftValue,first(select orderId from UserDept d " +
                    "where d.userId=u.user.userId and d.dept.leftValue=u.leftValue)");
        }
        else
        {
            buffer.append("select user from UserDept where deptId=:deptId");

            String condition = getCondition();
            if (condition != null)
                buffer.append(" and (").append(condition).append(")");

            buffer.append(" order by orderId");
        }


        return buffer.toString();
    }

    protected String getCondition() throws Exception
    {
        return "user.userName like ?userName";
    }

    protected void checkDept(Integer deptId, Integer userId) throws Exception
    {
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        if (!userOnlineInfo.isAdmin())
        {
            Collection<Integer> authDeptIds = getAuthDeptIds();

            if (authDeptIds != null && !authDeptIds.contains(deptId))
            {
                if (userId != null)
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth," + ",userId:" + userId + ",deptId:" + deptId);
                else if (getKeys() != null)
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth," + ",userIds:" + Arrays.toString(getKeys()) + ",deptId:" + deptId);
                else
                    throw new SystemMessageException(Messages.NO_AUTH_RECORD,
                            "no auth,deptId:" + deptId);
            }
        }
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        //检查权限
        checkDept(getDeptId(), getEntity().getUserId());

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        User user = getEntity();

        //检查权限
        checkDept(getDeptId(), getEntity().getUserId());

        if (userOnlineInfo.isAdmin() && user.getAdminUser() == null)
            user.setAdminUser(false);

        super.beforeUpdate();

        return true;
    }

    @Override
    public boolean beforeDelete(Integer userId) throws Exception
    {
        //检查权限
        checkDept(getDeptId(), userId);

        return true;
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        //检查是否有删除的权限
        checkDept(getDeptId(), null);

        return true;
    }

    /**
     * 批量修改时显示的页面
     *
     * @param forward 转向的页面
     * @return 转向修改的页面
     * @throws Exception 允许子类抛出异常
     */
    @Service(method = HttpMethod.post)
    public String batchShow(String forward) throws Exception
    {
        if (forward != null)
            setForward(forward);

        setUserIds(getKeys());

        return SystemCrudUtils.editForward(forward, this);
    }

    /**
     * 批量修改
     *
     * @throws Exception 允许子类抛出异常
     */
    @Service(method = HttpMethod.post)
    public void batchSave() throws Exception
    {
        for (Integer userId : getUserIds())
            save(userId);
    }

    /**
     * 批量保存时保存单个用户的信息
     *
     * @param userId 用户ID
     * @throws Exception 允许子类抛出异常
     */
    protected void save(Integer userId) throws Exception
    {
    }

    @Override
    protected Object createView(String action) throws Exception
    {
        if ("batchShow".equals(action))
            return createBatchShowView();

        return super.createView(action);
    }

    protected Object createBatchShowView() throws Exception
    {
        return null;
    }

    @Override
    public Integer getDeptId(User entity) throws Exception
    {
        List<Dept> depts = entity.getDepts();
        if (depts != null && depts.size() > 0)
        {
            Dept dept = depts.get(0);
            if (dept != null)
                return dept.getDeptId();
        }

        return null;
    }

    @Override
    public Integer getOwnerKey(User entity) throws Exception
    {
        //覆盖掉多拖放的支持
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOwnerKey(User entity, Integer deptId) throws Exception
    {
        //覆盖掉多拖放的支持
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveTo(Integer key, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        //覆盖掉多拖放的支持
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveAllTo(Integer[] keys, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        //覆盖掉多拖放的支持
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyTo(Integer key, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        //覆盖掉多拖放的支持
        super.copyTo(key, newDeptId, oldDeptId);
    }

    @Override
    public void copyAllTo(Integer[] keys, Integer newDeptId, Integer oldDeptId) throws Exception
    {
        //覆盖掉多拖放的支持
        super.copyAllTo(keys, newDeptId, oldDeptId);
    }
}
