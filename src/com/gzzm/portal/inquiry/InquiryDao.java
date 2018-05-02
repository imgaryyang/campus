package com.gzzm.portal.inquiry;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author camel
 * @date 12-11-7
 */
public abstract class InquiryDao extends GeneralDao
{
    public InquiryDao()
    {
    }

    public Inquiry getInquiry(Long inquiryId) throws Exception
    {
        return load(Inquiry.class, inquiryId);
    }

    public InquiryProcess getProcess(Long processId) throws Exception
    {
        return load(InquiryProcess.class, processId);
    }

    public void lockProcess(Long processId) throws Exception
    {
        lock(InquiryProcess.class, processId);
    }

    public InquiryCatalog getCatalog(Integer catalogId) throws Exception
    {
        return load(InquiryCatalog.class, catalogId);
    }

    public InquiryCatalog getRootCatalog() throws Exception
    {
        InquiryCatalog root = getCatalog(0);
        if (root == null)
        {
            root = new InquiryCatalog();
            root.setCatalogId(0);
            root.setCatalogName("根节点");

            add(root);
        }

        return root;
    }

    @OQL("select w from InquiryWay w order by orderId")
    public abstract List<InquiryWay> getWays() throws Exception;

    @OQL("select t from InquiryType t order by orderId")
    public abstract List<InquiryType> getTypes() throws Exception;

    @OQLUpdate("update InquiryProcess set lastProcess=0 where inquiryId=:1 and deptId=:2")
    public abstract void updateLast(Long inquiryId, Integer deptId) throws Exception;

    /**
     * 查询需要检查超时的处理过程，为那些未受理或处理中的记录
     *
     * @return 需要检查超时的记录
     * @throws Exception 数据库查询数据错误
     */
    @OQL("select p from InquiryProcess p where state in (0,1)")
    public abstract List<InquiryProcess> getShouldCheckTimeoutProcesses() throws Exception;

    @OQL("select count(inquiryId) from Inquiry p where ip like ?1 and sendTime>=?2")
    public abstract boolean countIp(String ip, Date startTime) throws Exception;
}
