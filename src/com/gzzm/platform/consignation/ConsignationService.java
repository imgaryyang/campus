package com.gzzm.platform.consignation;

import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 委托相关的service类
 *
 * @author camel
 * @date 2010-8-30
 */
public class ConsignationService
{
    @Inject
    private ConsignationDao dao;

    public ConsignationService()
    {
    }

    public ConsignationInfo getConsignationInfo(Integer consigner, String[] modules) throws Exception
    {
        return getConsignationInfo(consigner, new Date(), modules);
    }

    /**
     * 获得某个某段时间的委托信息，当被委托者在这个时间又委托给其他人的时候，递归获得最终的被委托者
     *
     * @param consigner 委托人
     * @param time      时间
     * @param modules   委托的功能模块
     * @return 委托信息，如果这个时间没有委托给别人，则返回null
     * @throws Exception 数据库错误
     */
    public ConsignationInfo getConsignationInfo(Integer consigner, Date time, String[] modules) throws Exception
    {
        Consignation consignation = dao.getAvailableConsignation(consigner, time, modules);

        Integer consignee = null;
        Integer consignationId;

        //没有有效委托，返回null
        if (consignation == null)
            return null;

        //用户ID集合，保存遍历过的用户ID，避免循环委托导致死循环
        Set<Integer> userIds = new HashSet<Integer>();

        //获得第一层委托的委托ID
        consignationId = consignation.getConsignationId();

        while (consignation != null)
        {
            //循环委托，跳出
            if (userIds.contains(consignation.getConsignee()))
                break;

            consignee = consignation.getConsignee();
            userIds.add(consignee);
            consignation = dao.getAvailableConsignation(consignation.getConsignee(), time, modules);
        }

        if (consignee == null)
            return null;

        return new ConsignationInfo(consignationId, consignee);
    }
}
