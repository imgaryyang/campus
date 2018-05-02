package com.gzzm.oa.userfile;

import com.gzzm.platform.commons.crud.SelectableTreeView;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.User;
import net.cyan.crud.TreeCrud;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 共享目录左边目录树，第一层节点为提供共享的用户，第二层节点为此用户共享的目录
 *
 * @author : wmy
 * @date : 2010-5-5
 */
public class UserFileShareNodeDisplay extends TreeCrud<UserFileShareNode, String>
{

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 文件操作类
     */
    @Inject
    private UserFileDao fileDao;

    public UserFileShareNodeDisplay()
    {
    }

    @Override
    public UserFileShareNode getNode(String key) throws Exception
    {
        if ("-1".equals(key))
        {
            return UserFileShareNode.ROOT;
        }
        else
        {
            return new UserFileShareNode(key);
        }
    }

    @Override
    public boolean hasChildren(UserFileShareNode node) throws Exception
    {
        return node.isRoot() || node.isUserNode() ||
                fileDao.getShareFolderCountByUserId(userOnlineInfo.getUserId(),
                        Integer.valueOf(node.getNodeId().substring(1)), userOnlineInfo.getDeptIds()) > 0;

    }

    @Override
    public UserFileShareNode getRoot() throws Exception
    {
        return UserFileShareNode.ROOT;
    }

    @Override
    public List<UserFileShareNode> getChildren(String parent) throws Exception
    {
        List<UserFileShareNode> result;

        if ("-1".equals(parent))
        {
            List<User> userList = fileDao.getShareUserListByUserId(
                    userOnlineInfo.getUserId(), userOnlineInfo.getDeptIds());

            result = new ArrayList<UserFileShareNode>(userList.size());

            for (User user : userList)
            {
                // 在ID前加标识符:u代表共享人层级
                UserFileShareNode fileShare = new UserFileShareNode("u" + user.getUserId(), user.getUserName());
                result.add(fileShare);
            }
        }
        else if (parent.startsWith("u"))
        {
            List<UserFileFolder> folderList = fileDao.getRootShareFolderListByUserId(userOnlineInfo.getUserId(),
                    Integer.valueOf(parent.substring(1)), userOnlineInfo.getDeptIds());

            result = new ArrayList<UserFileShareNode>(folderList.size());

            for (UserFileFolder folder : folderList)
            {
                // 在ID前加标识符:f代表目录层级
                UserFileShareNode fileShare = new UserFileShareNode("f" + folder.getFolderId(), folder.getFolderName());
                result.add(fileShare);
            }
        }
        else
        {
            List<UserFileFolder> folderList = fileDao.getShareFolderListByUserId(userOnlineInfo.getUserId(),
                    Integer.valueOf(parent.substring(1)), userOnlineInfo.getDeptIds());

            result = new ArrayList<UserFileShareNode>(folderList.size());

            for (UserFileFolder folder : folderList)
            {
                // 在ID前加标识符:f代表目录层级
                UserFileShareNode fileShare = new UserFileShareNode("f" + folder.getFolderId(), folder.getFolderName());
                result.add(fileShare);
            }
        }

        return result;
    }

    /**
     * 获取节点主键
     */
    public String getKey(UserFileShareNode entity) throws Exception
    {
        return entity.getNodeId();
    }

    @Override
    public String toString(UserFileShareNode node) throws Exception
    {
        return node.getNodeName();
    }

    @Override
    protected Object createView() throws Exception
    {
        SelectableTreeView view = new SelectableTreeView();
        view.setIcon("${icon}");

        return view;
    }
}
