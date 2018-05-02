package com.gzzm.oa.userfile;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 个人文件维护
 * 继承自UserOwnedTreeCrud，以实现用户权限控制，实现只能维护用户自己的文件夹,ccs
 *
 * @author : wmy
 * @date : 2010-3-10
 */
@Service(url = "/oa/userfile/folder")
public class UserFileFolderCrud extends UserOwnedTreeCrud<UserFileFolder, Integer>
{
    @Inject
    private UserFileDao dao;

    private List<UserFileFolderShare> shares;

    public UserFileFolderCrud()
    {
    }

    public List<UserFileFolderShare> getShares()
    {
        return shares;
    }

    public void setShares(List<UserFileFolderShare> shares)
    {
        this.shares = shares;
    }

    @Override
    public UserFileFolder getRoot() throws Exception
    {
        return dao.getRootFolder();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeView view = new PageTreeView();
        view.defaultInit();
        view.addButton(Buttons.edit("share", "共享"));

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
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        if ("share".equals(getForward()))
        {
            setShares(getEntity().getShares());
        }
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if ("share".equals(getForward()))
        {
            //设置文件夹共享时不修改其它字段，重新创建一个对象
            UserFileFolder folder = new UserFileFolder();
            folder.setFolderId(getEntity().getFolderId());

            //设置文件夹共享时，如果把所有人清空了，request中取不到数据，要情空共享人
            if (shares == null)
                folder.setShares(new ArrayList<UserFileFolderShare>(0));
            else
                folder.setShares(shares);

            setEntity(folder);
        }

        return true;
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
