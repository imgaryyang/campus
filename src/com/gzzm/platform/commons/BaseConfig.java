package com.gzzm.platform.commons;

import net.cyan.nest.annotation.Injectable;

import java.util.*;

/**
 * 系统配置，配置一些系统几乎不变的信息，当此配置发生变化时往往意味着底层功能的变化
 *
 * @author camel
 * @date 2009-7-20
 */
@Injectable(singleton = true)
public class BaseConfig
{
    /**
     * 部门级别名称
     */
    private List<String> deptLevels;

    /**
     * 部门级别和名称的映射关系
     */
    private Map<String, String> deptLevelMap;

    /**
     * 部门级别和名称的映射关系，并且在最前面加上一个空的级别-1
     */
    private Map<String, String> deptLevelMapWithEmpty;

    public BaseConfig()
    {
    }

    public List<String> getDeptLevels()
    {
        return deptLevels;
    }

    public void setDeptLevels(List<String> deptLevels)
    {
        this.deptLevels = deptLevels;
    }

    public synchronized Map<String, String> getDeptLevelMapWithEmpty()
    {
        if (deptLevelMapWithEmpty == null)
        {
            if (deptLevels == null)
            {
                deptLevelMapWithEmpty = Collections.emptyMap();
            }
            else
            {
                int n = deptLevels.size();
                deptLevelMapWithEmpty = new LinkedHashMap<String, String>(n + 1);
                deptLevelMapWithEmpty.put(Integer.toString(-1), "其它");
                for (int i = 0; i < n; i++)
                    deptLevelMapWithEmpty.put(Integer.toString(i), deptLevels.get(i));
            }
        }

        return deptLevelMapWithEmpty;
    }

    public Map<String, String> getDeptLevelMap()
    {
        if (deptLevelMap == null)
        {
            if (deptLevels == null)
            {
                deptLevelMap = Collections.emptyMap();
            }
            else
            {
                int n = deptLevels.size();
                deptLevelMap = new LinkedHashMap<String, String>(n);
                for (int i = 0; i < n; i++)
                    deptLevelMap.put(Integer.toString(i), deptLevels.get(i));
            }
        }

        return deptLevelMap;
    }
}
