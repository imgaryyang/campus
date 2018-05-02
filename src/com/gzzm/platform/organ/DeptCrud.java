package com.gzzm.platform.organ;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.view.EnumCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 部门维护
 *
 * @author camel
 * @date 2009-12-14
 */
@Service(url = "/DeptCrud")
public class DeptCrud extends BaseTreeCrud<Dept, Integer>
{
    public static final EntityTreeOrganizer ORGANIZER =
            new NestedSetTreeOrganizer("leftValue", "rightValue", "orderId");

    @Inject
    private BaseConfig config;

    /**
     * @noinspection MismatchedQueryAndUpdateOfCollection
     */
    @Inject
    private List<DeptAttributeItem> attributeItems;

    @BureauId
    private Integer bureauId;

    @Inject
    private OrganDao dao;

    @Inject
    private DeptService service;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 只允许排序
     */
    private boolean sort;

    public DeptCrud()
    {
        setLog(true);
        setOrganizer(ORGANIZER);
    }

    public boolean isSort()
    {
        return sort;
    }

    public void setSort(boolean sort)
    {
        this.sort = sort;
    }

    public Dept getRoot() throws Exception
    {
        if (authDeptIds == null)
            return dao.getDept(bureauId);

        List<SimpleDeptInfo> depts = service.getAuthDeptTree(null, null, authDeptIds);
        if (depts.size() == 0)
            throw new SystemMessageException(Messages.NO_AUTH, "no auth");

        return dao.getDept(depts.get(0).getDeptId());
    }

    @Override
    public void setString(Dept dept, String deptName) throws Exception
    {
        dept.setDeptName(deptName);
    }

    @Override
    public String getDeleteTagField()
    {
        return "state";
    }

    @Override
    public void initEntity(Dept entity) throws Exception
    {
        super.initEntity(entity);

        if (entity.getDeptLevel() == null)
            entity.setDeptLevel((byte) 0);
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        if (authDeptIds != null)
            checkAuth(getEntity().getParentDept(), getEntity().getDeptId());
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        //检查是否有权限添加部门，只允许添加拥有权限的部门的子部门
        if (authDeptIds != null)
            checkAuth(getEntity().getParentDeptId());

        getEntity().setState((byte) 0);

        super.beforeInsert();

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        //检查是否有权限修改部门，只允许修改拥有权限的部门的子部门
        if (authDeptIds != null)
            checkAuth(getEntity().getParentDeptId());

        super.beforeUpdate();

        return true;
    }

    @Override
    public boolean beforeDelete(Integer deptId) throws Exception
    {
        //检查是否有权限删除部门，只允许删除拥有权限的部门的子部门
        if (authDeptIds != null)
            checkAuth(service.getDept(deptId), deptId);

        super.beforeDelete(deptId);

        return true;
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        if (authDeptIds != null)
        {
            //检查是否有权限删除部门，只允许删除拥有权限的部门的子部门
            for (Integer deptId : getKeys())
                checkAuth(service.getDept(deptId), deptId);
        }

        super.beforeDeleteAll();

        return true;
    }

    /**
     * 检查是否有权限操作此部门
     *
     * @param deptId 部门id
     * @throws Exception 如果没有权限抛出异常
     */
    private void checkAuth(Integer deptId) throws Exception
    {
        if (!authDeptIds.contains(deptId))
            checkAuth(service.getDept(deptId).parentDept(), deptId);
    }

    private void checkAuth(DeptInfo dept) throws Exception
    {
        checkAuth(dept, dept.getDeptId());
    }

    private void checkAuth(DeptInfo dept, Integer deptId) throws Exception
    {
        if (dept == null)
            throw new SystemMessageException(Messages.NO_AUTH_RECORD, "no auth,deptId:" + deptId);

        if (!authDeptIds.contains(dept.getDeptId()))
            checkAuth(dept.parentDept(), deptId);
    }

    @Override
    public void afterChange() throws Exception
    {
        super.afterUpdate();
        Dept.setUpdated();
    }

    @NotSerialized
    @Select(field = "entity.deptLevel")
    public Map<String, String> getLevels() throws Exception
    {
        return config.getDeptLevelMapWithEmpty();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        if ("exp".equals(getAction()))
        {
            PageTreeTableView view = new PageTreeTableView();

            view.setRootVisible(true);
            view.addColumn("部门名称", "deptName");
            view.addColumn("说明", "remark");
            view.addColumn("部门级别", new EnumCell("deptLevel", getLevels()));
            return view;
        }
        else
        {
            PageTreeView view = new PageTreeView();

            view.addComponent("部门名称", "text");
            view.addButton(Buttons.query());

            if (sort)
            {
                view.addButton(Buttons.up());
                view.addButton(Buttons.down());
                view.addButton(Buttons.export("xls"));
            }
            else
            {
                view.defaultInit();
                view.addButton(Buttons.export("xls"));

                if (userOnlineInfo.isAdmin())
                {
                    view.addButton(Buttons.imp());
                    view.addButton("初始化", "initTree();");
                }
            }

            view.importJs("/platform/organ/dept.js");

            return view;
        }
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("部门名称", "deptName");
        if (config.getDeptLevels() != null)
            view.addComponent("部门级别", "deptLevel");
        view.addComponent("部门简称", "shortName");
        view.addComponent("部门编码", "deptCode");
        view.addComponent("部门简码", "shortCode");
        view.addComponent("机构代码", "orgCode");
        view.addComponent("行政区划代码", "divisionCode");
        view.addComponent("部门说明", new CTextArea("remark"));
        view.addComponent("办公电话", "phone");
        view.addComponent("外部ID", "linkId");


        if (attributeItems != null)
        {
            Map<String, Object> args = null;
            for (DeptAttributeItem item : attributeItems)
            {
                if (args == null)
                {
                    if (getEntity().getDeptId() == null)
                        getEntity().setDeptId(0);
                    if (getEntity().getParentDeptId() == null)
                        getEntity().setParentDeptId(0);
                    args = new CollectionUtils.ObjectMap(getEntity());
                }

                String selectableSql = item.getSelectableSql();
                if (StringUtils.isEmpty(selectableSql))
                {
                    view.addComponent(item.getLabel(), "attributes." + item.getName());
                }
                else
                {
                    view.addComponent(item.getLabel(), new CCombox("attributes." + item.getName(),
                            getCrudService().sqlQuery(selectableSql, Object[].class, args)));
                }
            }

            if (getEntity().getDeptId() == 0)
                getEntity().setDeptId(null);
            if (getEntity().getParentDeptId() == 0)
                getEntity().setParentDeptId(null);
        }

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("部门列表");
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "deptName like :searchText or spell like :searchText or simpleSpell like :searchText";
    }

    @Service
    @Transactional
    public void initTree() throws Exception
    {
        super.initTree(1);
        Dept.setUpdated();
    }

    @Override
    protected void initImportor(CrudEntityImportor<Dept, Integer> importor) throws Exception
    {
        super.initImportor(importor);

        importor.addColumnMap("机构名称", "deptName");
        importor.addColumnMap("简称", "shortName");
    }
}
