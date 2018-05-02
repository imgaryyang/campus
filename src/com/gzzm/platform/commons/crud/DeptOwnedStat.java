package com.gzzm.platform.commons.crud;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.organ.AuthDeptTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.In;

import java.util.*;

/**
 * 属于部门的数据的统计
 *
 * @author camel
 * @date 11-8-30
 */
@Service
public abstract class DeptOwnedStat<E> extends BaseStatCrud<E> implements DeptOwnedCrud
{
    /**
     * 用部门id作查询条件
     */
    private Integer deptId;

    /**
     * 根级部门的id，表示查询此部门的所有子部门的数据
     */
    private Collection<Integer> topDeptIds;

    /**
     * 拥有权限的部门列表，通过setAuthDeptIds注入，以方便子类覆盖注入方式
     */
    @NotSerialized
    private Collection<Integer> authDeptIds;

    /**
     * 部门id列表，可同时查询多个部门
     */
    private List<Integer> deptIds;

    /**
     * 部门id列表，可同时查询多个部门
     */
    @NotSerialized
    private Collection<Integer> queryDeptIds;

    protected AuthDeptTreeModel deptTree;

    public DeptOwnedStat()
    {
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @AuthDeptIds
    protected void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Collection<Integer> getTopDeptIds()
    {
        return topDeptIds;
    }

    public void setTopDeptIds(Collection<Integer> topDeptId)
    {
        this.topDeptIds = topDeptId;
    }

    public List<Integer> getDeptIds()
    {
        return deptIds;
    }

    public void setDeptIds(List<Integer> deptIds)
    {
        this.deptIds = deptIds;
    }

    @In("deptId")
    public Collection<Integer> getQueryDeptIds()
    {
        return queryDeptIds;
    }

    public void setQueryDeptIds(Collection<Integer> queryDeptIds)
    {
        this.queryDeptIds = queryDeptIds;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    protected Integer getDefaultDeptId() throws Exception
    {
        return DeptOwnedCrudUtils.getDefaultDeptId(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void setDefaultDeptId() throws Exception
    {
        if (getDeptId() == null)
            setDeptId(DeptOwnedCrudUtils.getDefaultDeptId(this));

        setDeptIds(null);
        setQueryDeptIds(null);
    }

    @Select(field = {"deptIds", "topDeptIds"})
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
            deptTree.setShowBox(true);
        }

        return deptTree;
    }
}
