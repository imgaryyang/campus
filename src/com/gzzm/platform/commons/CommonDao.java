package com.gzzm.platform.commons;

import net.cyan.commons.transaction.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.Date;

/**
 * updatetime的数据访问对象
 *
 * @author camel
 * @date 2009-7-20
 */
public abstract class CommonDao extends GeneralDao
{
    public static CommonDao getInstance() throws Exception
    {
        return Tools.getBean(CommonDao.class);
    }

    public CommonDao()
    {
    }

    /**
     * 获得某项数据的更新时间，如果没有更新过返回null
     *
     * @param name 数据名称
     * @return 数据的更新时间
     * @throws Exception 数据库异常
     */
    @OQL("select u.lastTime from UpdateTime u where u.name=:1")
    public abstract Date getLastTime(String name) throws Exception;

    /**
     * 更新某项数据的更新时间
     *
     * @param name     数据名称
     * @param lastTime 数据的最后更新时间
     * @throws Exception 数据库异常
     */
    @Transactional(mode = TransactionMode.not_supported)
    public void updateLastTime(String name, Date lastTime) throws Exception
    {
        UpdateTime updateTime = new UpdateTime();
        updateTime.setName(name);
        updateTime.setLastTime(lastTime);
        save(updateTime);
    }

    /**
     * 后的某个配置项的值
     *
     * @param name 配置项名称
     * @return 值
     * @throws Exception 数据库异常
     */
    @OQL("select c.configValue from Config c where c.configName=:1")
    public abstract String getConfig(String name) throws Exception;

    /**
     * 更新某个配置项的值
     *
     * @param name  配置项名称
     * @param value 值
     * @throws Exception 数据库异常
     */
    public void setConfig(String name, String value) throws Exception
    {
        Config config = new Config();
        config.setConfigName(name);
        config.setConfigValue(value);
        save(config);
    }

    public String getConfig(String name, String defaultValue) throws Exception
    {
        String value = getConfig(name);
        if (value == null && !StringUtils.isEmpty(defaultValue))
        {
            setConfig(name, defaultValue);
            return defaultValue;
        }
        else
        {
            return value;
        }
    }
}
