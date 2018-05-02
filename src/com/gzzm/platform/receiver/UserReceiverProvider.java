package com.gzzm.platform.receiver;

import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 从系统用户中加载接收者
 *
 * @author camel
 * @date 2010-4-28
 */
public class UserReceiverProvider implements ReceiverProvider
{
    @Inject
    private static Provider<DeptService> serviceProvider;

    @Inject
    private static Provider<OrganDao> daoProvider;

    public UserReceiverProvider()
    {
    }

    public List<ReceiverGroup> getGroups(ReceiversLoadContext context, String parentGroupId) throws Exception
    {
        DeptService service = serviceProvider.get();
        Collection<Integer> authDeptIds = context.getAuthDeptIds();

        boolean self = false;
        if (parentGroupId != null)
        {
            self = parentGroupId.startsWith("$");
            if (self)
                parentGroupId = parentGroupId.substring(1);
        }

        List<SimpleDeptInfo> depts =
                service.getAuthDeptTree(parentGroupId == null ? null : Integer.valueOf(parentGroupId), null,
                        authDeptIds);

        List<ReceiverGroup> result = new ArrayList<ReceiverGroup>(depts.size());
        for (SimpleDeptInfo dept : depts)
        {
            Integer deptId = dept.getDeptId();
            String id = deptId.toString();
            if (self)
                id = "$" + id;
            result.add(new ReceiverGroup(id, dept.getDeptName(),
                    service.containsSubAuthDept(deptId, null, authDeptIds)));
        }

        if (parentGroupId == null && !ReceiversLoadContext.STATIONID.equals(context.getType()))
        {
            Integer bureauId = context.getUser().getBureauId();
            if (bureauId != 1 && (authDeptIds == null || authDeptIds.contains(bureauId)))
            {
                boolean exists = false;
                for (SimpleDeptInfo dept : depts)
                {
                    if (dept.getDeptId().equals(bureauId))
                    {
                        exists = true;
                        break;
                    }
                }

                SimpleDeptInfo bureau = context.getUser().getBureau();
                String bureauName = bureau.getDeptName();
                if (exists)
                {
                    if (((DeptInfo) bureau).getSubDeptIds(authDeptIds).size() > 1)
                    {
                        result.add(new ReceiverGroup("#" + bureauId, bureauName + "所有人员", false));
                    }
                }
                else
                {
                    boolean hasChildGroups = service.containsSubAuthDept(bureauId, null, authDeptIds);
                    result.add(new ReceiverGroup("$" + bureauId, bureauName, hasChildGroups));
                    if (hasChildGroups)
                        result.add(new ReceiverGroup("#" + bureauId, bureauName + "所有人员", false));
                }
            }
        }

        return result;
    }

    public List<Receiver> getReceivers(ReceiversLoadContext context, String groupId) throws Exception
    {
        if (groupId == null)
            return null;

        if (ReceiversLoadContext.STATIONID.equals(context.getType()))
        {
            List<Station> stations = daoProvider.get().getStationsInDept(Integer.valueOf(groupId));

            return toReceivers(stations);
        }
        else
        {
            OrganDao dao = daoProvider.get();

            List<User> users;
            if (groupId.startsWith("#"))
            {
                groupId = groupId.substring(1);
                Integer deptId = Integer.valueOf(groupId);

                users = new ArrayList<User>();
                loadUsers(dao.getDept(deptId), users, dao, context.getAuthDeptIds());
            }
            else
            {
                if (groupId.startsWith("$"))
                    groupId = groupId.substring(1);
                Integer deptId = Integer.valueOf(groupId);
                users = daoProvider.get().getUsersInDept(deptId);
                if (users.size() == 0)
                {
                    users = new ArrayList<User>();
                    loadUsers(dao.getDept(deptId), users, dao, context.getAuthDeptIds());
                }
            }

            return toReceivers(users, context.getType());
        }
    }

    public List<Receiver> queryReceivers(ReceiversLoadContext context, String s) throws Exception
    {
        if (ReceiversLoadContext.STATIONID.equals(context.getType()))
        {
            return null;
        }
        else
        {
            List<User> users =
                    daoProvider.get().queryUsersByName(s, context.getAuthDeptIds(), context.getMaxCount());

            return toReceivers(users, context.getType());
        }
    }

    public static List<Receiver> toReceivers(List<User> users, String type)
    {
        List<Receiver> result = new ArrayList<Receiver>(users.size());

        for (User user : users)
        {
            Receiver receiver = toReceiver(user, type);
            if (receiver != null)
                result.add(receiver);
        }

        return result;
    }

    private static Receiver toReceiver(User user, String type)
    {
        if (user.getState() != null && user.getState().intValue() != 0)
            return null;

        String name = user.getUserName();

        Receiver receiver = new Receiver();
        receiver.setId(user.getUserId().toString());
        receiver.setEditable(false);
        receiver.setName(name);
        receiver.setRemark(user.firstDeptName(1));


        if (ReceiversLoadContext.USERID.equals(type))
        {
            //用户ID
            receiver.setValue(user.getUserId().toString() + "@local");
        }
        else
        {
            String value;

            if (ReceiversLoadContext.EMAIL.equals(type))
            {
                //电子邮件
                value = user.getLoginName() + "@local";
            }
            else if (ReceiversLoadContext.PHONE.equals(type))
            {
                //手机号码
                value = user.getPhone();
            }
            else
            {
                //其他类型，从用户扩展属性表中获取
                value = user.getAttributes().get(type);
            }


            if (StringUtils.isEmpty(value))
            {
                //用户没有定义此联系方式，设置无效
                receiver.setValid(false);
                receiver.setValue("user_" + user.getUserId());
            }
            else
            {
                if (ReceiversLoadContext.EMAIL.equals(type) && !StringUtils.isEmpty(user.getSourceMail()))
                    value = user.getSourceMail();
                else
                    value = user.getUserId() + "@local";
                receiver.setValue("\"" + name + "\"<" + value + ">");
            }
        }

        return receiver;
    }

    public static List<Receiver> toReceivers(List<Station> stations)
    {
        List<Receiver> result = new ArrayList<Receiver>(stations.size());

        for (Station station : stations)
        {
            result.add(toReceiver(station));
        }

        return result;
    }

    private static Receiver toReceiver(Station station)
    {
        Receiver receiver = new Receiver();

        receiver.setId(station.getStationId().toString());
        receiver.setValue(station.getStationId().toString());
        receiver.setName(station.getStationName());
        receiver.setRemark(station.getDept().getAllName(1));

        return receiver;
    }

    public List<String> queryGroup(ReceiversLoadContext context, String s) throws Exception
    {
        DeptService service = serviceProvider.get();

        List<? extends DeptInfo> depts = service.searchDept(s, context.getAuthDeptIds());

        List<String> result = new ArrayList<String>(depts.size());
        for (DeptInfo dept : depts)
            result.add(dept.getDeptId().toString());

        return result;
    }

    public String getParentGroupId(ReceiversLoadContext context, String groupId) throws Exception
    {
        DeptService service = serviceProvider.get();

        Collection<Integer> authDeptIds = context.getAuthDeptIds();

        DeptInfo parentDept = service.getDept(new Integer(groupId)).parentDept();

        if (parentDept == null)
            return null;


        if (authDeptIds == null)
            return parentDept.getDeptId().toString();

        while (parentDept != null && !authDeptIds.contains(parentDept.getDeptId()))
            parentDept = parentDept.parentDept();

        if (parentDept != null)
            return parentDept.getDeptId().toString();

        return null;
    }

    private boolean loadUsers(DeptInfo dept, List<User> users, OrganDao dao, Collection<Integer> authDeptIds)
            throws Exception
    {
        Integer deptId = dept.getDeptId();

        if (authDeptIds == null || authDeptIds.contains(deptId))
        {
            for (User user : dao.getUsersInDept(deptId))
            {
                Integer userId = user.getUserId();
                boolean exists = false;
                for (User user1 : users)
                {
                    if (user1.getUserId().equals(userId))
                    {
                        exists = true;
                        break;
                    }
                }

                if (!exists)
                {
                    users.add(user);
                    if (users.size() > 300)
                    {
                        return false;
                    }
                }
            }
        }

        for (DeptInfo subDept : dept.subDepts())
        {
            if (!loadUsers(subDept, users, dao, authDeptIds))
                return false;
        }

        return true;
    }

    public String getName()
    {
        return "组织机构";
    }

    public String getId()
    {
        return "user";
    }

    public boolean isGroup()
    {
        return true;
    }

    public boolean accept(ReceiversLoadContext context) throws Exception
    {
        return true;
    }
}
