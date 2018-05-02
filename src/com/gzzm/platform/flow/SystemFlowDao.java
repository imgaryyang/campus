package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.compile.Compile;
import net.cyan.thunwind.PersistenceManager;
import net.cyan.thunwind.annotation.*;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.flow.dao.*;

import java.util.*;

/**
 * @author camel
 * @date 2010-9-18
 */
public abstract class SystemFlowDao extends BaseFlowDao
{
    private final static Map<String, Class<? extends SystemFlowInstance>> INSTANCE_CLASS_MAP =
            new HashMap<String, Class<? extends SystemFlowInstance>>();

    private final static Map<String, Class<? extends SystemFlowStep>> STEP_CLASS_MAP =
            new HashMap<String, Class<? extends SystemFlowStep>>();

    public static SystemFlowDao getInstance(Class<? extends SystemFlowInstance> instanceClass,
                                            Class<? extends SystemFlowStep> stepClass) throws Exception
    {
        if (instanceClass == null)
            throw new NullPointerException();

        if (stepClass == null)
            throw new NullPointerException();

        SystemFlowDao dao = Tools.getBean(SystemFlowDao.class);
        dao.instanceClass = instanceClass;
        dao.stepClass = stepClass;

        return dao;
    }

    public static SystemFlowDao getInstance(String entityTable, String stepTable) throws Exception
    {
        return getInstance(createInstanceClass(entityTable), createStepClass(stepTable));
    }

    public static SystemFlowDao getInstance() throws Exception
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    private synchronized static Class<? extends SystemFlowInstance> createInstanceClass(String table) throws Exception
    {
        Class<? extends SystemFlowInstance> c = INSTANCE_CLASS_MAP.get(table);
        if (c == null)
        {
            String className = "com.gzzm.platform.flow." + table;

            String s = "package com.gzzm.platform.flow;\n" +
                    "import net.cyan.thunwind.annotation.*;\n" +
                    "@Entity(table = \"" + table + "\", keys = \"instanceId\")\n" +
                    "@Optimized(false)\n" +
                    "public class " + table + " extends SystemFlowInstance\n{}";

            c = Compile.compile(s, className, false);

            PersistenceManager.getManager("").addEntityClass(c);

            INSTANCE_CLASS_MAP.put(table, c);
        }

        return c;
    }

    @SuppressWarnings("unchecked")
    private synchronized static Class<? extends SystemFlowStep> createStepClass(String table) throws Exception
    {
        Class<? extends SystemFlowStep> c = STEP_CLASS_MAP.get(table);
        if (c == null)
        {
            String className = "com.gzzm.platform.flow." + table;

            String s = "package com.gzzm.platform.flow;\n" +
                    "import net.cyan.thunwind.annotation.*;\n" +
                    "@Entity(table = \"" + table + "\", keys = {\"stepId\", \"preStepId\"})\n" +
                    "@Optimized(false)\n" +
                    "public class " + table + " extends SystemFlowStep\n{}";

            c = Compile.compile(s, className, false);


            PersistenceManager.getManager("").addEntityClass(c);

            STEP_CLASS_MAP.put(table, c);
        }

        return c;
    }

    public SystemFlowDao()
    {
        this(null, null);
    }

    protected SystemFlowDao(Class<? extends SystemFlowInstance> instanceClass,
                            Class<? extends SystemFlowStep> stepClass)
    {
        super(instanceClass, stepClass, SystemFlowContext.class);
    }

    @Override
    @Transactional(mode = TransactionMode.none)
    @SuppressWarnings("unchecked")
    public Class<? extends SystemFlowInstance> getInstanceClass() throws Exception
    {
        return (Class<? extends SystemFlowInstance>) super.getInstanceClass();
    }

    @Override
    @Transactional(mode = TransactionMode.none)
    @SuppressWarnings("unchecked")
    public Class<? extends SystemFlowStep> getStepClass() throws Exception
    {
        return (Class<? extends SystemFlowStep>) super.getStepClass();
    }

    @Transactional(mode = TransactionMode.none)
    public SystemFlowInstance newFlowInstance() throws Exception
    {
        return (SystemFlowInstance) super.newFlowInstance();
    }

    @Transactional(mode = TransactionMode.none)
    public SystemFlowStep newFlowStep() throws Exception
    {
        return (SystemFlowStep) super.newFlowStep();
    }

    public SystemFlowInstance getFlowInstance(Long instanceId) throws Exception
    {
        return load(getInstanceClass(), instanceId);
    }

    public SystemFlowStep getFlowStep(Long stepId) throws Exception
    {
        return oqlQueryFirst("select s from " + getStepClass().getName() + " s where s.stepId=: order by s.receiveTime",
                stepId);
    }

    public void lockFlowInstance(Long instanceId) throws Exception
    {
        lock(getInstanceClass(), instanceId);
    }

    public void lockFlowInstanceByStepId(Long stepId) throws Exception
    {
        SystemFlowStep flowStep = getFlowStep(stepId);
        if (flowStep != null)
            lock(getInstanceClass(), flowStep.getInstanceId());
    }

    public Long getBodyId(Long instanceId) throws Exception
    {
        return oqlQueryFirst("select bodyId from " + getInstanceClass().getName() + " where instanceId=:1", instanceId);
    }

    @Override
    protected void stepToEntity(FlowStep step, FlowStepEntity stepEntity) throws Exception
    {
        super.stepToEntity(step, stepEntity);

        SystemFlowStep systemFlowStep = (SystemFlowStep) stepEntity;
        systemFlowStep.setDealer(step.<Integer>getProperty("dealer"));
        Object deptId = step.getProperty("deptId");
        if (deptId != null)
        {
            systemFlowStep.setDeptId(DataConvert.convertType(Integer.class, deptId));
        }
        else if (systemFlowStep.getDeptId() == null)
        {
            String receiver = step.getReceiver();
            if (receiver != null)
            {
                if (!receiver.contains("@") && !receiver.contains("$") && !receiver.contains("#"))
                {
                    try
                    {
                        Integer userId = Integer.valueOf(receiver);
                        User user = getUser(userId);
                        if (user != null)
                        {
                            for (Dept dept : user.getDepts())
                            {
                                if (dept.getDeptId() != 1)
                                {
                                    systemFlowStep.setDeptId(dept.getDeptId());
                                    break;
                                }
                            }

                            if (systemFlowStep.getDeptId() == null && user.getDepts().size() > 0)
                                systemFlowStep.setDeptId(user.getDepts().get(0).getDeptId());
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        //接收者不是用户id
                    }
                }
            }
        }
        systemFlowStep.setConsignationId(step.<Integer>getProperty("consignationId"));
    }

    @Override
    protected void entityToStep(FlowStepEntity stepEntity, FlowStep step) throws Exception
    {
        super.entityToStep(stepEntity, step);

        SystemFlowStep systemFlowStep = (SystemFlowStep) stepEntity;

        Integer dealer = systemFlowStep.getDealer();
        if (dealer == null)
        {
            try
            {
                dealer = Integer.valueOf(systemFlowStep.getReceiver());
            }
            catch (NumberFormatException ex)
            {
                //不是一个用户id
            }
        }
        if (dealer != null)
        {
            step.setProperty("dealer", dealer);
            if (StringUtils.isEmpty(step.getDealerName()))
                step.setDealerName(getUserName(dealer));
        }

        Integer deptId = systemFlowStep.getDeptId();
        step.setProperty("deptId", deptId);
        step.setProperty("consignationId", systemFlowStep.getConsignationId());

        if (deptId != null)
        {
            if (step.getProperty("deptName") == null)
            {
                step.setProperty("deptName", getDeptName(deptId));
            }
        }
    }

    @Override
    protected void entityToInstance(FlowInstanceEntity instanceEntity, FlowInstance instance) throws Exception
    {
        super.entityToInstance(instanceEntity, instance);

        SystemFlowInstance systemFlowInstance = (SystemFlowInstance) instanceEntity;
        FlowApi.setDeptId(instance, systemFlowInstance.getDeptId());
        FlowApi.setBodyId(instance, systemFlowInstance.getBodyId());
        FlowApi.setTest(instance, systemFlowInstance.isTest());
    }

    @Override
    protected void instanceToEntity(FlowInstance instance, FlowInstanceEntity instanceEntity) throws Exception
    {
        super.instanceToEntity(instance, instanceEntity);

        SystemFlowInstance systemFlowInstance = (SystemFlowInstance) instanceEntity;
        systemFlowInstance.setDeptId(FlowApi.getDeptId(instance));
        systemFlowInstance.setBodyId(FlowApi.getBodyId(instance));
        systemFlowInstance.setTest(FlowApi.isTest(instance));
    }

    public String createStepId() throws Exception
    {
        return getId("PFFLOWSTEPID", 13, Long.class).toString();
    }

    public String createInstanceId() throws Exception
    {
        return getId("PFFLOWINSTANCEID", 13, Long.class).toString();
    }

    public String createStepGroupId() throws Exception
    {
        return getId("PFFLOWSTEPGROUPID", 13, Long.class).toString();
    }

    /**
     * 获得某个步骤的打回信息
     *
     * @param stepId 步骤ID
     * @return 打回信息列表
     * @throws Exception 数据库错误
     */
    @OQL("select b from FlowStepBack b where stepId=:1 order by backTime")
    public abstract List<FlowStepBack> getBacks(Long stepId) throws Exception;

    @Override
    public void deleteInstance(String instanceId) throws Exception
    {
        Long instanceId1 = Long.valueOf(instanceId);

        if (getInstanceClass() != FlowInstanceBak.class)
        {
            SystemFlowInstance instance = getFlowInstance(instanceId1);

            if (instance == null)
                return;

            FlowInstanceBak instanceBak = new FlowInstanceBak();
            copyInstance(instance, instanceBak);

            instanceBak.setDao(BeanUtils.getRealClass(getClass()).getName());
            instanceBak.setDeleteTime(new Date());

            save(instanceBak);

            for (SystemFlowStep step : getSteps(instanceId1))
            {
                FlowStepBak stepBak = new FlowStepBak();
                copyStep(step, stepBak);

                save(stepBak);
            }
        }

        super.deleteInstance(instanceId);
    }

    protected void copyInstance(SystemFlowInstance instance1, SystemFlowInstance instance2)
    {
        instance2.setInstanceId(instance1.getInstanceId());
        instance2.setFlowId(instance1.getFlowId());
        instance2.setStartTime(instance1.getStartTime());
        instance2.setEndTime(instance1.getEndTime());
        instance2.setState(instance1.getState());
        instance2.setFlowTag(instance1.getFlowTag());
        instance2.setTitle(instance1.getTitle());
        instance2.setMainStepId(instance1.getMainStepId());
        instance2.setCreator(instance1.getCreator());
        instance2.setDeptId(instance1.getDeptId());
        instance2.setBodyId(instance1.getBodyId());
        instance2.setTest(instance1.isTest());
        instance2.setYear(instance1.getYear());
    }

    protected void copyStep(SystemFlowStep step1, SystemFlowStep step2)
    {
        step2.setStepId(step1.getStepId());
        step2.setGroupId(step1.getGroupId());
        step2.setInstanceId(step1.getInstanceId());
        step2.setNodeId(step1.getNodeId());
        step2.setNodeName(step1.getNodeName());
        step2.setPreStepId(step1.getPreStepId());
        step2.setTopStepId(step1.getTopStepId());
        step2.setReceiver(step1.getReceiver());
        step2.setSourceName(step1.getSourceName());
        step2.setState(step1.getState());
        step2.setReceiveTime(step1.getReceiveTime());
        step2.setAcceptTime(step1.getAcceptTime());
        step2.setDisposeTime(step1.getDisposeTime());
        step2.setShowTime(step1.getShowTime());
        step2.setWaitForGroup(step1.getWaitForGroup());
        step2.setLastStep(step1.isLastStep());
        step2.setDeptId(step1.getDeptId());
        step2.setDealer(step1.getDealer());
        step2.setConsignationId(step1.getConsignationId());
        step2.setHidden(step1.getHidden());
        step2.setYear(step1.getYear());
    }

    public List<SystemFlowStep> getSteps(Long instanceId) throws Exception
    {
        return oqlQuery("select s from " + getStepClass().getName() + " s where instanceId=:1", instanceId);
    }

    public void makeDeptLast(Long instanceId) throws Exception
    {
        String className = getStepClass().getName();
        List<Long> maxStepIds = oqlQuery("select max(stepId) from " + className +
                " s where instanceId=:1 and state in (0,2,3,6,7,15,16,17) group by deptId", instanceId);

        if (maxStepIds.size() > 0)
        {
            executeOql("update " + className + " s set deptLast=1 where instanceId=:1 and stepId in :2", instanceId,
                    maxStepIds);
            executeOql("update " + className + " s set deptLast=0 where instanceId=:1 and stepId not in :2", instanceId,
                    maxStepIds);
        }
        else
        {
            executeOql("update " + className + " s set deptLast=0 where instanceId=:1", instanceId);
        }
    }

    public void setStepRead(Long stepId) throws Exception
    {
        executeOql("update " + getStepClass().getName() + " set read=1 where stepId=:1", stepId);
    }

    public User getUser(Integer userId) throws Exception
    {
        return load(User.class, userId);
    }

    protected String getUserName(Integer userId) throws Exception
    {
        User user = getUser(userId);

        if (user == null)
            return null;
        else
            return user.getUserName();
    }

    protected String getDeptName(Integer deptId) throws Exception
    {
        Dept dept = load(Dept.class, deptId);

        if (dept == null)
            return null;
        else
            return dept.getDeptName();
    }

    protected String getStationName(Integer stationId) throws Exception
    {
        Station station = load(Station.class, stationId);

        if (station == null)
            return null;
        else
            return station.getStationName();
    }

    @Override
    public String getReceiverName(String receiver) throws Exception
    {
        if (StringUtils.isEmpty(receiver))
            return "";

        if (receiver.startsWith("$"))
        {
            return receiver.substring(1);
        }

        int index = receiver.indexOf('@');

        if (index == 0)
        {
            return getDeptName(Integer.valueOf(receiver.substring(1)));
        }
        else if (index > 0)
        {
            return getStationName(Integer.valueOf(receiver.substring(0, index)));
        }
        else
        {
            index = receiver.indexOf('#');

            if (index > 0)
                return receiver.substring(0, index);

            return getUserName(Integer.valueOf(receiver));
        }
    }

    @GetByField({"stepId", "userId", "content"})
    public abstract FlowStepMessage getMessage(Long stepId, Integer userId, String content) throws Exception;

    @OQLUpdate("update FlowStepMessage set readed=1 where instanceId=:1 and userId=:2 and readed=0")
    public abstract void setMessageReaded(Long instanceId, Integer userId) throws Exception;

    @OQLUpdate("update FlowStepMessage set readed=1 where messageId in :1 and readed=0")
    public abstract void setMessageReaded(Long[] messageIds);

    public void recover(Long instanceId) throws Exception
    {
    }
}
