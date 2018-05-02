package com.gzzm.portal.inquiry.in;

import com.gzzm.platform.attachment.*;
import com.gzzm.portal.inquiry.*;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * @author lfx
 * @date 17-4-21
 */

public abstract class InquiryInfterfaceDao extends GeneralDao
{
    public InquiryInfterfaceDao()
    {
    }

    @OQL("select deptId from Dept c where c.deptCode=:1 limit 1")
    public abstract Integer getDeptIdByDeptCode(String deptCode);

    @OQL("select count(c.inquiryId) from Inquiry c where c.code=:1")
    public abstract boolean checkCode(String code);

    @OQL("select b from InquiryProcess b where b.inquiry.state in (2,3) and lastProcess=1 and b.inquiry.deptId in ?1 and " +
            " b.inquiryId in (select c.inquiryId from InquiryInterface c where c.receive=0)")
    public abstract List<InquiryProcess> getProcesses(Collection<Integer> deptIds) throws Exception;

    @OQLUpdate("update InquiryInterface set receive=1,receiveTime=:2 where inquiryId in :1")
    public abstract void updateReceive(List<Long> inquiryIds, Date date);

    @GetByField({"deptId","inquiryId"})
    public abstract Inquiry getInquiry(Integer deptId,Long inquiryId);

    @OQL("select a from Attachment a where attachmentId=:1 order by orderId,attachmentNo")
    public abstract Attachment getAttachment(Long attachmentId) throws Exception;

}
