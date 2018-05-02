package com.gzzm.platform.form;

import net.cyan.commons.transaction.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 表单相关的数据库操作
 *
 * @author camel
 * @date 2010-9-20
 */
public abstract class FormDao extends GeneralDao
{
    public FormDao()
    {
    }

    public FormInfo getFormInfo(Integer formId) throws Exception
    {
        return get(FormInfo.class, formId);
    }

    public FormRecord getFormRecord(Long recordId) throws Exception
    {
        return get(FormRecord.class, recordId);
    }

    @OQL("select form from FormInfo where formId=:1")
    public abstract char[] getFormContent(Integer formId) throws Exception;


    @OQL("select updateTime from FormInfo where formId=:1")
    public abstract Date getFormUpdateTime(Integer formId) throws Exception;

    /**
     * 获得某个表单最后一个版本
     *
     * @param ieFormId 忽略版本号的表单
     * @return 此表单最后一个发布版本
     * @throws Exception 数据库查询错误
     * @see com.gzzm.platform.form.FormInfo#ieFormId
     */
    @OQL("select f from FormInfo f where ieFormId=:1 and published=1 order by version desc limit 1")
    public abstract FormInfo getLastForm(Integer ieFormId) throws Exception;

    /**
     * 获得某个表单的最大版本
     *
     * @param ieFormId 忽略版本号的表单ID
     * @return 此表单的最大版本号
     * @throws Exception 数据异常
     */
    @OQL("select max(version) from FormInfo where ieFormId=:1 and version is not null")
    public abstract Integer getMaxVersion(Integer ieFormId) throws Exception;

    /**
     * 获得某个表单最后一个版本的ID
     *
     * @param ieFormId 忽略版本号的表单ID
     * @return 此表单最后一个发布版本的ID
     * @throws Exception 数据库查询错误
     * @see com.gzzm.platform.form.FormInfo#ieFormId
     */
    @OQL("select formId from FormInfo where ieFormId=:1 and published=1 and version is not null order by version desc limit 1")
    public abstract Integer getLastFormId(Integer ieFormId) throws Exception;

    /**
     * 根据流程名称和部门ID获得一个发布的流程
     *
     * @param formName 流程名称
     * @param type     表单类型
     * @param deptId   部门ID
     * @return 对应的流程的id
     * @throws Exception 数据库查询错误
     */
    @OQL("select formId from FormInfo f where formName=:1 and type=:2 and deptId=:3 and published=1 " +
            "and version in (select max(version) from FormInfo f1 where f1.ieFormId=f.ieFormId and published=1 " +
            "and version is not null) order by formId desc")
    public abstract Integer getFormIdByName(String formName, String type, Integer deptId) throws Exception;

    /**
     * 标志表单未被使用，不使用事务，已避免外部事务将表单表锁住
     *
     * @param formId 要标志为被使用的表单的ID
     * @throws Exception 数据库异常
     */
    @Transactional(mode = TransactionMode.not_supported)
    @OQLUpdate("update FormInfo set used=1 where formId=:1 and used=0")
    public abstract void setFormUsed(Integer formId) throws Exception;

    public FormBody getFormBody(Long bodyId) throws Exception
    {
        return get(FormBody.class, bodyId);
    }

    /**
     * 锁住表单数据，防止两个线程并发修改
     *
     * @param bodyId 表单数据体Id
     * @throws Exception 数据库操作错误
     */
    public void lockFormBody(Long bodyId) throws Exception
    {
        lock(FormBody.class, bodyId);
    }


    /**
     * 根据部门和类型，获得所有表单的最后一个版本
     *
     * @param deptId 部门id
     * @param type   流程类型
     * @return 流程列表
     * @throws Exception 数据库异常
     */
    @OQL("select f from FormInfo f where type=:2 and deptId=:1 and published=1 and version in " +
            "(select max(version) from FormInfo f1 where f1.ieFormId=f.ieFormId and published=1) order by formName,formId desc")
    public abstract List<FormInfo> getLastFormInfos(Integer deptId, String type) throws Exception;
}
