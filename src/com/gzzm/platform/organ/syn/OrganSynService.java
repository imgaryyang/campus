package com.gzzm.platform.organ.syn;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.CrudAdvice;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 组织机构同步服务
 *
 * @author camel
 * @date 2011-4-19
 */
@SuppressWarnings("UnusedDeclaration")
public class OrganSynService
{
    @Inject
    private OrganSynDao dao;

    @Inject
    private DeptCrud deptCrud;

    //保存旧的部门id和新的部门id的映射关系，避免重复查询
    private Map<String, Dept> deptMap;

    public OrganSynService()
    {
    }

    public OrganSynDao getDao()
    {
        return dao;
    }

    private Dept getDept(String sourceId) throws Exception
    {
        if (deptMap == null)
            deptMap = new HashMap<String, Dept>();

        Dept dept = deptMap.get(sourceId);
        if (dept == null)
            deptMap.put(sourceId, dept = dao.getDeptWithSourceId(sourceId));

        return dept;
    }

    /**
     * 同步部门
     *
     * @param synDepts 要同步的部门列表
     * @throws Exception 同步错误，一般由数据库错误引起
     */
    @Transactional
    public void synDepts(List<SynDeptInfo> synDepts) throws Exception
    {
        Set<String> syneds = new HashSet<String>();
        for (SynDeptInfo synDept : synDepts)
        {
            synDept(synDept, synDepts, syneds);
        }

        initDeptTree();
    }

    @Transactional
    public void initDeptTree() throws Exception
    {
        try
        {
            CrudAdvice.before(deptCrud);

            deptCrud.initTree(1);

            CrudAdvice.after(deptCrud, null);
        }
        catch (Throwable ex)
        {
            CrudAdvice.catchHandle(deptCrud, ex);

            Tools.handleException(ex);
        }
        finally
        {
            CrudAdvice.finallyHandle(deptCrud);
        }

        Dept.setUpdated();
    }

    private void synDept(SynDeptInfo synDept, List<SynDeptInfo> synDepts, Set<String> syneds) throws Exception
    {
        if (syneds.contains(synDept.getDeptId()))
            return;

        Tools.log("syn dept:" + synDept.getDeptId());

        syneds.add(synDept.getDeptId());

        String sourceId = synDept.getDeptId();

        String sourceParentId = synDept.getParentDeptId();

        Dept dept = getDept(sourceId);

        if (!synDept.isDeleted() || dept != null)
        {
            Integer parentDeptId;
            if (sourceParentId != null)
            {
                for (SynDeptInfo synDept1 : synDepts)
                {
                    if (synDept1.getDeptId().equals(sourceParentId))
                        synDept(synDept1, synDepts, syneds);
                }

                Dept parentDept = getDept(sourceParentId);
                if (parentDept == null)
                    return;
                parentDeptId = parentDept.getDeptId();

                if (parentDept.getState() != null && parentDept.getState() == 1)
                {
                    if (dept == null)
                        return;
                    else
                        synDept.setDeleted(true);
                }
            }
            else
            {
                parentDeptId = 1;
            }

            if (dept == null)
                dept = new Dept();

            if (synDept.isDeleted())
            {
                //删除部门
                dept.setState((byte) 1);
                dao.update(dept);
            }
            else
            {
                dept.setParentDeptId(parentDeptId);
                dept.setDeptName(synDept.getDeptName());
                dept.setDeptCode(synDept.getDeptCode());
                dept.setShortCode(synDept.getShortCode());
                dept.setShortName(synDept.getShortName());
                dept.setDeptLevel(synDept.getLevel());
                dept.setOrderId(synDept.getDeptSort());
                dept.setState((byte) 0);

                if (dept.getDeptId() == null)
                {
                    //新增部门
                    dept.setSourceId(sourceId);
                    dao.add(dept);

                    if (deptMap == null)
                        deptMap = new HashMap<String, Dept>();
                    deptMap.put(sourceId, dept);
                }
                else
                {
                    //更新部门
                    dao.update(dept);
                }
            }
        }
    }

    /**
     * 同步用户
     *
     * @param synUsers 要同步的用户列表
     * @throws Exception 同步错误，一般由数据库错误引起
     */
    public void synUsers(List<SynUserInfo> synUsers) throws Exception
    {
        for (SynUserInfo synUser : synUsers)
        {
            synUser(synUser);
        }
    }

    /**
     * 同步单个用户
     *
     * @param synUser 要同步的用户
     * @throws Exception 同步错误，一般由数据库错误引起
     */
    @Transactional
    public void synUser(SynUserInfo synUser) throws Exception
    {
        Tools.log("syn user:" + synUser.getUserId());

        String sourceId = synUser.getUserId();

        Integer userId = dao.getUserIdWithSourceId(sourceId);

        if (synUser.getState() == 2)
        {
            //删除用户
            if (userId != null)
                dao.deleteUser(userId);
        }
        else
        {
            User user = new User();
            user.setUserId(userId);
            user.setUserName(synUser.getUserName());
            user.setLoginName(synUser.getLoginName());
            user.setPassword(synUser.getPassword());
            user.setSex(synUser.getSex());
            user.setPhone(synUser.getPhone());
            user.setOfficePhone(synUser.getOfficePhone());
            user.setCertType(synUser.getCertType());
            user.setCertId(synUser.getCertId());
            user.setIdCardType(synUser.getIdCardType());
            user.setIdCardNo(synUser.getIdCardNo());
            user.setSourceMail(synUser.getMail());
            user.setState((byte) synUser.getState());
            user.setWorkday(synUser.getWorkday());

            if (userId == null)
            {
                //新增用户
                user.setSourceId(sourceId);
                dao.add(user);

                userId = user.getUserId();
            }
            else
            {
                dao.update(user);
            }

            List<SynUserDeptInfo> synUserDepts = synUser.getDepts();
            if (synUserDepts != null)
            {
                int n = synUserDepts.size();
                Integer[] deptIds = new Integer[n];
                for (int i = 0; i < n; i++)
                {
                    SynUserDeptInfo synUserDept = synUserDepts.get(i);
                    Dept dept = getDept(synUserDept.getDeptId());

//                    if (deptId == null)
//                        throw new SystemException("dept " + synUserDept.getDeptId() + " does not exist");

                    if ("".equals(synUserDept.getDeptId()))
                    {
                        deptIds[i] = 1;
                    }
                    else
                    {
                        deptIds[i] = dept == null || dept.getState() != null && dept.getState() > 0 ?
                                null : dept.getDeptId();
                    }
                }

                dao.setUserDepts(userId, deptIds);

                for (int i = 0; i < n; i++)
                {
                    Integer deptId = deptIds[i];

                    if (deptId != null)
                    {
                        UserDept userDept = dao.getUserDept(userId, deptId);

                        SynUserDeptInfo synUserDept = synUserDepts.get(i);

                        userDept.setOrderId(synUserDept.getSort());

                        if (synUserDept.getRoleIds() != null)
                        {
                            for (Integer roleId : synUserDept.getRoleIds())
                            {
                                UserRole userRole = new UserRole(userId, deptId, roleId);
                                dao.save(userRole);
                            }
                        }

                        if (synUserDept.getStationIds() != null)
                        {
                            for (Integer stationId : synUserDept.getStationIds())
                            {
                                UserStation userStation = new UserStation(userId, deptId, stationId);
                                try
                                {
                                    dao.save(userStation);
                                }
                                catch (Exception ex)
                                {
                                    throw new SystemException("add station to user fail,userId:" + userId +
                                            ",stationId:" + stationId, ex);
                                }
                            }
                        }

                        dao.update(userDept);
                    }
                }
            }
        }
    }
}