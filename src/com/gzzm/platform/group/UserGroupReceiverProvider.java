package com.gzzm.platform.group;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.receiver.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 从用户组中选择用户
 *
 * @author camel
 * @date 2010-8-26
 */
public class UserGroupReceiverProvider implements ReceiverProvider
{
    @Inject
    private static Provider<GroupDao> daoProvider;

    @Inject
    private static Provider<DeptService> deptServiceProvider;

    public UserGroupReceiverProvider()
    {
    }

    private List<UserGroup> getUserGroups(ReceiversLoadContext context) throws Exception
    {
        List<UserGroup> userGroups = new ArrayList<UserGroup>();

        GroupDao dao = daoProvider.get();
        userGroups.addAll(dao.getUserGroupsByUser(context.getUserId()));

        DeptService service = deptServiceProvider.get();
        DeptInfo dept = service.getDept(context.getDeptId());
        Collection<Integer> authDeptIds = context.getAuthDeptIds();

        while (dept != null)
        {
            if (authDeptIds == null || authDeptIds.contains(dept.getDeptId()))
                userGroups.addAll(dao.getUserGroupsByDept(dept.getDeptId()));

            dept = dept.parentDept();
        }

        return userGroups;
    }

    public List<ReceiverGroup> getGroups(ReceiversLoadContext context, String parentGroupId) throws Exception
    {
        if (parentGroupId == null)
        {
            List<UserGroup> userGroups = getUserGroups(context);
            List<ReceiverGroup> receiverGroups = new ArrayList<ReceiverGroup>(userGroups.size());

            for (UserGroup userGroup : userGroups)
            {
                receiverGroups.add(new ReceiverGroup(userGroup.getGroupId().toString(), userGroup.getGroupName(),
                        false));
            }

            return receiverGroups;
        }
        else
        {
            return null;
        }
    }

    public List<Receiver> getReceivers(ReceiversLoadContext context, String groupId) throws Exception
    {
        if (groupId == null || "0".equals(groupId))
            return null;

        UserGroup group = daoProvider.get().getUserGroup(new Integer(groupId));

        return UserReceiverProvider.toReceivers(group.getUsers(), context.getType());
    }

    public List<Receiver> queryReceivers(ReceiversLoadContext context, String s) throws Exception
    {
        return null;
    }

    public List<String> queryGroup(ReceiversLoadContext context, String s) throws Exception
    {
        List<UserGroup> groups = getUserGroups(context);
        List<String> result = new ArrayList<String>(groups.size());
        for (UserGroup group : groups)
        {
            if (Tools.matchText(group.getGroupName(), s))
                result.add(group.getGroupId().toString());
        }

        return result;
    }

    public String getParentGroupId(ReceiversLoadContext context, String groupId) throws Exception
    {
        return null;
    }

    public String getName()
    {
        return "用户组";
    }

    public String getId()
    {
        return "usergroup";
    }

    public boolean isGroup()
    {
        return true;
    }

    public boolean accept(ReceiversLoadContext context) throws Exception
    {
        return !ReceiversLoadContext.STATIONID.equals(context.getType());
    }
}
