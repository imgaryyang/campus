package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.consignation.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.message.ImMessageSender;
import com.gzzm.platform.message.sms.SmsMessageSender;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;

import java.util.*;

/**
 * 和流程相关的一些Api
 *
 * @author camel
 * @date 2010-9-19
 */
public final class FlowApi
{
    private final static FlowContainer FLOW_CONTAINER = new FlowContainer(new SystemFlowLoader(), 1000 * 60 * 60 * 24);

    @Inject
    private static Provider<DeptService> deptServiceProvider;

    @Inject
    private static Provider<ConsignationService> consignationServiceProvider;

    private FlowApi()
    {
    }

    /**
     * 通过流程ID加载流程对象
     *
     * @param flowId 流程ID
     * @return 流程对象
     * @throws Exception 数据库获取流程信息错误
     */
    public static Flow getFlow(Integer flowId) throws Exception
    {
        return FLOW_CONTAINER.get(flowId.toString());
    }

    /**
     * 新发起一个流程
     *
     * @param instance 流程实例的内容，初始化一些流程数据
     * @return 流程上下文，可以通过流程上下文操作流程
     * @throws Exception 发起流程失败，可能是流程定义错误或者数据库错误
     */
    public static FlowContext newContext(FlowInstance instance, SystemFlowDao dao) throws Exception
    {
        FlowContext context = FlowContext.newContext(dao, FLOW_CONTAINER.get(instance.getFlowId()), instance,
                SystemFlowEnhancer.INSTANCE);

        //标识流程被使用
        setFlowUsed(Integer.valueOf(instance.getFlowId()));

        return context;
    }

    /**
     * 根据一个流程步骤加载流程上下文，以操作流程
     *
     * @param stepId 步骤ID
     * @return 流程上下文，可以通过流程上下文操作流程
     * @throws Exception 加载流程失败，可能是流程定义错误或者数据库错误
     */
    public static FlowContext loadContext(Long stepId, SystemFlowDao dao) throws Exception
    {
        FlowContext flowContext = FlowContext.loadContext(dao, FLOW_CONTAINER,
                stepId.toString(), SystemFlowEnhancer.INSTANCE);

        if (flowContext.getInstance().getState() == -1)
            flowContext.getInstance().setState(0);

        if (flowContext.getStep().getState() == -1)
            flowContext.getStep().setState(FlowStep.DRAFT);

        return flowContext;
    }

    /**
     * 获得流程控制器，流程控制器干涉某个流程实例的步骤和状态
     *
     * @param instanceId 流程实例ID
     * @param dao        流程数据访问对象
     * @return 流程控制器
     * @throws Exception 数据库错误
     */
    public static FlowController getController(Long instanceId, SystemFlowDao dao) throws Exception
    {
        return new FlowController(dao, instanceId.toString(), FLOW_CONTAINER,
                SystemFlowEnhancer.INSTANCE);
    }

    /**
     * 获得某个流程的最后一个版本
     *
     * @param ieFlowId 忽略版本号的流程ID
     * @return 最后一个发布版本的流程ID
     * @throws Exception 数据库查询错误
     */
    public static Integer getLastFlowId(Integer ieFlowId) throws Exception
    {
        return FlowInfoDao.getInstance().getLastFlowId(ieFlowId);
    }

    /**
     * 获得某个流程的最后一个版本
     *
     * @param ieFlowId 忽略版本号的流程信息
     * @return 最后一个发布版本的流程新兴
     * @throws Exception 数据库查询错误
     */
    public static FlowInfo getLastFlow(Integer ieFlowId) throws Exception
    {
        return FlowInfoDao.getInstance().getLastFlow(ieFlowId);
    }


    /**
     * 根据流程名称和部门ID获得一个发布的流程
     *
     * @param flowName 流程名称
     * @param type     流程类型
     * @param deptId   部门ID
     * @return 对应的流程的id
     * @throws Exception 数据库查询错误
     */
    public static Integer getFlowIdByName(String flowName, String type, Integer deptId) throws Exception
    {
        return FlowInfoDao.getInstance().getFlowIdByName(flowName, type, deptId);
    }


    public static void setFlowUsed(Integer flowId) throws Exception
    {
        FlowInfoDao.getInstance().setFlowUsed(flowId);
    }

    /**
     * 得到某个用户对应的所有接收者列表，包括用户，岗位，部门
     *
     * @param userId 用户ID
     * @return 接收者列表
     * @throws Exception 数据库查询错误
     */
    public static List<String> getReceivers(Integer userId) throws Exception
    {
        //用户
        List<String> result = new ArrayList<String>();
        result.add(userId.toString());

        List<UserDept> userDepts = deptServiceProvider.get().getDao().getUserDepts(userId);

        for (UserDept userDept : userDepts)
        {
            Integer deptId = userDept.getDeptId();
            result.add("@" + deptId);

            for (UserStation userStation : userDept.getStations())
            {
                String s = userStation.getStationId() + "@null";
                if (!result.contains(s))
                    result.add(s);
                result.add(userStation.getStationId() + "@" + userStation.getDeptId());
                result.add(userStation.getStation().getStationName() + "#" + userStation.getDeptId());
                result.add(userStation.getStation().getStationName() + "#" +
                        userStation.getDept().getParentDept(1).getDeptId());
            }
        }

        return result;
    }

    public static List<Integer> getUserIds(String receiver) throws Exception
    {
        if (StringUtils.isEmpty(receiver))
            return Collections.emptyList();

        int index = receiver.indexOf('@');

        List<User> users;

        if (index == 0)
        {
            users = deptServiceProvider.get().getDao().getUsersInDept(Integer.valueOf(receiver.substring(1)));
        }
        else if (index > 0)
        {
            Integer stationId = Integer.valueOf(receiver.substring(0, index));
            String deptStr = receiver.substring(index + 1);

            if ("null".equals(deptStr))
            {
                users = deptServiceProvider.get().getDao().getUsersOnStation(stationId);
            }
            else
            {
                Integer deptId = Integer.valueOf(deptStr);
                users = deptServiceProvider.get().getDao().getUsersOnStation(deptId, stationId);
            }
        }
        else
        {
            index = receiver.indexOf('#');

            if (index > 0)
            {
                String stationName = receiver.substring(0, index);
                Integer deptId = Integer.valueOf(receiver.substring(index + 1));

                users = deptServiceProvider.get().getDao()
                        .getUsersByStationNames(deptId, Collections.singletonList(stationName));
            }
            else
            {
                return Collections.singletonList(Integer.valueOf(receiver));
            }
        }

        List<Integer> userIds = new ArrayList<Integer>(users.size());
        for (User user : users)
        {
            userIds.add(user.getUserId());
        }

        return userIds;
    }

    public static void setConsignationInfo(List<? extends NodeReceiverInfo> receivers, String[] modules)
            throws Exception
    {
        ConsignationService consignationService = null;

        for (Iterator<? extends NodeReceiverInfo> iterator = receivers.iterator(); iterator.hasNext(); )
        {
            NodeReceiverInfo receiverInfo = iterator.next();
            //根据委托信息替换掉接收者
            String receiver = receiverInfo.getReceiver();

            String nodeId = null;

            if (receiverInfo instanceof NodeReceiverSelect)
                nodeId = ((NodeReceiverSelect) receiverInfo).getNodeId();

            if (receiver != null && !"#pass".equals(nodeId) && !"传阅".equals(nodeId) && receiver.indexOf('@') < 0 &&
                    receiver.indexOf('#') < 0)
            {
                //用户才需要委托
                Integer userId = Integer.valueOf(receiver);

                if (consignationService == null)
                    consignationService = consignationServiceProvider.get();

                ConsignationInfo consignationInfo = consignationService.getConsignationInfo(userId, modules);

                if (consignationInfo != null)
                {
                    //有委托信息
                    receiverInfo.setReceiver(consignationInfo.getConsignee().toString());

                    //判断是否有重复的接收者，时的话，剔除
                    if (nodeId != null)
                    {
                        boolean exists = false;
                        for (NodeReceiverInfo receiverInfo1 : receivers)
                        {
                            if (receiverInfo1 != receiverInfo && receiverInfo1 instanceof NodeReceiverSelect)
                            {
                                String nodeId1 = ((NodeReceiverSelect) receiverInfo1).getNodeId();

                                if (nodeId.equals(nodeId1) && receiverInfo.getReceiver().equals(
                                        receiverInfo1.getReceiver()))
                                {
                                    exists = true;
                                    break;
                                }
                            }
                        }

                        if (exists)
                            iterator.remove();
                    }

                    String consigneeName = deptServiceProvider.get().getDao().
                            getUser(consignationInfo.getConsignee()).getUserName();

                    receiverInfo.setReceiverName(Tools.getMessage("platform.flow.consignation", consigneeName,
                            receiverInfo.getReceiverName()));

                    //记录委托ID，此ID将被传递到FlowStep表
                    receiverInfo.setProperty("consignationId", consignationInfo.getConsignationId());
                }
            }
        }
    }

    public static void initReceiverName(List<? extends NodeReceiverInfo> receivers) throws Exception
    {
        FlowReceiverDao dao = null;

        for (NodeReceiverInfo receiverInfo : receivers)
        {
            if (StringUtils.isEmpty(receiverInfo.getReceiverName()))
            {
                if (dao == null)
                    dao = FlowReceiverDao.getInstance();
                receiverInfo.setReceiverName(dao.getReceiverName(receiverInfo.getReceiver()));
            }
        }
    }

    public static void initReceiversWithApp(List<NodeReceiverSelect> receivers) throws Exception
    {
        DeptService deptService = null;
        int n = receivers.size();
        for (int i = 0; i < n; i++)
        {
            NodeReceiverSelect receiverSelect = receivers.get(i);
            String receiver = receiverSelect.getReceiver();
            if (receiver != null)
            {
                int index = receiver.indexOf('&');
                if (index > 0)
                {
                    String nodeId = receiverSelect.getNodeId();
                    receivers.remove(i);

                    if (deptService == null)
                        deptService = deptServiceProvider.get();

                    String appId = receiver.substring(index + 1);
                    Integer deptId = Integer.valueOf(receiver.substring(0, index));

                    List<User> users = deptService.getUsersByApp(appId, deptId);
                    for (User user : users)
                    {
                        receivers.add(i++,
                                new NodeReceiverSelect(nodeId, user.getUserId().toString(), user.getUserName()));
                        n++;
                    }

                    i--;
                    n--;
                }
            }
        }
    }

    /**
     * 获得可选的流程列表
     *
     * @param deptId      流程所属的部门ID
     * @param type        流程类型
     * @param authDeptIds 用户有权限查看其流程的部门，为空表示不做权限校验
     * @return 流程列表
     * @throws Exception 数据库查询错误
     */
    public static List<FlowInfo> getSelectableFlowList(Integer deptId, final String type,
                                                       Collection<Integer> authDeptIds)
            throws Exception
    {
        final FlowInfoDao dao = FlowInfoDao.getInstance();

        return deptServiceProvider.get().loadDatasFromParentDepts(deptId, new DeptOwnedDataProvider<FlowInfo>()
        {
            public List<FlowInfo> get(Integer deptId) throws Exception
            {
                return dao.getLastFlowInfos(deptId, type);
            }
        }, authDeptIds, null);
    }

    public static Long getFirstStepId(Long instanceId, SystemFlowDao dao) throws Exception
    {
        return Long.valueOf(dao.getFirstStepId(instanceId.toString()));
    }

    public static Long getLastStepId(Long instanceId, SystemFlowDao dao) throws Exception
    {
        return Long.valueOf(dao.getLastStepId(instanceId.toString()));
    }

    public static Long getLastStepId(Long instanceId) throws Exception
    {
        return getLastStepId(instanceId, DefaultSystemFlowDao.getInstance());
    }

    public static Long getLastStepId(Long instanceId, SystemFlowDao dao, Integer userId) throws Exception
    {
        String instanceIdS = instanceId.toString();
        for (String receiver : getReceivers(userId))
        {
            String stepId = dao.getLastStepIdWithReceiver(instanceIdS, receiver);
            if (stepId != null)
                return Long.valueOf(stepId);
        }

        return null;
    }


    public static Integer getDeptId(FlowInstance instance)
    {
        return instance.getProperty("deptId");
    }

    public static void setDeptId(FlowInstance instance, Integer deptId)
    {
        instance.setProperty("deptId", deptId);
    }

    public static Long getBodyId(FlowInstance instance)
    {
        return instance.getProperty("bodyId");
    }

    public static void setBodyId(FlowInstance instance, Long bodyId)
    {
        instance.setProperty("bodyId", bodyId);
    }

    public static boolean isTest(FlowInstance instance)
    {
        Boolean test = instance.getProperty("test");
        return test != null && test;
    }

    public static void setTest(FlowInstance instance, Boolean test)
    {
        instance.setProperty("test", test);
    }

    public static SystemFormContext getFormContext(Long instanceId, UserInfo userInfo, SystemFlowDao dao)
            throws Exception
    {
        Long bodyId = dao.getBodyId(instanceId);

        if (bodyId == null)
            return null;

        return FormApi.getFormContext(bodyId, userInfo);
    }

    public static SystemFormContext getFormContext(Long instanceId, SystemFlowDao dao) throws Exception
    {
        return getFormContext(instanceId, null, dao);
    }

    public static void sendMessage(String content, List<FlowStep> steps, String pageUrl) throws Exception
    {
        Map<Integer, Long> userIdToStepId = new HashMap<Integer, Long>();

        for (FlowStep step : steps)
        {
            if (step.getState() != FlowStep.ACCEPTED && step.getState() != FlowStep.ACCEPTED_DEALED)
            {
                String receiver = step.getReceiver();

                for (Integer userId : FlowApi.getUserIds(receiver))
                {
                    if (!userIdToStepId.containsKey(userId))
                    {
                        userIdToStepId.put(userId, Long.valueOf(step.getStepId()));
                    }
                }
            }
        }

        for (Map.Entry<Integer, Long> entry : userIdToStepId.entrySet())
        {
            Integer userId = entry.getKey();
            Long stepId = entry.getValue();

            com.gzzm.platform.message.Message message = new com.gzzm.platform.message.Message();
            message.setUserId(userId);
            message.setMessage(content);
            message.setForce(true);
            message.setMethods(ImMessageSender.IM, SmsMessageSender.SMS);

            if (pageUrl != null)
                message.setUrl(pageUrl + "/" + stepId);

            message.send();
        }
    }

    public static void sendMessageToGroup(String content, Long groupId, String pageUrl, SystemFlowDao dao)
            throws Exception
    {
        List<FlowStep> steps = dao.getStepsInGroup(groupId.toString());

        sendMessage(content, steps, pageUrl);
    }

    public static void sendMessageToNoDealSteps(String content, Long instanceId, String pageUrl,
                                                SystemFlowDao dao)
            throws Exception
    {
        List<FlowStep> steps = dao.getNoDealSteps(instanceId.toString());

        sendMessage(content, steps, pageUrl);
    }
}
