package com.gzzm.platform.organ;

import com.gzzm.platform.commons.BaseConfig;
import net.cyan.arachne.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用于设置角色作用范围的部门树
 *
 * @author camel
 * @date 2010-2-4
 */
public class ScopeDeptTreeModel
        implements CheckBoxTreeModel<ScopeDeptTreeModel.Item>, SearchablePageTreeModel<ScopeDeptTreeModel.Item>
{
    public static class Item
    {
        private Integer id;

        private String name;

        private DeptInfo deptInfo;

        public Item(Integer id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public Item(DeptInfo deptInfo)
        {
            this.id = deptInfo.getDeptId();
            this.name = deptInfo.getDeptName();
            this.deptInfo = deptInfo;
        }

        public Integer getId()
        {
            return id;
        }

        public String getName()
        {
            return name;
        }
    }

    @Inject
    private DeptService service;

    @Inject
    private BaseConfig config;

    private DeptInfo rootDept;

    private Collection<Integer> ids;

    public ScopeDeptTreeModel()
    {
    }

    public void setIds(Collection<Integer> ids)
    {
        this.ids = ids;
    }

    private DeptInfo getRootDept() throws Exception
    {
        if (rootDept == null)
            rootDept = service.getRoot();

        return rootDept;
    }

    public boolean hasCheckBox(Item item) throws Exception
    {
        return item.getId() != 0;
    }

    public Boolean isChecked(Item item) throws Exception
    {
        return ids != null && ids.contains(item.getId());
    }

    public Item getRoot() throws Exception
    {
        return new Item(0, "");
    }

    public boolean isLeaf(Item item) throws Exception
    {
        return item.deptInfo != null ? item.deptInfo.subDepts().size() == 0 : item.getId() != 0;
    }

    public int getChildCount(Item parent) throws Exception
    {
        if (parent.deptInfo != null)
            return parent.deptInfo.subDepts().size();
        else if (parent.id == 0)
            return config.getDeptLevels().size() + 2;
        else
            return 0;
    }

    public Item getChild(Item parent, int index) throws Exception
    {
        if (parent.deptInfo == null)
        {
            List<String> levels = config.getDeptLevels();
            if (index == 0)
                return new Item(getRootDept());
            else if (index == 1)
                return new Item(-100, "当前业务部门");
            else
                return new Item(-index + 1, "当前" + levels.get(index - 2));
        }
        else
        {
            return new Item(parent.deptInfo.subDepts().get(index));
        }
    }

    public String getId(Item item) throws Exception
    {
        return item.getId().toString();
    }

    public String toString(Item item) throws Exception
    {
        return item.getName();
    }

    public Item getNode(String id) throws Exception
    {
        return new Item(service.getDept(Integer.parseInt(id)));
    }

    public Boolean isRootVisible()
    {
        return null;
    }

    public boolean isSearchable() throws Exception
    {
        return true;
    }

    public Collection<Item> search(String text) throws Exception
    {
        List<? extends DeptInfo> deptInfos = service.searchDept(text, null);

        if (deptInfos != null)
        {
            List<Item> result = new ArrayList<Item>();

            for (DeptInfo deptInfo : deptInfos)
            {
                result.add(new Item(deptInfo));
            }

            return result;
        }

        return Collections.emptyList();
    }

    public Item getParent(Item item) throws Exception
    {
        if (item.deptInfo == null)
            return null;

        DeptInfo parentDeptInfo = item.deptInfo.parentDept();

        if (parentDeptInfo == null)
            return getRoot();
        else
            return new Item(parentDeptInfo);
    }
}
