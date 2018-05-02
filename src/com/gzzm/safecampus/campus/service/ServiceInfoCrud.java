package com.gzzm.safecampus.campus.service;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 服务管理
 * @author zy
 * @date 2018/3/12 17:29
 */
@Service(url = "/campus/service/serviceinfocrud")
public class ServiceInfoCrud extends BaseNormalCrud<ServiceInfo,Integer>
{
    @Inject
    private ServiceDao dao;

    /**
     * 服务名称
     */
    @Like
    private String serviceName;

    /**
     * 所属类型
     */
    private Integer typeId;

    /**
     * 服务类型
     */
    private List<ServiceType> serviceTypes;

    /**
     * 包含权限
     */
    private List<Integer> roleIds;

    /**
     * 权限角色树
     */
    private RoleTreeModel roleTree;

    public ServiceInfoCrud()
    {
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public List<Integer> getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds)
    {
        this.roleIds = roleIds;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Select(field = {"typeId","entity.typeId"},text = "typeName",value = "typeId")
    public List<ServiceType> getServiceTypes() throws Exception
    {
        if(serviceTypes==null)
            serviceTypes=dao.getServiceTypeList();
        return serviceTypes;
    }

    @Select(field = "roleIds")
    public RoleTreeModel getRoleTree() throws Exception
    {
        if(roleTree==null)
            roleTree= Tools.getBean(RoleTreeModel.class);
        roleTree.setShowBox(true);
        roleTree.setRoleIds(roleIds);
        return roleTree;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view=new PageTableView();
        view.addComponent("服务名称","serviceName");
        view.addComponent("服务类型","typeId");
        view.addColumn("服务名称","serviceName");
        view.addColumn("服务类型","type.typeName");
        view.addColumn("包含权限","roles");
        view.addDefaultButtons();
        view.makeEditable();
        return view;
    }

    @Override
    public String show(Integer key, String forward) throws Exception
    {
        ServiceInfo serviceInfo=getEntity(key);
        if(serviceInfo!=null&&serviceInfo.getRoles()!=null)
        {
            roleIds=new ArrayList<>();
            for(Role role:serviceInfo.getRoles())
            {
                roleIds.add(role.getRoleId());
            }
        }
        return super.show(key, forward);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view=new SimpleDialogView();
        view.addComponent("服务名称","serviceName");
        view.addComponent("服务类型","typeId");
        view.addComponent("包含权限","this.roleIds").setProperty("text","${roles!=null?roles:''}");
        view.addComponent("服务说明",new CTextArea("remark"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        ServiceInfo entity=getEntity();
        if(roleIds!=null&&roleIds.size()>0)
        {
            List<Role> roles=new ArrayList<>();
            for (Integer roleId:roleIds)
            {
                Role role=new Role();
                role.setRoleId(roleId);
                roles.add(role);
            }
            entity.setRoles(roles);
        }
        setEntity(entity);
        return super.beforeSave();
    }
}
