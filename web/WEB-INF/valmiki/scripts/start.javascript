var isNashorn = __$$engine$$__.indexOf("Nashorn") > 0;

Valmiki = {
    add: function (e1, e2)
    {
        if ((e1 instanceof NodeReceiverList) && (e2 instanceof NodeReceiverList))
            return e1.add(e2);

        if ((e1 instanceof NodeReceiverList) && e2 == null)
            return e1;

        if ((e2 instanceof NodeReceiverList) && e1 == null)
            return e2;

        if (e1 instanceof Date)
            e1 = dateToString(e1, null);

        if (e2 instanceof Date)
            e2 = dateToString(e2, null);

        return e1 + e2;
    },
    sub: function (e1, e2)
    {
        if ((e1 instanceof NodeReceiverList) && (e2 instanceof NodeReceiverList))
            return e1.sub(e2);
        else
            return e1 - e2;
    },
    multiply: function (e1, e2)
    {
        if ((e1 instanceof NodeReceiverList) && (e2 instanceof NodeReceiverList))
            return e1.multiply(e2);
        else
            return e1 * e2;
    },
    getIds: function (items)
    {
        var n = items.size();
        var ids = [];
        for (var i = 0; i < n; i++)
        {
            ids[i] = items.get(i).getId();
        }

        return ids;
    },
    dateToString: function (date, format)
    {
        return DateUtils.toString(date, format == null ? "yyyy年MM月dd日" : format);
    },
    date: function (date)
    {
        return DateUtils.getDate(date);
    },
    day: function (date)
    {
        return DateUtils.getDay(date);
    },
    year: function (date)
    {
        return DateUtils.getYear(date);
    },
    month: function (date)
    {
        return DateUtils.getMonth(date);
    },
    hours: function (date)
    {
        return DateUtils.getHour(date);
    },
    minutes: function (date)
    {
        return DateUtils.getMinute(date);
    },
    seconds: function (date)
    {
        return DateUtils.getSecond(date);
    },
    incDate: function (date, n)
    {
        return DateUtils.addDate(date, n);
    },
    getDeptName: function (deptId)
    {
        var dept = FlowFunctions.getDeptInfo(deptId);
        if (dept == null)
            return "";
        else
            return dept.getDeptName();
    },
    getParentDeptId: function (deptId)
    {
        var dept = FlowFunctions.getDeptInfo(deptId);
        if (dept == null)
            return null;
        else
            return dept.getParentDeptId();
    },
    getSubDeptIdByName: function (deptId, deptNames)
    {
        return FlowFunctions.getSubDeptIdByName(deptId, deptNames);
    },
    getSubDeptIds: function (deptId)
    {
        if (deptId == 1)
            deptId = businessDeptId;
        return FlowFunctions.getSubDeptIds(deptId);
    },
    getDeptAttribute: function (deptId, attributeName, defaultValue)
    {
        return FlowFunctions.getDeptAttribute(deptId, attributeName, defaultValue);
    },
    getDeptAttributeValues: function (deptId, attributeName, defaultValues)
    {
        return FlowFunctions.getDeptAttributeValues(deptId, attributeName, defaultValues);
    },
    getDeptsAttribute: function (deptIds, attributeName, defaultValue)
    {
        return FlowFunctions.getDeptsAttribute(deptIds, attributeName, defaultValue);
    },
    getUser: function (userIds)
    {
        if (userIds instanceof Collection)
        {
            var list = new ArrayList();
            for (var i = userIds.iterator(); i.hasNext();)
            {
                var userId = i.next();
                if (userId instanceof Integer)
                {
                    list.add(userId);
                }
                else if (typeof userId == "number" || userId instanceof Number)
                {
                    list.add(Integer.valueOf(userId));
                }
                else if (userId instanceof com.gzzm.platform.group.Member)
                {
                    list.add(userId.id);
                }
                else if (userId instanceof Value)
                {
                    userId = userId.valueOf();
                    if (userId instanceof Integer)
                        list.add(userId);
                    if (typeof userId == "number" || userId instanceof Number)
                        list.add(Integer.valueOf(userId));
                    else
                        list.add(Integer.valueOf(userId.toString()));
                }
                else if (userId instanceof Collection)
                {
                    for (var i2 = userId.iterator(); i2.hasNext();)
                    {
                        var userId2 = i2.next();
                        if (userId2 instanceof Integer)
                        {
                            list.add(userId2);
                        }
                        else if (typeof userId2 == "number" || userId2 instanceof Number)
                        {
                            list.add(Integer.valueOf(userId2));
                        }
                        else if (userId2 instanceof com.gzzm.platform.group.Member)
                        {
                            list.add(userId2.id);
                        }
                        else if (userId2 instanceof Value)
                        {
                            userId2 = userId2.valueOf();
                            if (userId2 instanceof Integer)
                                list.add(userId2);
                            if (typeof userId2 == "number" || userId2 instanceof Number)
                                list.add(Integer.valueOf(userId2));
                            else
                                list.add(Integer.valueOf(userId2.toString()));
                        }
                        else
                        {
                            list.add(Integer.valueOf(userId.toString()));
                        }
                    }
                }
                else
                {
                    list.add(Integer.valueOf(userId.toString()));
                }

            }
            return FlowFunctions.getUser(list);
        }
        else if (userIds instanceof com.gzzm.platform.group.Member)
        {
            return FlowFunctions.getUser(userIds.id);
        }
        else if (userIds instanceof Value)
        {
            return FlowFunctions.getUser(userIds.valueOf());
        }
        else if (userIds instanceof Array)
        {
            return FlowFunctions.getUser(userIds);
        }
        else if (userIds instanceof Integer)
        {
            return FlowFunctions.getUser(userIds);
        }
        else
        {
            return FlowFunctions.getUser(Integer.valueOf(userIds.toString()));
        }
    },
    getUserInDept: function (deptIds)
    {
        if (arguments.length > 1)
            deptIds = arguments;

        return FlowFunctions.getUserInDept(deptIds);
    },
    getUserOnStation: function (deptIds, stationNames)
    {
        if (deptIds instanceof Integer)
            deptIds = Collections.singletonList(deptIds);
        else if (deptIds instanceof Number || typeof deptIds == "number")
            deptIds = Collections.singletonList(Integer.valueOf(deptIds));
        else if (deptIds instanceof Array)
            deptIds = Arrays.asList(deptIds);
        else if (deptIds == null)
            deptIds = Collections.emptyList();

        if (arguments.length > 2)
            stationNames = Arrays.asList(arguments.slice(1));
        else if (stationNames instanceof String)
            stationNames = Collections.singletonList(stationNames);
        else if (stationNames instanceof Array)
            stationNames = Arrays.asList(stationNames);

        return FlowFunctions.getUserOnStation(deptIds, stationNames);
    },
    getUserWithRole: function (deptIds, roleNames)
    {
        if (deptIds instanceof Integer)
            deptIds = Collections.singletonList(deptIds);
        else if (deptIds instanceof Number || typeof deptIds == "number")
            deptIds = Collections.singletonList(Integer.valueOf(deptIds));
        else if (deptIds instanceof Array)
            deptIds = Arrays.asList(deptIds);

        if (arguments.length > 2)
            roleNames = Arrays.asList(arguments.slice(1));
        else if (roleNames instanceof String)
            roleNames = Collections.singletonList(roleNames);
        else if (roleNames instanceof Array)
            roleNames = Arrays.asList(roleNames);

        return FlowFunctions.getUserWithRole(deptIds, roleNames);
    },
    getDept: function (deptIds)
    {
        if (deptIds instanceof Integer)
            deptIds = Collections.singletonList(deptIds);
        else if (deptIds instanceof Number || typeof deptIds == "number")
            deptIds = Collections.singletonList(Integer.valueOf(deptIds));
        else if (deptIds instanceof Array)
            deptIds = Arrays.asList(deptIds);

        return FlowFunctions.getDept(deptIds);
    },
    getStationById: function (deptId, stationId)
    {
        return FlowFunctions.getStationById(deptId, stationId);
    },
    getStationByName: function (deptId, stationNames)
    {
        if (arguments.length > 2)
            stationNames = Arrays.asList(arguments.slice(1));
        else if (stationNames instanceof String)
            stationNames = Collections.singletonList(stationNames);
        else if (stationNames instanceof Array)
            stationNames = Arrays.asList(stationNames);

        return FlowFunctions.getStationByName(deptId, stationNames);
    },
    getApp: function (deptIds, app)
    {
        return FlowFunctions.getApp(deptIds, app);
    },
    getUserSelect: function (scopeId)
    {
        if (scopeId == null)
        {
            if ($flowTag == "approve")
                return FlowFunctions.getUserSelect("", bureauId);
            else
                return FlowFunctions.getUserSelect();
        }
        else if (scopeId instanceof Integer || typeof scopeId == "number" || scopeId instanceof Number)
        {
            return FlowFunctions.getUserSelect(scopeId);
        }
        else if ($flowTag == "approve")
        {
            return FlowFunctions.getUserSelect(scopeId, bureauId);
        }
        else
        {
            return FlowFunctions.getUserSelect(scopeId, businessDeptId);
        }
    },
    getDeptSelect: function (scopeId, appId)
    {
        if (scopeId == null)
            return FlowFunctions.getDeptSelect(appId);
        else if (scopeId instanceof Integer || typeof scopeId == "number" || scopeId instanceof Number)
            return FlowFunctions.getDeptSelect(scopeId, appId);
        else
            return FlowFunctions.getDeptSelect(scopeId, businessDeptId, appId);
    },
    getUserSelectWithDept: function (deptId)
    {
        return FlowFunctions.getUserSelectWithDept(deptId);
    },
    getNodeCurrentOperator: function (nodeIds)
    {
        if (arguments.length > 1)
            nodeIds = arguments;

        if (nodeIds instanceof String)
            return $context.getNodeCurrentOperator(nodeIds);

        var r = $empty;
        for (var i = 0; i < nodeIds.length; i++)
        {
            r = r.add($context.getNodeCurrentOperator(nodeIds[i]));
        }

        return r;
    },
    getNodeLastOperator: function (nodeIds)
    {
        if (arguments.length > 1)
            nodeIds = arguments;

        if (nodeIds instanceof String)
            return $context.getNodeLastOperator(nodeIds);

        var r = $empty;
        for (var i = 0; i < nodeIds.length; i++)
        {
            r = r.add($context.getNodeLastOperator(nodeIds[i]));
        }

        return r;
    },
    getNodeOperator: function (nodeIds)
    {
        if (arguments.length > 1)
            nodeIds = arguments;

        if (nodeIds instanceof String)
            return $context.getNodeOperator(nodeIds);

        var r = $empty;
        for (var i = 0; i < nodeIds.length; i++)
        {
            r = r.add($context.getNodeOperator(nodeIds[i]));
        }

        return r;
    },
    getNodeLastOperatorId: function (nodeId)
    {
        step = $context.getLastStep(nodeId);

        if (step == null)
            return "";

        return step.getReceiver();
    },
    getNodeLastOperatorDeptId: function (nodeId)
    {
        var step = $context.getLastStep(nodeId);

        if (step == null)
            return "";

        return step.getProperty("deptId");
    },
    isOnStation: function (userId, deptId, stationNames)
    {
        if (arguments.length > 3)
            stationNames = Arrays.asList(arguments.slice(1));
        else if (stationNames instanceof String)
            stationNames = Collections.singletonList(stationNames);
        else if (stationNames instanceof Array)
            stationNames = Arrays.asList(stationNames);

        return FlowFunctions.isOnStation(userId, deptId, stationNames);
    },
    sendSmsToPhone: function (content, phone)
    {
        FlowFunctions.sendSmsToPhone(content, phone);
    },
    sendMessageToUser: function (content, userId)
    {
        FlowFunctions.sendMessageToUser(content, userId);
    },
    sendMessageToNode: function (content, nodeId)
    {
        FlowFunctions.sendMessageToNode(content, nodeId, $context);
    }
};

function importJs(path)
{
    if (path)
    {
        var RequestContext;
        if (isNashorn)
            RequestContext = Java.type("net.cyan.arachne.RequestContext");
        else
            RequestContext = Packages.net.cyan.arachne.RequestContext;

        RequestContext.getContext().getForm().importJs(path);
    }
}

function onload(callback)
{
    if (callback)
    {
        var RequestContext;
        if (isNashorn)
            RequestContext = Java.type("net.cyan.arachne.RequestContext");
        else
            RequestContext = Packages.net.cyan.arachne.RequestContext;

        RequestContext.getContext().getForm().importJs(executeS(callback));
    }
}