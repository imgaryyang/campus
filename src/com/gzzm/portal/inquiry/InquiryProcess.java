package com.gzzm.portal.inquiry;

import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.timeout.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 咨询处理，一个咨询可能在多个部门处理，因此对应多条处理记录
 *
 * @author camel
 * @date 12-11-6
 */
@Indexes({
        @Index(columns = {"DEPTID", "LASTPROCESS", "STATE"})
})
@Entity(table = "PLINQUIRYPROCESS", keys = "processId")
public class InquiryProcess
{
    @Inject
    private static Provider<InquiryService> serviceProvider;

    @Inject
    private static Provider<TimeoutService> timeoutServiceProvider;

    @Generatable(length = 11)
    private Long processId;

    @Index
    private Long inquiryId;

    @NotSerialized
    private Inquiry inquiry;

    /**
     * 上一个处理
     */
    private Long previousProcessId;

    @NotSerialized
    @ToOne("PREVIOUSPROCESSID")
    private InquiryProcess previousProcess;

    /**
     * 处理的部门
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 收到的时间，收到是指系统收到
     */
    private Date startTime;

    /**
     * 接收的时间
     */
    private Date acceptTime;

    /**
     * 处理结束的时间
     */
    private Date endTime;

    /**
     * 处理状态
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private ProcessState state;

    /**
     * 是否转内部处理
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean turnInnered;

    /**
     * 预处理回复的内容，在正式回复之前先通知申请人的内容
     */
    @NotSerialized
    private char[] preReplyContent;

    /**
     * 回复的内容
     */
    @NotSerialized
    private char[] replyContent;

    /**
     * 是否公开处理结果
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean publicity;

    /**
     * 处理的用户
     */
    private Integer userId;

    @NotSerialized
    private User user;

    @ColumnDescription(type = "number(12)")
    private Long attachmentId;

    /**
     * 附件列表，处理过程上传的附件
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    @NotSerialized
    @EntityList(column = "ATTACHMENTID")
    private SortedSet<Attachment> attachments;

    /**
     * 是否为本部门处理的最后一条记录，用于显示的时候只过滤出最后一条
     */
    @ColumnDescription(defaultValue = "1")
    private Boolean lastProcess;

    /**
     * 受理时限
     */
    @ColumnDescription(type = "number(2)")
    private Integer acceptTimeLimit;

    /**
     * 处理时限
     */
    @ColumnDescription(type = "number(2)")
    private Integer processTimeLimit;

    @NotSerialized
    @ComputeColumn("select t from Timeout t where t.typeId in ('" + InquiryTimeout.ACCEPTID +
            "','" + InquiryTimeout.PROCESSID + "') and recordId=processId order by timeoutTime")
    private List<Timeout> timeouts;

    /**
     * 是否已读
     */
    @ColumnDescription
    private Boolean read;

    public InquiryProcess()
    {
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public Long getInquiryId()
    {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId)
    {
        this.inquiryId = inquiryId;
    }

    public Inquiry getInquiry()
    {
        return inquiry;
    }

    public void setInquiry(Inquiry inquiry)
    {
        this.inquiry = inquiry;
    }

    public Long getPreviousProcessId()
    {
        return previousProcessId;
    }

    public void setPreviousProcessId(Long previousProcessId)
    {
        this.previousProcessId = previousProcessId;
    }

    public InquiryProcess getPreviousProcess()
    {
        return previousProcess;
    }

    public void setPreviousProcess(InquiryProcess previousProcess)
    {
        this.previousProcess = previousProcess;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getAcceptTime()
    {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public ProcessState getState()
    {
        return state;
    }

    public void setState(ProcessState state)
    {
        this.state = state;
    }

    public Boolean isTurnInnered()
    {
        return turnInnered;
    }

    public void setTurnInnered(Boolean turnInnered)
    {
        this.turnInnered = turnInnered;
    }

    public char[] getPreReplyContent()
    {
        return preReplyContent;
    }

    public void setPreReplyContent(char[] preReplyContent)
    {
        this.preReplyContent = preReplyContent;
    }

    public char[] getReplyContent()
    {
        return replyContent;
    }

    public void setReplyContent(char[] replyContent)
    {
        this.replyContent = replyContent;
    }

    public Boolean isPublicity()
    {
        return publicity;
    }

    public void setPublicity(Boolean publicity)
    {
        this.publicity = publicity;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public SortedSet<Attachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(SortedSet<Attachment> attachments)
    {
        this.attachments = attachments;
    }

    public Boolean getLastProcess()
    {
        return lastProcess;
    }

    public void setLastProcess(Boolean lastProcess)
    {
        this.lastProcess = lastProcess;
    }

    public Integer getAcceptTimeLimit()
    {
        return acceptTimeLimit;
    }

    public void setAcceptTimeLimit(Integer acceptTimeLimit)
    {
        this.acceptTimeLimit = acceptTimeLimit;
    }

    public Integer getProcessTimeLimit()
    {
        return processTimeLimit;
    }

    public void setProcessTimeLimit(Integer processTimeLimit)
    {
        this.processTimeLimit = processTimeLimit;
    }

    public List<Timeout> getTimeouts()
    {
        return timeouts;
    }

    public void setTimeouts(List<Timeout> timeouts)
    {
        this.timeouts = timeouts;
    }

    public Boolean getRead()
    {
        return read;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
    }

    @NotSerialized
    public Timeout getValidTimeout()
    {
        Timeout validTimeout = null;
        for (Timeout timeout : getTimeouts())
        {
            if (timeout.getValid() != null && timeout.getValid())
            {
                validTimeout = timeout;
            }
        }

        return validTimeout;
    }

    @NotSerialized
    public List<Timeout> getValidTimeouts()
    {
        List<Timeout> timeouts = new ArrayList<Timeout>(2);
        for (Timeout timeout : getTimeouts())
        {
            if (timeout.getValid() != null && timeout.getValid())
            {
                timeouts.add(timeout);
            }
        }

        return timeouts;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof InquiryProcess))
            return false;

        InquiryProcess that = (InquiryProcess) o;

        return processId.equals(that.processId);
    }

    @Override
    public int hashCode()
    {
        return processId.hashCode();
    }

    @Override
    public String toString()
    {
        Inquiry inquiry = getInquiry();

        return inquiry == null ? "" : inquiry.toString();
    }

    @BeforeAdd
    public void beforeAdd() throws Exception
    {
        //根据配置初始化受理时限和处理时限
        if (deptId != null)
        {
            if (acceptTimeLimit == null)
            {
                TimeoutConfigInfo timeoutConfig =
                        timeoutServiceProvider.get().getTimeoutConfig(InquiryTimeout.ACCEPTID, this, deptId);

                if (timeoutConfig != null)
                    acceptTimeLimit = timeoutConfig.getTimeLimit();
            }

            if (processTimeLimit == null)
            {
                TimeoutConfigInfo timeoutConfig =
                        timeoutServiceProvider.get().getTimeoutConfig(InquiryTimeout.PROCESSID, this, deptId);

                if (timeoutConfig != null)
                    processTimeLimit = timeoutConfig.getTimeLimit();
            }
        }
    }

    @BeforeUpdate
    public void beforeUpdate() throws Exception
    {
        if (state == ProcessState.PROCESSING || state == ProcessState.TURNED || state == ProcessState.REPLYED)
        {
            serviceProvider.get().checkTimeout(processId);
        }
    }
}
