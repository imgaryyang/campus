package com.gzzm.platform.timeout;

import com.gzzm.platform.annotation.CacheInstance;
import com.gzzm.platform.organ.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 超时相关的服务
 *
 * @author camel
 * @date 12-8-21
 */
@CacheInstance("Timeout")
public class TimeoutService
{
    @Inject
    private static Provider<DeptService> deptServiceProvider;

    @Inject
    private static Provider<TimeoutDao> daoProvider;

    /**
     * 缓存所有超时配置，第一层的key为typeId，第二层的key为部门ID，值为timeoutConfig对象
     */
    private Map<String, Map<Integer, List<TimeoutConfigInfo>>> timeoutConfigs;

    @Inject
    private List<TimeoutTypeProvider> typeProviders;

    public TimeoutService() throws Exception
    {
        loadTimeoutConfigs();
    }

    public TimeoutDao getDao()
    {
        return daoProvider.get();
    }

    public List<TimeoutTypeProvider> getTypeProviders()
    {
        return typeProviders;
    }

    private List<TimeoutConfigInfo> getTimeoutConfigs0(String typeId, Object obj, Integer deptId) throws Exception
    {
        if (deptId == null)
            return null;

        Map<Integer, List<TimeoutConfigInfo>> configMap = timeoutConfigs.get(typeId);

        if (configMap == null)
            return null;

        List<TimeoutConfigInfo> configs = configMap.get(deptId);

        if (configs == null)
        {
            DeptInfo dept = deptServiceProvider.get().getDept(deptId);

            if (dept == null)
                return null;

            while ((dept = dept.parentDept()) != null)
            {
                configs = configMap.get(dept.getDeptId());

                if (configs != null)
                {
                    break;
                }
            }
        }

        if (configs != null)
        {
            if (obj == null)
                return configs;

            List<TimeoutConfigInfo> result = new ArrayList<TimeoutConfigInfo>(configs.size());
            for (TimeoutConfigInfo config : configs)
            {
                if (config.accept(obj))
                    result.add(config);
            }

            return result;
        }

        return null;
    }

    public TimeoutConfigInfo getTimeoutConfig(String typeId, Object obj, Integer deptId) throws Exception
    {
        List<TimeoutConfigInfo> configs = getTimeoutConfigs0(typeId, obj, deptId);

        if (configs == null)
            return null;

        TimeoutConfigInfo result = null;

        for (TimeoutConfigInfo config : configs)
        {
            if (!StringUtils.isEmpty(config.getCondition()))
                return config;
            else if (result == null)
                result = config;
        }

        return result;
    }

    public TimeoutConfigInfo getTimeoutConfig(String typeId, Integer deptId) throws Exception
    {
        return getTimeoutConfig(typeId, null, deptId);
    }

    public boolean hasTimeoutConfig(String typeId, Collection<Integer> deptIds) throws Exception
    {
        Map<Integer, List<TimeoutConfigInfo>> configMap = timeoutConfigs.get(typeId);

        if (configMap == null)
            return false;

        if (deptIds == null)
            return true;

        for (Integer deptId : deptIds)
        {
            if (getTimeoutConfigs0(typeId, null, deptId) != null)
                return true;
        }

        return false;
    }

    public List<TimeoutConfigInfo> getTimeoutConfigs(String typeId, Collection<Integer> deptIds) throws Exception
    {
        Map<Integer, List<TimeoutConfigInfo>> configMap = timeoutConfigs.get(typeId);

        if (configMap == null)
            return Collections.emptyList();

        List<TimeoutConfigInfo> result = new ArrayList<TimeoutConfigInfo>();

        if (deptIds == null)
        {
            for (List<TimeoutConfigInfo> configs : configMap.values())
            {
                for (TimeoutConfigInfo config : configs)
                {
                    if (!result.contains(config))
                        result.add(config);
                }
            }
        }
        else
        {
            for (Integer deptId : deptIds)
            {
                List<TimeoutConfigInfo> configs = getTimeoutConfigs0(typeId, null, deptId);
                if (configs != null)
                {
                    for (TimeoutConfigInfo config : configs)
                    {
                        if (!result.contains(config))
                            result.add(config);
                    }
                }
            }
        }

        return result;
    }

    @Transactional(mode = TransactionMode.supported)
    public List<TimeoutLevel> getTimeoutLimits(Collection<Integer> deptIds, String... typeIds) throws Exception
    {
        TimeoutDao dao = getDao();
        List<TimeoutLevel> timeoutLevels = new ArrayList<TimeoutLevel>();

        for (String typeId : typeIds)
        {
            for (TimeoutConfigInfo config : getTimeoutConfigs(typeId, deptIds))
            {
                for (Map.Entry<Integer, Integer> entry : config.getDays().entrySet())
                {
                    if (entry.getValue() != null)
                    {
                        Integer levelId = entry.getKey();

                        boolean exists = false;
                        for (TimeoutLevel timeoutLevel : timeoutLevels)
                        {
                            if (timeoutLevel.getLevelId().equals(levelId))
                            {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists)
                        {
                            TimeoutLevel timeoutLevel = dao.getTimeoutLevel(levelId);
                            if (timeoutLevel != null)
                                timeoutLevels.add(timeoutLevel);
                        }
                    }
                }
            }
        }

        Collections.sort(timeoutLevels);

        return timeoutLevels;
    }

    private void loadTimeoutConfigs() throws Exception
    {
        if (timeoutConfigs == null)
        {
            timeoutConfigs = new HashMap<String, Map<Integer, List<TimeoutConfigInfo>>>();

            for (TimeoutConfig config : daoProvider.get().getAllTimeoutConfigs())
            {
                String typeId = config.getTypeId();
                Integer deptId = config.getDeptId();

                Map<Integer, List<TimeoutConfigInfo>> configMap = timeoutConfigs.get(typeId);

                if (configMap == null)
                    timeoutConfigs.put(typeId, configMap = new HashMap<Integer, List<TimeoutConfigInfo>>());

                List<TimeoutConfigInfo> configs = configMap.get(deptId);
                if (configs == null)
                    configMap.put(deptId, configs = new ArrayList<TimeoutConfigInfo>());

                configs.add(new TimeoutConfigInfo(config));
            }
        }
    }

    public TimeoutType getTimeoutType(String typeId) throws Exception
    {
        if ("@root".equals(typeId))
            return TimeoutType.ROOT;

        List<TimeoutTypeProvider> typeProviders = getTypeProviders();
        if (typeProviders != null)
        {
            for (TimeoutTypeProvider provider : typeProviders)
            {
                TimeoutType type = provider.getType(typeId);

                if (type != null)
                    return type;
            }
        }

        return null;
    }

    public boolean check(TimeoutCheck check) throws Exception
    {
        check.setService(this);
        return check.check();
    }
}
