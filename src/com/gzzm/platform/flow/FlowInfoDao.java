package com.gzzm.platform.flow;

import net.cyan.commons.transaction.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 2014/8/21
 */
public abstract class FlowInfoDao extends GeneralDao
{
    @Inject
    private static Provider<FlowInfoDao> daoProvider;

    public static FlowInfoDao getInstance()
    {
        return daoProvider.get();
    }

    public FlowInfoDao()
    {
    }

    public FlowInfo getFlowInfo(Integer flowId) throws Exception
    {
        return get(FlowInfo.class, flowId);
    }

    @OQL("select flow from FlowInfo where flowId=:1")
    public abstract char[] getFlowContent(Integer flowId) throws Exception;

    @OQL("select updateTime from FlowInfo where flowId=:1")
    public abstract Date getFlowUpdateTime(Integer flowId) throws Exception;

    /**
     * 获得某个流程最后一个版本
     *
     * @param ieFlowId 忽略版本号的流程
     * @return 此流程最后一个发布版本
     * @throws Exception 数据库查询错误
     * @see com.gzzm.platform.flow.FlowInfo#ieFlowId
     */
    @OQL("select f from FlowInfo f where ieFlowId=:1 and published=1 and version is not null order by version desc limit 1")
    public abstract FlowInfo getLastFlow(Integer ieFlowId) throws Exception;

    /**
     * @param ieFlowId 忽略版本号的流程ID （ieFlowId）
     * @return 返回同一流程中版本号值最大的版本号
     * @throws Exception 数据库异常
     */
    @OQL("select max(version) from FlowInfo where ieFlowId =:1 and published=1 and version is not null")
    public abstract Integer getMaxVersion(Integer ieFlowId) throws Exception;

    /**
     * 获得某个流程最后一个版本的ID
     *
     * @param ieFlowId 忽略版本号的流程ID
     * @return 此流程最后一个发布版本的ID
     * @throws Exception 数据库查询错误
     * @see com.gzzm.platform.flow.FlowInfo#ieFlowId
     */
    @OQL("select flowId from FlowInfo where ieFlowId=:1 and published=1 order by version desc limit 1")
    public abstract Integer getLastFlowId(Integer ieFlowId) throws Exception;

    /**
     * 根据流程名称和部门ID获得一个发布的流程
     *
     * @param flowName 流程名称
     * @param type     流程类型
     * @param deptId   部门ID
     * @return 对应的流程的id
     * @throws Exception 数据库查询错误
     */
    @OQL("select flowId from FlowInfo f where flowName=:1 and type=:2 and deptId=:3 and published=1 " +
            "and version in (select max(version) from FlowInfo f1 where f1.ieFlowId=f.ieFlowId and published=1 " +
            "and version is not null) order by flowId desc")
    public abstract Integer getFlowIdByName(String flowName, String type, Integer deptId) throws Exception;

    /**
     * 标志流程未被使用，不使用事务，已避免外部事务将流程表锁住
     *
     * @param flowId 要标志为被使用的流程的ID
     * @throws Exception 数据库异常
     */
    @Transactional(mode = TransactionMode.not_supported)
    @OQLUpdate("update FlowInfo set used=1 where flowId=:1 and used=0")
    public abstract void setFlowUsed(Integer flowId) throws Exception;

    /**
     * 根据部门和类型，获得所有流程的最后一个版本
     *
     * @param deptId 部门id
     * @param type   流程类型
     * @return 流程列表
     * @throws Exception 数据库异常
     */
    @OQL("select f from FlowInfo f where type=:2 and deptId=:1 and published=1 and version in " +
            "(select max(version) from FlowInfo f1 where f1.ieFlowId=f.ieFlowId and published=1) order by flowName,flowId desc")
    public abstract List<FlowInfo> getLastFlowInfos(Integer deptId, String type) throws Exception;
}
