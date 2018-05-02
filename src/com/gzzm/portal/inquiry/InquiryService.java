package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.event.Events;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.timeout.TimeoutCheck;
import com.gzzm.platform.wordnumber.WordNumberService;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Null;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-11-8
 */
public class InquiryService
{
    @Inject
    private InquiryDao dao;

    @Inject
    private CommonDao commonDao;

    @Inject
    private WordNumberService wordNumberService;

    @Inject
    private DeptService deptService;

    public InquiryService()
    {
    }

    public InquiryDao getDao()
    {
        return dao;
    }

    public CommonDao getCommonDao()
    {
        return commonDao;
    }

    /**
     * 提交一个来信
     *
     * @param inquiry 要提交的来信的信息
     * @return 来信ID
     * @throws Exception 写入数据库错误
     */
    @Transactional
    public Inquiry write(Inquiry inquiry) throws Exception
    {
        DeptInfo dept = deptService.getDept(inquiry.getDeptId());

        Date time = new Date();
        inquiry.setSendTime(time);
        inquiry.setLastTime(time);
        inquiry.setState(InquiryState.NOACCEPTED);

        inquiry.setCode(wordNumberService.getWordNumber("inquiry", inquiry.getDeptId(),
                Collections.singletonMap("deptCode", (Object) dept.getDeptCode())));

        dao.add(inquiry);

        //同时往收信部门写一条待处理的记录
        InquiryProcess process = new InquiryProcess();
        process.setDeptId(inquiry.getDeptId());
        process.setInquiryId(inquiry.getInquiryId());
        process.setStartTime(time);
        process.setState(ProcessState.NOACCEPTED);
        process.setLastProcess(true);
        dao.add(process);

        Events.invoke(dao.getProcess(process.getProcessId()), "receive");

        return inquiry;
    }

    /**
     * 将咨询投诉转部门
     *
     * @param process 当前处理过程
     * @param deptId  要转向的部门
     * @return 新的处理过程的id
     * @throws Exception 写入数据库错误
     */
    @Transactional
    public Long turn(InquiryProcess process, Integer deptId) throws Exception
    {
        Date time = new Date();

        process.setState(ProcessState.TURNED);
        process.setEndTime(time);
        process.setInquiry(null);
        dao.update(process);

        dao.updateLast(process.getInquiryId(), deptId);

        InquiryProcess nextProcess = new InquiryProcess();
        nextProcess.setPreviousProcessId(process.getProcessId());
        nextProcess.setDeptId(deptId);
        nextProcess.setInquiryId(process.getInquiryId());
        nextProcess.setStartTime(time);
        nextProcess.setState(ProcessState.NOACCEPTED);
        nextProcess.setLastProcess(true);
        dao.add(nextProcess);

        Events.invoke(dao.getProcess(nextProcess.getProcessId()), "receive");

        return nextProcess.getProcessId();
    }

    /**
     * 将咨询投诉转部门处理
     *
     * @param inquiryId 要转部门的来信ID
     * @param deptId    要转向的部门
     * @param opinion   意见
     * @return 新的处理过程的id
     * @throws Exception 读写数据库错误
     */
    @Transactional
    public Long turn(Long inquiryId, Integer deptId, String opinion) throws Exception
    {
        Date time = new Date();

        Inquiry inquiry = dao.getInquiry(inquiryId);
        InquiryProcess process = inquiry.getLastProcess();

        process.setState(ProcessState.TURNED);
        process.setEndTime(time);
        process.setInquiry(null);
        process.setReplyContent(opinion.toCharArray());
        dao.update(process);

        dao.updateLast(inquiryId, deptId);

        InquiryProcess nextProcess = new InquiryProcess();
        nextProcess.setPreviousProcessId(process.getProcessId());
        nextProcess.setDeptId(deptId);
        nextProcess.setInquiryId(process.getInquiryId());
        nextProcess.setStartTime(time);
        nextProcess.setState(ProcessState.NOACCEPTED);
        nextProcess.setLastProcess(true);
        dao.add(nextProcess);

        inquiry.setLastTime(new Date());
        dao.update(inquiry);

        Events.invoke(dao.getProcess(nextProcess.getProcessId()), "receive");

        return nextProcess.getProcessId();
    }

    @Transactional
    public void back(Long inquiryId) throws Exception
    {
        Inquiry inquiry = dao.getInquiry(inquiryId);

        inquiry.setState(InquiryState.PROCESSING);
        inquiry.setPublishType(PublishType.PUBLICITY);
        inquiry.setReplyTime(Null.Timestamp);

        dao.update(inquiry);

        for (InquiryProcess process : inquiry.getProcesses())
        {
            if (process.getState() == ProcessState.REPLYED)
            {
                process.setState(ProcessState.PROCESSING);
                process.setEndTime(Null.Timestamp);
                dao.update(process);
            }
        }
    }

    public InquiryCatalog modifyCatalog(Long inquiryId, Integer catalogId) throws Exception
    {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryId(inquiryId);
        inquiry.setCatalogId(catalogId);

        dao.update(inquiry);

        return dao.getCatalog(catalogId);
    }

    public void modifyPublishType(Long inquiryId, PublishType type) throws Exception
    {
        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryId(inquiryId);
        inquiry.setPublishType(type);

        dao.update(inquiry);
    }

    public void modifyAcceptTimeLimit(Long processId, Integer timeLimit) throws Exception
    {
        InquiryProcess process = new InquiryProcess();
        process.setProcessId(processId);
        process.setAcceptTimeLimit(timeLimit);

        dao.update(process);
    }

    public void modifyProcessTimeLimit(Long processId, Integer timeLimit) throws Exception
    {
        InquiryProcess process = new InquiryProcess();
        process.setProcessId(processId);
        process.setProcessTimeLimit(timeLimit);

        dao.update(process);
    }

    public void checkTimeouts() throws Exception
    {
        for (InquiryProcess process : dao.getShouldCheckTimeoutProcesses())
        {
            try
            {
                checkTimeout(process);
            }
            catch (Throwable ex)
            {
                //检查一条记录错误，不影响检查其他记录，只记录日志

                Tools.log("check timeout error,processId:" + process.getProcessId(), ex);
            }
        }
    }

    public void checkTimeout(Long processId) throws Exception
    {
        checkTimeout(dao.getProcess(processId));
    }

    public void checkTimeout(InquiryProcess process) throws Exception
    {
        checkTimeout(process, InquiryTimeout.ACCEPTID);
        checkTimeout(process, InquiryTimeout.PROCESSID);
    }

    /**
     * 检查超时
     *
     * @param timeOutTypeId 受理超时合适处理超时
     * @param process       要检查超时的记录
     * @throws Exception 数据库操作错误
     */
    public void checkTimeout(InquiryProcess process, String timeOutTypeId) throws Exception
    {
        ProcessState state = process.getState();

        //开始计时的时间，未受理为来信时间（或转到此部门的时间），处理为受理时间
        java.util.Date startTime;

        //办理时限
        Integer timeLimit;

        //已经处理，判别是否超时的时间为结束的时间，否则为当前时间
        java.util.Date time;
        java.util.Date endTime;

        if (InquiryTimeout.ACCEPTID.equals(timeOutTypeId))
        {
            //受理超时,开始时间为申请时间或者转到此部门的时间,时限为受理时限
            startTime = process.getStartTime();
            timeLimit = process.getAcceptTimeLimit();

            if (state == ProcessState.NOACCEPTED)
            {
                time = new Date();
                endTime = null;
            }
            else
            {
                time = process.getAcceptTime();
                endTime = process.getAcceptTime();
                if (time == null)
                    time = new Date();
            }
        }
        else
        {
            //处理超时，开始时间为受理时间，时限为办理时限
            timeOutTypeId = InquiryTimeout.PROCESSID;
            startTime = process.getAcceptTime();
            timeLimit = process.getProcessTimeLimit();

            if (state == ProcessState.REPLYED || state == ProcessState.TURNED)
            {
                time = process.getEndTime();
                endTime = process.getEndTime();
                if (time == null)
                    time = new Date();
            }
            else
            {
                time = new Date();
                endTime = null;
            }
        }

        if (startTime == null)
            return;

        TimeoutCheck timeoutCheck = new TimeoutCheck();

        timeoutCheck.setTypeId(timeOutTypeId);
        timeoutCheck.setStartTime(startTime);
        timeoutCheck.setTimeLimit(timeLimit);
        timeoutCheck.setTime(time);
        timeoutCheck.setRecordId(process.getProcessId());
        timeoutCheck.setDeptId(process.getDeptId());
        timeoutCheck.setEndTime(endTime);
        timeoutCheck.setObj(process);

        if (timeoutCheck.check())
        {
            Inquiry inquiry = process.getInquiry();
            if (inquiry != null)
            {
                inquiry.setLastTime(new Date());
                dao.update(inquiry);
            }
        }
    }


}
