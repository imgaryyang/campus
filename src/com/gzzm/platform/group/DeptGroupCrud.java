package com.gzzm.platform.group;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;

import java.util.ArrayList;

/**
 * @author camel
 * @date 11-9-21
 */
@Service(url = "/DeptGroup")
public class DeptGroupCrud extends DeptOwnedNormalCrud<DeptGroup, Integer>
{
    @Like
    private String groupName;

    public DeptGroupCrud()
    {
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        DeptGroup group = getEntity();
        if (group.getDepts() == null)
            group.setDepts(new ArrayList<Dept>(0));

        return true;
    }

    @Override
    @Forward(page = "/platform/group/deptgroup.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/platform/group/deptgroup.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/platform/group/deptgroup.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    @Override
    public DeptGroup clone(DeptGroup entity) throws Exception
    {
        DeptGroup group = super.clone(entity);
        group.setDepts(new ArrayList<Dept>(entity.getDepts()));

        return group;
    }


    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
            view = new PageTableView(true);
        else
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("部门组名称", "groupName");

        view.addColumn("部门组名称", "groupName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    public AuthDeptTreeModel getDeptTree()
    {
        AuthDeptTreeModel deptTree = super.getDeptTree();

        deptTree.setFull(true);
        deptTree.setAppId(Member.DEPT_SELECT_APP);

        return deptTree;
    }
}
