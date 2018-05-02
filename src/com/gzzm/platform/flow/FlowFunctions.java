package com.gzzm.platform.flow;

import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;

import java.util.*;

/**
 * 在流程脚本中调用的方法
 *
 * @author camel
 * @date 11-10-12
 */
@SuppressWarnings("UnusedDeclaration")
public final class FlowFunctions
{
    @Inject
    private static Provider<DeptService> deptServiceProvider;

    private FlowFunctions()
    {
    }

    /**
     * 获得若干个用户作为接收者
     *
     * @param userIds 用户ID
     * @return 接收者列表
     * @throws Exception 从数据库加载用户信息错误
     */
    public static NodeReceiverList getUser(Integer[] userIds) throws Exception
    {
        if (userIds == null || userIds.length == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        return getUser(Arrays.asList(userIds));
    }

    /**
     * 获得若干个用户作为接收者
     *
     * @param userIds 用户ID
     * @return 接收者列表
     * @throws Exception 从数据库加载用户信息错误
     */
    public static NodeReceiverList getUser(Collection<Integer> userIds) throws Exception
    {
        if (userIds == null || userIds.size() == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        DeptService deptService = deptServiceProvider.get();

        NodeReceiverList receiverList = new NodeReceiverList(userIds.size());
        for (Integer userId : userIds)
        {
            User user = deptService.getDao().getUser(userId);
            NodeReceiver nodeReceiver = new NodeReceiver(userId.toString(), user.getUserName());
            Dept dept = user.getDepts().get(0);
            nodeReceiver.setProperty("deptId", dept.getDeptId());
            nodeReceiver.setProperty("deptName", dept.getDeptName());
            nodeReceiver.setProperty("stations", getStations(user, dept.getDeptId()));
            receiverList.addReceiver(nodeReceiver);
        }

        return receiverList;
    }

    /**
     * 获得一个用户作为接收者
     *
     * @param userId 用户ID
     * @return 接收者列表
     * @throws Exception 从数据库加载用户信息错误
     */
    public static NodeReceiverList getUser(Integer userId) throws Exception
    {
        if (userId == null)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        return getUser(Collections.singleton(userId));
    }

    /**
     * 获得若干个部门作为接收者
     *
     * @param deptIds 部门ID
     * @return 接收者列表
     * @throws Exception 从数据库加载部门信息错误
     */
    public static NodeReceiverList getDept(Integer[] deptIds) throws Exception
    {
        if (deptIds == null || deptIds.length == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        return getDept(Arrays.asList(deptIds));
    }

    /**
     * 获得若干个部门作为接收者
     *
     * @param deptIds 部门ID
     * @return 接收者列表
     * @throws Exception 从数据库加载部门信息错误
     */
    public static NodeReceiverList getDept(Collection<Integer> deptIds) throws Exception
    {
        if (deptIds == null || deptIds.size() == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        DeptService deptService = deptServiceProvider.get();

        NodeReceiverList receiverList = new NodeReceiverList(deptIds.size());
        for (Integer deptId : deptIds)
        {
            String deptName = deptService.getDeptName(deptId);
            NodeReceiver nodeReceiver = new NodeReceiver("@" + deptId, deptName);
            nodeReceiver.setProperty("deptId", deptId);
            receiverList.addReceiver(nodeReceiver);
        }

        return receiverList;
    }

    /**
     * 获得一个部门作为接收者
     *
     * @param deptId 部门ID
     * @return 接收者列表
     * @throws Exception 从数据库加载部门信息错误
     */
    public static NodeReceiverList getDept(Integer deptId) throws Exception
    {
        if (deptId == null)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        return getDept(Collections.singleton(deptId));
    }

    public static NodeReceiverList getDeptByDeptId(Integer deptId) throws Exception
    {
        return getDept(deptId);
    }

    public static NodeReceiverList getDeptByDeptIds(Integer[] deptIds) throws Exception
    {
        return getDept(deptIds);
    }

    public static NodeReceiverList getApp(Integer deptId, String appId) throws Exception
    {
        return getApp(Collections.singleton(deptId), appId);
    }

    public static NodeReceiverList getApp(Integer[] deptIds, String appId) throws Exception
    {
        return getApp(Arrays.asList(deptIds), appId);
    }

    public static NodeReceiverList getApp(Collection<Integer> deptIds, String appId) throws Exception
    {
        if (deptIds == null || deptIds.size() == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        DeptService deptService = deptServiceProvider.get();

        NodeReceiverList receiverList = new NodeReceiverList(deptIds.size());
        for (Integer deptId : deptIds)
        {
            String deptName = deptService.getDeptName(deptId);
            NodeReceiver nodeReceiver = new NodeReceiver(deptId + "&" + appId, deptName);
            nodeReceiver.setProperty("deptId", deptId);
            receiverList.addReceiver(nodeReceiver);
        }

        return receiverList;
    }

    public static List<Integer> getSubDeptIds(Integer deptId) throws Exception
    {
        return deptServiceProvider.get().getDept(deptId).allSubDeptIds();
    }

    public static Integer getSubDeptIdByName(Integer deptId, String name) throws Exception
    {
        return getSubDeptIdByName(deptId, Arrays.asList(name));
    }

    public static Integer getSubDeptIdByName(Integer deptId, String[] names) throws Exception
    {
        return getSubDeptIdByName(deptId, Arrays.asList(names));
    }

    public static Integer getSubDeptIdByName(Integer deptId, List<String> names) throws Exception
    {
        return getSubDeptIdByName(deptServiceProvider.get().getDept(deptId), names);
    }

    private static Integer getSubDeptIdByName(DeptInfo dept, List<String> names) throws Exception
    {
        for (DeptInfo subDept : dept.subDepts())
        {
            if (names.contains(subDept.getDeptName()))
                return subDept.getDeptId();
        }

        for (DeptInfo subDept : dept.subDepts())
        {
            Integer result = getSubDeptIdByName(subDept, names);

            if (result != null)
                return result;
        }

        if (names.contains(dept.getDeptName()))
            return dept.getDeptId();

        return null;
    }

    public static NodeReceiverList getUserInDept(Integer deptId) throws Exception
    {
        return getUserInDept(Collections.singleton(deptId));
    }

    public static NodeReceiverList getUserInDept(Integer[] deptIds) throws Exception
    {
        return getUserInDept(Arrays.asList(deptIds));
    }

    /**
     * 获得部门中的用户
     *
     * @param deptIds 部门ID列表
     * @return 接收者列表
     * @throws Exception 从数据库加载部门和用户信息错误
     */
    public static NodeReceiverList getUserInDept(Collection<Integer> deptIds) throws Exception
    {
        if (deptIds == null || deptIds.size() == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        DeptService deptService = deptServiceProvider.get();

        NodeReceiverList receiverList = new NodeReceiverList();

        for (Integer deptId : deptIds)
        {
            if (deptId != null)
            {
                List<User> users = deptService.getDao().getUsersInDept(deptId);

                if (users.size() > 0)
                {
                    DeptInfo dept = deptService.getDept(deptId);
                    for (User user : users)
                    {
                        NodeReceiver nodeReceiver =
                                new NodeReceiver(Integer.toString(user.getUserId()), user.getUserName());
                        nodeReceiver.setProperty("deptId", deptId);
                        nodeReceiver.setProperty("deptName", dept.getDeptName());
                        nodeReceiver.setProperty("stations", getStations(user, deptId));

                        receiverList.addReceiver(nodeReceiver);
                    }
                }
            }
        }

        return receiverList;
    }

    public static NodeReceiverList getUserOnStation(Integer deptId, String stationName) throws Exception
    {
        return getUserOnStation(Collections.singleton(deptId), Collections.singletonList(stationName));
    }

    public static NodeReceiverList getUserOnStation(Integer deptId, String[] stationNames) throws Exception
    {
        return getUserOnStation(Collections.singleton(deptId), Arrays.asList(stationNames));
    }

    public static NodeReceiverList getUserOnStation(Integer deptId, List<String> stationNames) throws Exception
    {
        return getUserOnStation(Collections.singleton(deptId), stationNames);
    }

    public static NodeReceiverList getUserOnStation(Integer[] deptIds, String stationName) throws Exception
    {
        return getUserOnStation(Arrays.asList(deptIds), Collections.singletonList(stationName));
    }

    public static NodeReceiverList getUserOnStation(Collection<Integer> deptIds, String stationName) throws Exception
    {
        return getUserOnStation(deptIds, Collections.singletonList(stationName));
    }

    public static NodeReceiverList getUserOnStation(Integer[] deptIds, String[] stationNames) throws Exception
    {
        return getUserOnStation(Arrays.asList(deptIds), Arrays.asList(stationNames));
    }

    public static NodeReceiverList getUserOnStation(Integer[] deptIds, List<String> stationNames) throws Exception
    {
        return getUserOnStation(Arrays.asList(deptIds), stationNames);
    }

    public static NodeReceiverList getUserOnStation(Collection<Integer> deptIds, String[] stationNames) throws Exception
    {
        return getUserOnStation(deptIds, Arrays.asList(stationNames));
    }

    /**
     * 获得某部门某岗位上的用户
     *
     * @param deptIds      部门ID
     * @param stationNames 岗位列表
     * @return 接收者列表
     * @throws Exception 从数据库读取部门或用户错误
     */
    public static NodeReceiverList getUserOnStation(Collection<Integer> deptIds, List<String> stationNames)
            throws Exception
    {
        if (deptIds == null || deptIds.size() == 0 || stationNames == null || stationNames.size() == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        DeptService deptService = deptServiceProvider.get();

        NodeReceiverList receiverList = new NodeReceiverList();

        if (deptIds.size() == 1)
        {
            Integer deptId = deptIds.iterator().next();
            DeptInfo dept = deptService.getDept(deptId);
            List<User> users = deptService.getDao().getUsersByStationNames(deptId, stationNames);

            if (users.size() > 0)
            {
                for (User user : users)
                {
                    NodeReceiver nodeReceiver =
                            new NodeReceiver(Integer.toString(user.getUserId()), user.getUserName());
                    nodeReceiver.setProperty("deptId", deptId);
                    nodeReceiver.setProperty("deptName", dept.getDeptName());
                    nodeReceiver.setProperty("stations", getStations(user, deptId));

                    receiverList.addReceiver(nodeReceiver);
                }
            }
        }
        else
        {
            List<UserDept> userDepts = deptService.getDao().getUserDeptsByStationNames(deptIds, stationNames);

            if (userDepts.size() > 0)
            {
                for (UserDept userDept : userDepts)
                {
                    String userId = Integer.toString(userDept.getUserId());

                    NodeReceiver nodeReceiver = null;
                    for (NodeReceiver nodeReceiver1 : receiverList.getReceiverList())
                    {
                        if (nodeReceiver1.getReceiver().equals(userId))
                        {
                            nodeReceiver = nodeReceiver1;
                            break;
                        }
                    }

                    if (nodeReceiver == null)
                    {
                        nodeReceiver = new NodeReceiver(userId, userDept.getUser().getUserName());
                        nodeReceiver.setProperty("deptId", userDept.getDeptId());
                        nodeReceiver.setProperty("deptName", userDept.getDept().getDeptName());
                        nodeReceiver.setProperty("stations", getStations(userDept));
                        receiverList.addReceiver(nodeReceiver);
                    }
                    else
                    {
                        nodeReceiver.setProperty("deptName",
                                nodeReceiver.getProperty("deptName") + "," + userDept.getDept().getDeptName());
                        nodeReceiver.setProperty("stations", StringUtils.concat((String) nodeReceiver.getProperty(
                                "stations"), getStations(userDept), ","));
                    }
                }
            }
        }

        return receiverList;
    }

    public static NodeReceiverList getUserWithRole(Integer deptId, String roleName) throws Exception
    {
        return getUserWithRole(Collections.singleton(deptId), Collections.singletonList(roleName));
    }

    public static NodeReceiverList getUserWithRole(Integer deptId, String[] roleNames) throws Exception
    {
        return getUserWithRole(Collections.singleton(deptId), Arrays.asList(roleNames));
    }

    public static NodeReceiverList getUserWithRole(Integer deptId, List<String> roleNames) throws Exception
    {
        return getUserWithRole(Collections.singleton(deptId), roleNames);
    }

    public static NodeReceiverList getUserWithRole(Integer[] deptIds, String roleName) throws Exception
    {
        return getUserWithRole(Arrays.asList(deptIds), Collections.singletonList(roleName));
    }

    public static NodeReceiverList getUserWithRole(Integer[] deptIds, String[] roleNames) throws Exception
    {
        return getUserWithRole(Arrays.asList(deptIds), Arrays.asList(roleNames));
    }

    public static NodeReceiverList getUserWithRole(Integer[] deptIds, List<String> roleNames) throws Exception
    {
        return getUserWithRole(Arrays.asList(deptIds), roleNames);
    }

    public static NodeReceiverList getUserWithRole(Collection<Integer> deptIds, String roleName) throws Exception
    {
        return getUserWithRole(deptIds, Collections.singletonList(roleName));
    }

    public static NodeReceiverList getUserWithRole(Collection<Integer> deptIds, String[] roleNames) throws Exception
    {
        return getUserWithRole(deptIds, Arrays.asList(roleNames));
    }

    /**
     * 获得某部门拥有某角色的用户
     *
     * @param deptIds   部门ID
     * @param roleNames 角色列表
     * @return 接收者列表
     * @throws Exception 从数据库读取部门或用户错误
     */
    public static NodeReceiverList getUserWithRole(Collection<Integer> deptIds, List<String> roleNames)
            throws Exception
    {
        if (deptIds == null || deptIds.size() == 0 || roleNames == null || roleNames.size() == 0)
            return NodeReceiverList.EMPTYRECEIVERLIST;

        DeptService deptService = deptServiceProvider.get();

        NodeReceiverList receiverList = new NodeReceiverList();

        for (Integer deptId : deptIds)
        {
            DeptInfo dept = deptService.getDept(deptId);
            List<User> users = deptService.getDao().getUsersByRoleNames(deptId, roleNames);

            if (users.size() > 0)
            {
                for (User user : users)
                {
                    NodeReceiver nodeReceiver =
                            new NodeReceiver(Integer.toString(user.getUserId()), user.getUserName());
                    nodeReceiver.setProperty("deptId", deptId);
                    nodeReceiver.setProperty("deptName", dept.getDeptName());
                    nodeReceiver.setProperty("stations", getStations(user, deptId));

                    receiverList.addReceiver(nodeReceiver);
                }
            }
        }

        return receiverList;
    }

    /**
     * 从组织机构里选择用户
     *
     * @param scopeId 组织机构的部门范围，如果没有定义，则由权限user_select决定
     * @param deptId  业务部门ID
     * @return 对应的接收者列表对象
     * @see com.gzzm.platform.group.PageMemberSelector
     * @see com.gzzm.platform.group.Member#USER_SELECT_APP
     */
    public static NodeReceiverList getUserSelect(Integer scopeId, Integer deptId)
    {
        NodeReceiver nodeReceiver = new NodeReceiver("&", "", true);
        nodeReceiver.setProperty("scopeId", scopeId);
        if (deptId != null && deptId > 0)
            nodeReceiver.setProperty("deptId", deptId);
        return new NodeReceiverList(nodeReceiver);
    }

    public static NodeReceiverList getUserSelect(Integer scopeId)
    {
        return getUserSelect(scopeId, null);
    }

    /**
     * 从组织机构里选择用户
     *
     * @param scopeName 组织机构的部门范围名称，如果没有定义，则由权限user_select决定
     * @param deptId    当前部门ID
     * @return 对应的接收者列表对象
     * @throws Exception 从数据库读取范围ID出错
     * @see com.gzzm.platform.group.PageMemberSelector
     * @see com.gzzm.platform.group.Member#USER_SELECT_APP
     */
    public static NodeReceiverList getUserSelect(String scopeName, Integer deptId) throws Exception
    {
        DeptService deptService = deptServiceProvider.get();

        RoleScope scope = deptService.getRoleScopeByName(deptId, scopeName);
        if (scope != null)
            return getUserSelect(scope.getScopeId(), deptId);
        else
            return getUserSelect((Integer) null, deptId);
    }

    public static NodeReceiverList getUserSelect()
    {
        return getUserSelect(null);
    }

    public static NodeReceiverList getDeptSelect(Integer scopeId, Integer deptId, String appId)
    {
        NodeReceiver nodeReceiver = new NodeReceiver("&" + appId, "", true);
        nodeReceiver.setProperty("scopeId", scopeId);
        if (deptId != null && deptId > 0)
            nodeReceiver.setProperty("deptId", deptId);
        return new NodeReceiverList(nodeReceiver);
    }

    public static NodeReceiverList getDeptSelect(Integer scopeId, String appId)
    {
        return getDeptSelect(scopeId, null, appId);
    }

    public static NodeReceiverList getDeptSelect(String scopeName, Integer deptId, String appId) throws Exception
    {
        DeptService deptService = deptServiceProvider.get();

        RoleScope scope = deptService.getRoleScopeByName(deptId, scopeName);
        if (scope != null)
            return getDeptSelect(scope.getScopeId(), deptId, appId);
        else
            return getUserSelect((Integer) null, deptId);
    }

    public static NodeReceiverList getDeptSelect(String appId)
    {
        return getDeptSelect(null, appId);
    }

    public static NodeReceiverList getUserSelectWithDept(Integer deptId) throws Exception
    {
        return getUserSelect("流程用户选择", deptId);
    }

    public static String getStations(User user, Integer deptId)
    {
        List<UserStation> userStations = user.getStations();
        List<String> stationNames = new ArrayList<String>(userStations.size());

        for (UserStation userStation : userStations)
        {
            if (deptId == null || userStation.getDeptId().equals(deptId))
            {
                String stationName = userStation.getStation().getStationName();

                if (!stationNames.contains(stationName))
                    stationNames.add(stationName);
            }
        }

        return StringUtils.concat(stationNames, ",");
    }

    public static String getStations(UserDept userDept)
    {
        List<UserStation> userStations = userDept.getStations();
        List<String> stationNames = new ArrayList<String>(userStations.size());

        for (UserStation userStation : userStations)
        {
            String stationName = userStation.getStation().getStationName();

            if (!stationNames.contains(stationName))
                stationNames.add(stationName);
        }

        return StringUtils.concat(stationNames, ",");
    }

    public static boolean isOnStation(User user, Integer deptId, Collection<String> stationNames)
    {
        List<UserStation> userStations = user.getStations();
        for (UserStation userStation : userStations)
        {
            if (deptId == null || userStation.getDeptId().equals(deptId))
            {
                String stationName = userStation.getStation().getStationName();

                if (stationNames.contains(stationName))
                    return true;
            }
        }

        return false;
    }

    public static boolean isOnStation(User user, Integer deptId, String[] stationNames)
    {
        return isOnStation(user, deptId, Arrays.asList(stationNames));
    }

    public static boolean isOnStation(User user, Integer deptId, String stationName)
    {
        return isOnStation(user, deptId, Collections.singleton(stationName));
    }

    public static boolean isOnStation(Integer userId, Integer deptId, Collection<String> stationNames) throws Exception
    {
        return isOnStation(deptServiceProvider.get().getDao().getUser(userId), deptId, stationNames);
    }

    public static boolean isOnStation(Integer userId, Integer deptId, String[] stationNames) throws Exception
    {
        return isOnStation(userId, deptId, Arrays.asList(stationNames));
    }

    public static boolean isOnStation(Integer userId, Integer deptId, String stationName) throws Exception
    {
        return isOnStation(userId, deptId, Collections.singleton(stationName));
    }

    public static String getDeptAttribute(Integer deptId, String attributeName, String defaultValue) throws Exception
    {
        DeptInfo deptInfo = deptServiceProvider.get().getDept(deptId);

        String value = null;
        if (deptInfo != null)
            value = deptInfo.getAttributes().get(attributeName);

        if (StringUtils.isEmpty(value))
            value = defaultValue;

        return value;
    }

    public static String getDeptAttribute(Integer deptId, String attributeName) throws Exception
    {
        return getDeptAttribute(deptId, attributeName, null);
    }

    public static String[] getDeptAttributeValues(Integer deptId, String attributeName, String[] defaultValues)
            throws Exception
    {
        DeptInfo deptInfo = deptServiceProvider.get().getDept(deptId);

        String[] values = defaultValues;

        if (deptInfo != null)
        {
            String s = deptInfo.getAttributes().get(attributeName);
            if (!StringUtils.isEmpty(s))
                values = s.split(",");
        }

        return values;
    }

    public static String[] getDeptAttributeValues(Integer deptId, String attributeName) throws Exception
    {
        return getDeptAttributeValues(deptId, attributeName, new String[]{});
    }

    public static String[] getDeptsAttribute(Integer[] deptIds, String attributeName, String defaultValue)
            throws Exception
    {
        String[] result = new String[deptIds.length];

        for (int i = 0; i < deptIds.length; i++)
        {
            DeptInfo deptInfo = deptServiceProvider.get().getDept(deptIds[i]);

            String value = null;
            if (deptInfo != null)
                value = deptInfo.getAttributes().get(attributeName);

            if (StringUtils.isEmpty(value))
                value = defaultValue;

            result[i] = value;
        }

        return result;
    }

    /**
     * 通过名称获得岗位接收者
     *
     * @param deptId      部门ID
     * @param stationName 岗位名称
     * @return 接收者列表
     * @throws Exception 从数据库加载部门信息错误
     */
    public static NodeReceiverList getStationByName(Integer deptId, String stationName) throws Exception
    {
        NodeReceiver nodeReceiver = new NodeReceiver(stationName + "#" + deptId, stationName);
        nodeReceiver.setProperty("deptId", deptId);

        return new NodeReceiverList(nodeReceiver);
    }

    /**
     * 通过名称获得岗位接收者
     *
     * @param deptId       部门ID
     * @param stationNames 岗位名称
     * @return 接收者列表
     * @throws Exception 从数据库加载部门信息错误
     */
    public static NodeReceiverList getStationByName(Integer deptId, List<String> stationNames) throws Exception
    {
        NodeReceiverList nodeReceiverList = new NodeReceiverList(stationNames.size());
        for (String stationName : stationNames)
        {
            NodeReceiver nodeReceiver = new NodeReceiver(stationName + "#" + deptId, stationName);
            nodeReceiver.setProperty("deptId", deptId);
            nodeReceiverList.addReceiver(nodeReceiver);
        }

        return nodeReceiverList;
    }

    /**
     * 通过岗位ID获得岗位接收者
     *
     * @param deptId    部门ID
     * @param stationId 岗位ID
     * @return 接收者列表
     * @throws Exception 从数据库加载部门信息错误
     */
    public static NodeReceiverList getStationById(Integer deptId, Integer stationId) throws Exception
    {
        Station station = deptServiceProvider.get().getDao().getStation(stationId);

        NodeReceiver nodeReceiver = new NodeReceiver(stationId + "@" + deptId, station.getStationName());

        nodeReceiver.setProperty("deptId", deptId == null ? deptId : station.getDeptId());

        return new NodeReceiverList(nodeReceiver);
    }

    public static Integer getNodeLastOperatorDeptId(String nodeId, FlowService service) throws Exception
    {
        FlowStep step = service.getLastStep(nodeId);

        if (step == null)
            return null;

        Integer deptId = step.getProperty("deptId");
        if (deptId != null)
            return deptId;

        String receiver = step.getReceiver();
        if (StringUtils.isEmpty(receiver))
            return null;

        if (receiver.startsWith("$"))
            return null;

        int index = receiver.indexOf('@');
        if (index >= 0)
            return Integer.valueOf(receiver.substring(index + 1));

        index = receiver.indexOf('#');
        if (index > 0)
            return Integer.valueOf(receiver.substring(index + 1));

        User user = SystemFlowDao.getInstance().getUser(Integer.valueOf(receiver));

        if (user != null)
        {
            List<Dept> depts = user.getDepts();
            if (depts.size() > 0)
            {
                Set<Integer> businessDeptIds = Collections.singleton(FlowApi.getDeptId(service.getInstance()));
                for (Dept dept : depts)
                {
                    if (dept.isParentDeptIdsIn(businessDeptIds))
                        return dept.getDeptId();
                }

                return depts.get(0).getDeptId();
            }
        }

        return null;
    }

    public static DeptInfo getDeptInfo(Integer deptId) throws Exception
    {
        return deptServiceProvider.get().getDept(deptId);
    }

    public static void sendSmsToPhone(String content, String phone) throws Exception
    {
        com.gzzm.platform.message.Message.sendSmsToPhone(content, phone);
    }

    public static void sendMessageToUser(String content, Integer userId) throws Exception
    {
        com.gzzm.platform.message.Message.sendMessageToUser(content, userId);
    }

    public static void sendMessageToNode(String content, String nodeId, ScriptContext context) throws Exception
    {
        com.gzzm.platform.message.Message message = new com.gzzm.platform.message.Message();

        List<FlowStep> noDealSteps = context.getService().getNoDealSteps(nodeId);

        FlowApi.sendMessage(content, noDealSteps, null);
    }
}
