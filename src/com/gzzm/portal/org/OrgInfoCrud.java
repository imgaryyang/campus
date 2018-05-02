package com.gzzm.portal.org;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.DeptOwnedNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lk
 * @date 13-9-26
 */
@Service(url = "/portal/org/org")
public class OrgInfoCrud extends DeptOwnedNormalCrud<OrgInfo, Integer>
{
    @Inject
    private OrgInfoDao dao;

    @Like
    private String orgName;

    private Integer typeId;

    private OrgTypeTree typeTree;

    private OrgTypeTree multipleTypeTree;

    @NotCondition
    @Require
    private List<Integer> typeIds;

    public OrgInfoCrud()
    {
        setLog(true);
        addOrderBy("orgId");
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public List<Integer> getTypeIds()
    {
        return typeIds;
    }

    public void setTypeIds(List<Integer> typeIds)
    {
        this.typeIds = typeIds;
    }

    @Select(field = {"entity.typeId", "typeId"})
    public OrgTypeTree getTypeTree()
    {
        if (typeTree == null)
            typeTree = new OrgTypeTree();

        return typeTree;
    }

    @Select(field = "typeIds")
    public OrgTypeTree getMultipleTypeTree()
    {
        if (multipleTypeTree == null)
        {
            multipleTypeTree = new OrgTypeTree();
            multipleTypeTree.setShowBox(true);
        }
        return multipleTypeTree;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (typeIds != null)
            return "typeId in ?typeIds or (exists t in types : t.typeId in ?typeIds)";
        else
            return null;
    }

    @Override
    public void afterLoad() throws Exception
    {
        List<OrgType> types = getEntity().getTypes();

        typeIds = new ArrayList<Integer>(types.size() + 1);

        for (OrgType type : types)
        {
            typeIds.add(type.getTypeId());
        }

        if (getEntity().getTypeId() != null && !typeIds.contains(getEntity().getTypeId()))
        {
            typeIds.add(getEntity().getTypeId());
        }
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        if (typeIds != null)
        {
            if (typeIds.size() > 0)
            {
                getEntity().setTypeId(typeIds.get(0));
            }

            List<OrgType> types = new ArrayList<OrgType>();

            for (Integer typeId : typeIds)
            {
                OrgType type = getCrudService().get(OrgType.class, typeId);
                if(type != null) types.add(type);
            }

            getEntity().setTypes(types);
        }

        return true;
    }

    @Override
    @Forward(page = "/portal/org/org.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/portal/org/org.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/portal/org/org.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("机构名称", "orgName").setWidth("250").setAutoExpand(true);
        view.addColumn("机构类型", "typeName").setWidth("250").setOrderFiled("type.orderId");
        view.addColumn("地址", "address").setWidth("350");
        view.addColumn("主页", "mainPage").setWidth("200");
        view.addColumn("邮编", "postCode").setWidth("100");

        view.addComponent("机构名称", "orgName");
        view.addComponent("机构类型", "typeId");

        view.defaultInit(false);

        if (getAuthDeptIds() == null)
            view.addButton(Buttons.sort());

        view.importJs("/portal/org/org.js");

        return view;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"typeId"};
    }
}
