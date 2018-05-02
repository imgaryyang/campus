package com.gzzm.platform.flow;

import com.gzzm.platform.group.Member;
import com.gzzm.platform.organ.DeptInfo;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.*;
import net.cyan.commons.util.imp.Mvel2ScriptEngine;
import net.cyan.valmiki.flow.*;

import java.util.*;

/**
 * @author camel
 * @date 2017/7/23
 */
public final class FlowMvelFunctions
{
    private FlowMvelFunctions()
    {
    }

    public static Object add(Object e1, Object e2)
    {
        if ((e1 instanceof NodeReceiverList) && (e2 instanceof NodeReceiverList))
            return ((NodeReceiverList) e1).add((NodeReceiverList) e2);

        if ((e1 instanceof NodeReceiverList) && e2 == null)
            return e1;

        if ((e2 instanceof NodeReceiverList) && e1 == null)
            return e2;

        if (e1 instanceof Byte && e2 instanceof Byte)
            return ((Byte) e1) + ((Byte) e2);

        if ((e1 instanceof Short || e1 instanceof Byte) && (e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).shortValue() + ((Number) e2).shortValue();

        if ((e1 instanceof Integer || e1 instanceof Short || e1 instanceof Byte) &&
                (e2 instanceof Integer || e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).intValue() + ((Number) e2).intValue();

        if ((e1 instanceof Long || e1 instanceof Integer || e1 instanceof Short || e1 instanceof Byte) &&
                (e2 instanceof Long || e2 instanceof Integer || e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).longValue() + ((Number) e2).longValue();

        if (e1 instanceof Number && !(e1 instanceof Double) && e2 instanceof Number && !(e2 instanceof Double))
            return ((Number) e1).floatValue() + ((Number) e2).floatValue();

        if (e1 instanceof Number && e2 instanceof Number)
            return ((Number) e1).doubleValue() + ((Number) e2).doubleValue();

        if (e1 instanceof Date)
            e1 = dateToString((Date) e1, null);

        if (e2 instanceof Date)
            e2 = dateToString((Date) e2, null);

        return StringUtils.toString(e1) + StringUtils.toString(e2);
    }

    public static Object sub(Object e1, Object e2)
    {
        if ((e1 instanceof NodeReceiverList) && (e2 instanceof NodeReceiverList))
            return ((NodeReceiverList) e1).sub((NodeReceiverList) e2);

        if (e1 instanceof Byte && e2 instanceof Byte)
            return ((Byte) e1) - ((Byte) e2);

        if ((e1 instanceof Short || e1 instanceof Byte) && (e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).shortValue() - ((Number) e2).shortValue();

        if ((e1 instanceof Integer || e1 instanceof Short || e1 instanceof Byte) &&
                (e2 instanceof Integer || e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).intValue() - ((Number) e2).intValue();

        if ((e1 instanceof Long || e1 instanceof Integer || e1 instanceof Short || e1 instanceof Byte) &&
                (e2 instanceof Long || e2 instanceof Integer || e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).longValue() - ((Number) e2).longValue();

        if (e1 instanceof Number && !(e1 instanceof Double) && e2 instanceof Number && !(e2 instanceof Double))
            return ((Number) e1).floatValue() - ((Number) e2).floatValue();

        if (e1 instanceof Number && e2 instanceof Number)
            return ((Number) e1).doubleValue() - ((Number) e2).doubleValue();

        return null;
    }

    public static Object multiply(Object e1, Object e2)
    {
        if ((e1 instanceof NodeReceiverList) && (e2 instanceof NodeReceiverList))
            return ((NodeReceiverList) e1).multiply((NodeReceiverList) e2);

        if (e1 instanceof Byte && e2 instanceof Byte)
            return ((Byte) e1) * ((Byte) e2);

        if ((e1 instanceof Short || e1 instanceof Byte) && (e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).shortValue() * ((Number) e2).shortValue();

        if ((e1 instanceof Integer || e1 instanceof Short || e1 instanceof Byte) &&
                (e2 instanceof Integer || e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).intValue() * ((Number) e2).intValue();

        if ((e1 instanceof Long || e1 instanceof Integer || e1 instanceof Short || e1 instanceof Byte) &&
                (e2 instanceof Long || e2 instanceof Integer || e2 instanceof Short || e2 instanceof Byte))
            return ((Number) e1).longValue() * ((Number) e2).longValue();

        if (e1 instanceof Number && !(e1 instanceof Double) && e2 instanceof Number && !(e2 instanceof Double))
            return ((Number) e1).floatValue() * ((Number) e2).floatValue();

        if (e1 instanceof Number && e2 instanceof Number)
            return ((Number) e1).doubleValue() * ((Number) e2).doubleValue();

        return null;
    }

    public static String dateToString(Date date, String format)
    {
        return DateUtils.toString(date, format == null ? "yyyy年MM月dd日" : format);
    }

    public static String getDeptName(Integer deptId) throws Exception
    {
        DeptInfo dept = FlowFunctions.getDeptInfo(deptId);
        if (dept == null)
            return "";
        else
            return dept.getDeptName();
    }

    public static Integer getParentDeptId(Integer deptId) throws Exception
    {
        DeptInfo dept = FlowFunctions.getDeptInfo(deptId);
        if (dept == null)
            return null;
        else
            return dept.getParentDeptId();
    }

    public static List<Integer> getSubDeptIds(Integer deptId) throws Exception
    {
        if (deptId == 1)
            deptId = (Integer) Mvel2ScriptEngine.getContext().get("businessDeptId");

        return FlowFunctions.getSubDeptIds(deptId);
    }

    private static Integer getUserId(Object obj)
    {
        if (obj instanceof Integer)
        {
            return (Integer) obj;
        }
        else if (obj instanceof Member)
        {
            return ((Member) obj).getId();
        }
        else if (obj instanceof Value)
        {
            return getUserId(((Value) obj).valueOf());
        }
        else
        {
            return getUserId(Integer.valueOf(obj.toString()));
        }
    }

    private static void getUserIds(Object obj, List<Integer> list)
    {
        if (obj instanceof Collection)
        {
            for (Object userId : ((Collection) obj))
            {
                getUserIds(userId, list);
            }
        }
        else
        {
            list.add(getUserId(obj));
        }
    }

    public static NodeReceiverList getUser(Object obj) throws Exception
    {
        if (obj instanceof Collection)
        {
            List<Integer> list = new ArrayList<Integer>();
            getUserIds(obj, list);
            return FlowFunctions.getUser(list);
        }
        else if (obj instanceof Integer[])
        {
            return FlowFunctions.getUser((Integer[]) obj);
        }
        else
        {
            return FlowFunctions.getUser(getUserId(obj));
        }
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getUserInDept(Object deptIds) throws Exception
    {
        if (deptIds instanceof Collection)
            return FlowFunctions.getUserInDept((Collection<Integer>) deptIds);
        else if (deptIds instanceof Integer[])
            return FlowFunctions.getUserInDept((Integer[]) deptIds);
        else if (deptIds instanceof Integer)
            return FlowFunctions.getUserInDept((Integer) deptIds);
        else
            return FlowFunctions.getUserInDept(new Integer[0]);
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getUserOnStation(Object deptIds, Object stationNames) throws Exception
    {
        if (deptIds instanceof Integer)
        {
            if (stationNames instanceof String)
                return FlowFunctions.getUserOnStation((Integer) deptIds, (String) stationNames);
            else if (stationNames instanceof String[])
                return FlowFunctions.getUserOnStation((Integer) deptIds, (String[]) stationNames);
            else
                return FlowFunctions.getUserOnStation((Integer) deptIds, (List<String>) stationNames);
        }
        else if (deptIds instanceof Integer[])
        {
            if (stationNames instanceof String)
                return FlowFunctions.getUserOnStation((Integer[]) deptIds, (String) stationNames);
            else if (stationNames instanceof String[])
                return FlowFunctions.getUserOnStation((Integer[]) deptIds, (String[]) stationNames);
            else
                return FlowFunctions.getUserOnStation((Integer[]) deptIds, (List<String>) stationNames);
        }
        else
        {
            if (stationNames instanceof String)
                return FlowFunctions.getUserOnStation((Collection<Integer>) deptIds, (String) stationNames);
            else if (stationNames instanceof String[])
                return FlowFunctions.getUserOnStation((Collection<Integer>) deptIds, (String[]) stationNames);
            else
                return FlowFunctions.getUserOnStation((Collection<Integer>) deptIds, (List<String>) stationNames);
        }
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getUserWithRole(Object deptIds, Object roleNames) throws Exception
    {
        if (deptIds instanceof Integer)
        {
            if (roleNames instanceof String)
                return FlowFunctions.getUserWithRole((Integer) deptIds, (String) roleNames);
            else if (roleNames instanceof String[])
                return FlowFunctions.getUserWithRole((Integer) deptIds, (String[]) roleNames);
            else
                return FlowFunctions.getUserWithRole((Integer) deptIds, (List<String>) roleNames);
        }
        else if (deptIds instanceof Integer[])
        {
            if (roleNames instanceof String)
                return FlowFunctions.getUserWithRole((Integer[]) deptIds, (String) roleNames);
            else if (roleNames instanceof String[])
                return FlowFunctions.getUserWithRole((Integer[]) deptIds, (String[]) roleNames);
            else
                return FlowFunctions.getUserWithRole((Integer[]) deptIds, (List<String>) roleNames);
        }
        else
        {
            if (roleNames instanceof String)
                return FlowFunctions.getUserWithRole((Collection<Integer>) deptIds, (String) roleNames);
            else if (roleNames instanceof String[])
                return FlowFunctions.getUserWithRole((Collection<Integer>) deptIds, (String[]) roleNames);
            else
                return FlowFunctions.getUserWithRole((Collection<Integer>) deptIds, (List<String>) roleNames);
        }
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getDept(Object deptIds) throws Exception
    {
        if (deptIds instanceof Integer)
            return FlowFunctions.getDept((Integer) deptIds);
        else if (deptIds instanceof Integer[])
            return FlowFunctions.getDept((Integer[]) deptIds);
        else
            return FlowFunctions.getDept((Collection<Integer>) deptIds);
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getStationByName(Integer deptId, Object stationNames) throws Exception
    {
        if (stationNames instanceof String)
            return FlowFunctions.getStationByName(deptId, (String) stationNames);
        else if (stationNames instanceof String[])
            return FlowFunctions.getStationByName(deptId, Arrays.asList((String[]) stationNames));
        else
            return FlowFunctions.getStationByName(deptId, (List<String>) stationNames);
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getApp(Object deptIds, String app) throws Exception
    {
        if (deptIds instanceof Collection)
            return FlowFunctions.getApp((Collection<Integer>) deptIds, app);
        else if (deptIds instanceof Integer[])
            return FlowFunctions.getApp((Integer[]) deptIds, app);
        else if (deptIds instanceof Integer)
            return FlowFunctions.getApp((Integer) deptIds, app);

        return null;
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getUserSelect(Object scopeId) throws Exception
    {
        Map<String, Object> context = Mvel2ScriptEngine.getContext();
        String flowTag = (String) context.get("$flowTag");
        if (scopeId == null)
        {
            if ("approve".equals(flowTag))
                return FlowFunctions.getUserSelect("", (Integer) context.get("bureauId"));
            else
                return FlowFunctions.getUserSelect();
        }
        else if (scopeId instanceof Integer)
        {
            return FlowFunctions.getUserSelect((Integer) scopeId);
        }
        else if ("approve".equals(flowTag))
        {
            return FlowFunctions.getUserSelect((String) scopeId, (Integer) context.get("bureauId"));
        }
        else
        {
            return FlowFunctions.getUserSelect((String) scopeId, (Integer) context.get("businessDeptId"));
        }
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getDeptSelect(Object scopeId, String appId) throws Exception
    {
        if (scopeId == null)
        {
            return FlowFunctions.getDeptSelect(appId);
        }
        else if (scopeId instanceof Integer)
        {
            return FlowFunctions.getDeptSelect((Integer) scopeId, appId);
        }
        else
        {
            Map<String, Object> context = Mvel2ScriptEngine.getContext();
            return FlowFunctions.getDeptSelect((String) scopeId, (Integer) context.get("businessDeptId"), appId);
        }
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getNodeCurrentOperator(Object nodeIds) throws Exception
    {
        Map<String, Object> context = Mvel2ScriptEngine.getContext();
        ScriptContext scriptContext = (ScriptContext) context.get("$context");
        if (nodeIds instanceof String)
            return scriptContext.getNodeCurrentOperator((String) nodeIds);

        NodeReceiverList r = NodeReceiverList.EMPTYRECEIVERLIST;

        Collection<String> list = null;
        if (nodeIds instanceof String[])
            list = Arrays.asList((String[]) nodeIds);
        else if (nodeIds instanceof Collection)
            list = (Collection<String>) nodeIds;

        if (list != null)
        {
            for (String nodeId : list)
            {
                r = r.add(scriptContext.getNodeCurrentOperator(nodeId));
            }
        }

        return r;
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getNodeLastOperator(Object nodeIds) throws Exception
    {
        Map<String, Object> context = Mvel2ScriptEngine.getContext();
        ScriptContext scriptContext = (ScriptContext) context.get("$context");
        if (nodeIds instanceof String)
            return scriptContext.getNodeLastOperator((String) nodeIds);

        NodeReceiverList r = NodeReceiverList.EMPTYRECEIVERLIST;

        Collection<String> list = null;
        if (nodeIds instanceof String[])
            list = Arrays.asList((String[]) nodeIds);
        else if (nodeIds instanceof Collection)
            list = (Collection<String>) nodeIds;

        if (list != null)
        {
            for (String nodeId : list)
            {
                r = r.add(scriptContext.getNodeLastOperator(nodeId));
            }
        }

        return r;
    }

    @SuppressWarnings("unchecked")
    public static NodeReceiverList getNodeOperator(Object nodeIds) throws Exception
    {
        Map<String, Object> context = Mvel2ScriptEngine.getContext();
        ScriptContext scriptContext = (ScriptContext) context.get("$context");
        if (nodeIds instanceof String)
            return scriptContext.getNodeOperator((String) nodeIds);

        NodeReceiverList r = NodeReceiverList.EMPTYRECEIVERLIST;

        Collection<String> list = null;
        if (nodeIds instanceof String[])
            list = Arrays.asList((String[]) nodeIds);
        else if (nodeIds instanceof Collection)
            list = (Collection<String>) nodeIds;

        if (list != null)
        {
            for (String nodeId : list)
            {
                r = r.add(scriptContext.getNodeOperator(nodeId));
            }
        }

        return r;
    }

    public static String getNodeLastOperatorId(String nodeId) throws Exception
    {
        Map<String, Object> context = Mvel2ScriptEngine.getContext();
        ScriptContext scriptContext = (ScriptContext) context.get("$context");
        FlowStep step = scriptContext.getLastStep(nodeId);

        if (step == null)
            return "";

        return step.getReceiver();
    }

    public static Integer getNodeLastOperatorDeptId(String nodeId) throws Exception
    {
        Map<String, Object> context = Mvel2ScriptEngine.getContext();
        ScriptContext scriptContext = (ScriptContext) context.get("$context");
        return FlowFunctions.getNodeLastOperatorDeptId(nodeId, scriptContext.getService());
    }

    @SuppressWarnings("unchecked")
    public static boolean isOnStation(Integer userId, Integer deptId, Object stationNames) throws Exception
    {
        if (stationNames instanceof String)
            return FlowFunctions.isOnStation(userId, deptId, (String) stationNames);
        else if (stationNames instanceof String[])
            return FlowFunctions.isOnStation(userId, deptId, (String[]) stationNames);
        else
            return FlowFunctions.isOnStation(userId, deptId, (Collection<String>) stationNames);
    }

    public static void sendMessageToNode(String content, String nodeId) throws Exception
    {
        Map<String, Object> context = Mvel2ScriptEngine.getContext();
        ScriptContext scriptContext = (ScriptContext) context.get("$context");
        FlowFunctions.sendMessageToNode(content, nodeId, scriptContext);
    }

    public static void importJs(String path)
    {
        ((FlowPage) RequestContext.getContext().getForm()).importJs(path);
    }
}
