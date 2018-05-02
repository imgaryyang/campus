package com.gzzm.platform.organ;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.TreeListCrud;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * rolescope的crud操作
 *
 * @author camel
 * @date 2010-2-4
 */
@Service(url = "/RoleScope")
public class RoleScopeCrud extends DeptOwnedNormalCrud<RoleScope, Integer> implements TreeListCrud<RoleScope, Integer>
{
    @Like("scopeName")
    private String name;

    @Inject
    private static Provider<ScopeDeptTreeModel> deptTreeModelProvider;

    @NotSerialized
    private ScopeDeptTreeModel scopeDeptTree;

    private Integer parentScopeId;

    @Inject
    private OrganDao dao;

    private Integer scopeId;

    public RoleScopeCrud()
    {
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getParentScopeId()
    {
        return parentScopeId;
    }

    public void setParentScopeId(Integer parentScopeId)
    {
        this.parentScopeId = parentScopeId;
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        setScopeId(getEntity().getScopeId());
        setDeptId(getEntity().getDeptId());
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (Null.isNull(parentScopeId))
            return "parentScopeId is null";

        return null;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        if ("dept".equals(getForward()))
            return new String[]{"deptId"};
        else if ("parent".equals(getForward()))
            return new String[]{"parentScopeId"};

        return super.getOrderWithFields();
    }

    @Override
    protected String getSortListCondition() throws Exception
    {
        if ("dept".equals(getForward()))
            return super.getSortListCondition() + " and parentScopeId is null ";
        else
            return super.getSortListCondition();
    }

    @Override
    @Forwards({
            @Forward(page = "/platform/organ/scope.ptl"),
            @Forward(name = "catalog", page = "/platform/organ/scopecatalog.ptl")
    })
    public String add(String forward) throws Exception
    {
        super.add(forward);

        RoleScopeType type;
        if ("catalog".equals(forward))
            type = RoleScopeType.catalog;
        else
            type = RoleScopeType.scope;

        getEntity().setType(type);

        return forward;
    }

    @Override
    @Forwards({
            @Forward(page = "/platform/organ/scope.ptl"),
            @Forward(name = "catalog", page = "/platform/organ/scopecatalog.ptl")
    })
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);

        return forward();
    }

    @Override
    @Forwards({
            @Forward(page = "/platform/organ/scope.ptl"),
            @Forward(name = "catalog", page = "/platform/organ/scopecatalog.ptl")
    })
    public String duplicate(Integer key, String forward) throws Exception
    {
        super.duplicate(key, forward);

        return forward();
    }

    @Override
    @Forwards({
            @Forward(page = Pages.SORT),
            @Forward(name = "dept", page = Pages.SORT),
            @Forward(name = "parent", page = Pages.SORT)
    })
    public String showSortList(String forward) throws Exception
    {
        return super.showSortList(forward);
    }

    private String forward()
    {
        RoleScopeType type = getEntity().getType();

        if (type == RoleScopeType.catalog)
            return "catalog";

        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public RoleScope clone(RoleScope scope) throws Exception
    {
        RoleScope c = super.clone(scope);

        if (scope.getType() == RoleScopeType.scope)
        {
            //复制部门列表
            List<RoleScopeDept> scopeDepts = scope.getRoleScopeDepts();
            List<RoleScopeDept> scopeDepts1 = new ArrayList<RoleScopeDept>(scopeDepts.size());
            for (RoleScopeDept scopeDept : scopeDepts)
            {
                scopeDepts1.add(new RoleScopeDept(scopeDept.getDeptId(), scopeDept.isIncludeSub(),
                        scopeDept.isIncludeSup(),
                        scopeDept.getFilter()));
            }

            c.setRoleScopeDepts(scopeDepts1);
        }

        return c;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("名称", "name");

        view.addColumn("名称", "scopeName");
        view.addColumn("所属部门", "dept.deptName");
        view.addColumn("类型", "type");

        view.addButton(Buttons.query());
        view.addButton(Buttons.add(null, "新增范围"));
        view.addButton(Buttons.setId(Buttons.add("catalog", "新增目录"), "btn_add_catalog"));
        view.addButton(Buttons.delete());
        view.addButton(Buttons.sort("dept", "部门中排序").setProperty("id", "sort_dept"));
        view.addButton(Buttons.sort("parent", "目录中排序").setProperty("id", "sort_parent"));
        view.addButton(Buttons.export("xls"));

        view.makeEditable(null);
        view.addDuplicateColumn(null);
        view.enableDD();

        view.importJs("/platform/organ/scope_list.js");

        return view;
    }

    public ScopeDeptTreeModel getScopeDeptTree()
    {
        if (scopeDeptTree == null)
        {
            scopeDeptTree = deptTreeModelProvider.get();

            RoleScope role = getEntity();
            List<RoleScopeDept> scopeDepts = role.getRoleScopeDepts();
            if (scopeDepts != null)
            {
                List<Integer> deptIds = new ArrayList<Integer>(scopeDepts.size());
                for (RoleScopeDept scopeDept : scopeDepts)
                    deptIds.add(scopeDept.getDeptId());
                scopeDeptTree.setIds(deptIds);
            }
        }
        return scopeDeptTree;
    }

    @Override
    public String getParentField()
    {
        return "parentScopeId";
    }

    @Override
    public boolean hasChildren(RoleScope entity)
    {
        return entity.getType() == RoleScopeType.catalog && entity.getChildren().size() > 0;
    }

    @Select(field = "entity.parentScopeId")
    public RoleScopeCatalogTree getCatalogTree()
    {
        RoleScopeCatalogTree catalogTree = new RoleScopeCatalogTree();

        catalogTree.setDao(dao);
        catalogTree.setDeptId(getDeptId());
        catalogTree.setScopeId(getScopeId());

        return catalogTree;
    }
}
