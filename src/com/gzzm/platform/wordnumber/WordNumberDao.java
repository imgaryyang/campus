package com.gzzm.platform.wordnumber;

import net.cyan.commons.transaction.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 11-10-27
 */
public abstract class WordNumberDao extends GeneralDao
{
    public WordNumberDao()
    {
    }

    @GetByField({"type", "deptId"})
    public abstract WordNumberConfig getWordNumberConfig(String type, Integer deptId) throws Exception;

    /**
     * 获取流水号值，单独使用事务
     *
     * @param type   流水号类型
     * @param name   流水号名称
     * @param deptId 部门ID
     * @param year   年份
     * @return 流水号值
     * @throws Exception 数据库操作错误
     */
    @Transactional(mode = TransactionMode.required_new)
    public Integer getSerialValue(String type, Integer deptId, Integer year, String name) throws Exception
    {
        lock(Serial.class, type, deptId, year, name);

        Serial serial = getSerial(type, deptId, year, name);
        Integer value;

        if (serial == null)
        {
            value = 1;

            serial = new Serial();
            serial.setType(type);
            serial.setSerialName(name);
            serial.setDeptId(deptId);
            serial.setYear(year);
            serial.setSerialValue(2);

            add(serial);
        }
        else
        {
            value = serial.getSerialValue();

            serial.setSerialValue(value + 1);
            update(serial);
        }

        return value;
    }

    public Serial getSerial(String type, Integer deptId, Integer year, String name) throws Exception
    {
        return load(Serial.class, type, deptId, year, name);
    }

    public void setSerialValue(String type, Integer deptId, Integer year, String name, Integer value) throws Exception
    {
        Serial serial = new Serial();
        serial.setType(type);
        serial.setDeptId(deptId);
        serial.setYear(year);
        serial.setSerialName(name);
        serial.setSerialValue(value);

        save(serial);
    }

    @OQLUpdate("delete Serial where type=:1 and deptId=:2 and year=:3 and serialName in :4")
    public abstract int deleteSerials(String type, Integer deptId, Integer year, String... names) throws Exception;

    @OQLUpdate("update Serial set serialValue=1 where type=:1 and deptId=:2 and year=:3 and serialName in :4")
    public abstract void initialize(String type, Integer deptId, Integer year, String[] names) throws Exception;
}
