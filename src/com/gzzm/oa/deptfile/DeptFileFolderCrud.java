package com.gzzm.oa.deptfile;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 个人文件维护
 * 继承自DeptOwnedTreeCrud，以实现用户权限控制，实现只能维护部门自己的文件夹,ccs
 *
 * @author : ccs
 * @date : 2013-11-27
 */
@Service(url = "/oa/deptfile/folder")
public class DeptFileFolderCrud extends DeptOwnedTreeCrud<DeptFileFolder, Integer>
{
    @Inject
    private DeptFileDao dao;

    public DeptFileFolderCrud()
    {
    }

    @Override
    public DeptFileFolder getRoot() throws Exception
    {
        return dao.getRootFolder();
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

        view.importJs("/oa/deptfile/folder.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("目录名称", "folderName");

        view.addDefaultButtons();

        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreateTime(new Date());
        return true;
    }

    @Override
    public int getOrderFieldLength()
    {
        return 6;
    }

    @Override
    public boolean supportSearch() throws Exception
    {
        return true;
    }
}
