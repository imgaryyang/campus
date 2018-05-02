package com.gzzm.ods.receivetype;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.KeyValue;
import net.cyan.nest.annotation.Inject;

import java.util.*;


/**
 * 发文字号管理
 *
 * @author fwj
 * @date 2011-6-30
 */
@Service(url = "/ods/ReceiveType")
public class ReceiveTypeCrud extends DeptOwnedTreeCrud<ReceiveType, Integer>
{
    @Inject
    private ReceiveTypeDao dao;

    @Inject
    private DeptService deptService;

    private int type;

    public ReceiveTypeCrud()
    {
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public ReceiveType getRoot() throws Exception
    {
        return dao.getRootReceiveType();
    }

    @NotSerialized
    public String getText() throws Exception
    {
        ReceiveType receiveType = getEntity();
        if (receiveType != null)
        {
            return receiveType.getText();
        }

        return null;
    }

    public AuthDeptTreeModel getDeptTree()
    {
        AuthDeptTreeModel deptTree = super.getDeptTree();

        deptTree.setFull(true);

        return deptTree;
    }

    @NotSerialized
    @Select(field = "entity.serialDeptId")
    public List<KeyValue<String>> getSerialDepts() throws Exception
    {
        List<KeyValue<String>> result = new ArrayList<KeyValue<String>>(2);

        result.add(new KeyValue<String>("0", "业务部门"));
        result.add(new KeyValue<String>(getEntity().getDeptId().toString(),
                deptService.getDept(getEntity().getDeptId()).getDeptName()));

        return result;
    }

    @Override
    @Forward(name = "sourcedepts", page = "/ods/receivetype/sourcedepts.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setType(type);

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        if ("sourcedepts".equals(getForward()))
        {
            if (getEntity().getSourceDepts() == null)
                getEntity().setSourceDepts(new ArrayList<Dept>(0));
        }

        return super.beforeUpdate();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeView view = new PageTreeView();

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 1)
        {
            view.addComponent("选择部门", "deptId").setProperty("onchange", "selectDept()")
                    .setProperty("text", getDeptName());
        }

        view.defaultInit();
        view.addButton(Buttons.edit("sourcedepts", "设置来文单位"));

        view.importJs("/ods/receivetype/receivetype.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addHidden("entity.serial");

        view.addComponent("类型名称", "receiveTypeName");
        view.addComponent("收文编号", "this.text");
        view.addComponent("序号部门", "serialDeptId");

        view.addDefaultButtons();

        view.importJs("/ods/receivetype/receivetype.js");

        return view;
    }
}
