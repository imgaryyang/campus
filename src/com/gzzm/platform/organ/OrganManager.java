package com.gzzm.platform.organ;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.log.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 组织机构管理
 *
 * @author camel
 * @date 2009-7-18
 */
public class OrganManager
{
    /**
     * 当前用户登录信息，由框架注入
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private OrganDao dao;

    /**
     * 日志
     */
    @Inject
    private LogDao logDao;

    public OrganManager()
    {
    }

    /**
     * 删除用户
     *
     * @param userId 用户id
     * @param deptId 部门id
     * @return 用户是否彻底删除
     * @throws Exception 数据库操作异常
     */
    public boolean deleteUser(Integer userId, Integer deptId) throws Exception
    {
        boolean b = dao.deleteUser(userId, deptId);

        OperationLog log = new OperationLog();
        log.setType(User.class.getName());
        log.setTarget(userId.toString());
        log.setTargetName(dao.getUserName(userId));
        log.setLogAction(LogAction.delete);
        log.setRemark(Tools.getMessage("crud.removelog", dao.getDept(deptId).allName()));

        log(log);

        return b;
    }

    /**
     * 将一个用户从一个部门移到另外一个部门
     *
     * @param userId       用户id
     * @param sourceDeptId 源部门id
     * @param targetDeptId 目标部门id
     * @param sourceRemain 源部门的信息是否保留
     * @return 用户原来是否已经存在目标部门，如果是返回true，不是返回false
     * @throws Exception 数据库异常
     */
    public boolean moveUser(Integer userId, Integer sourceDeptId, Integer targetDeptId, boolean sourceRemain)
            throws Exception
    {
        boolean b = dao.moveUser(userId, sourceDeptId, targetDeptId, sourceRemain);

        if (!b)
        {
            //用户在目标部门不存在，记录日志
            OperationLog log = new OperationLog();
            log.setType(User.class.getName());
            log.setTarget(userId.toString());
            log.setTargetName(dao.getUserName(userId));
            log.setLogAction(LogAction.modify);
            if (sourceRemain)
            {
                log.setRemark(Tools.getMessage("crud.appendlog", new Object[]{dao.getDept(targetDeptId).allName()}));
            }
            else
            {
                log.setRemark(Tools.getMessage("crud.movelog", dao.getDept(sourceDeptId).allName(),
                        dao.getDept(targetDeptId).allName()));
            }

            log(log);
        }

        return b;
    }

    public boolean setUserDepts(Integer userId, Integer... deptIds) throws Exception
    {
        List<Integer> oldDeptIds = dao.getDeptIdsByUserId(userId);

        boolean b = dao.setUserDepts(userId, deptIds);

        if (b)
        {
            //部门发生了变化，记录日志
            OperationLog log = new OperationLog();
            log.setType(User.class.getName());
            log.setTarget(userId.toString());
            log.setTargetName(dao.getUserName(userId));
            log.setLogAction(LogAction.modify);

            StringBuilder buffer = new StringBuilder();
            for (Integer deptId : oldDeptIds)
            {
                if (buffer.length() > 0)
                    buffer.append(",");
                buffer.append(dao.getDept(deptId).allName());
            }

            log.setRemark(Tools.getMessage("修改了所属部门,原来属于:{0}", new Object[]{buffer.toString()}));

            log(log);
        }

        return b;
    }

    /**
     * 将某个权限赋权给多个用户
     *
     * @param roleId  权限ID
     * @param userIds 用户ID
     * @throws Exception 数据库读写错误
     */
    @Transactional
    public void addRoleToUser(Integer roleId, Integer... userIds) throws Exception
    {
        for (Integer userId : userIds)
        {
            User user = dao.getUser(userId);

            for (Dept dept : user.getDepts())
            {
                UserRole userRole = new UserRole(userId, dept.getDeptId(), roleId);

                dao.save(userRole);
            }
        }
    }

    private void log(OperationLog log)
    {
        try
        {
            log.fill(userOnlineInfo);
            logDao.log(log);
        }
        catch (Exception ex)
        {
            //写日志错误不影响操作的完成，只记录异常
            Tools.log(ex);
        }
    }

    public OrganDao getDao()
    {
        return dao;
    }
}