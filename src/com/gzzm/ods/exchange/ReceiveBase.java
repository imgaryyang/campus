package com.gzzm.ods.exchange;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.timeout.OdTimeout;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.menu.MenuContainer;
import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.organ.DeptService;
import com.gzzm.platform.organ.User;
import com.gzzm.platform.timeout.Timeout;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 基于收文的文件，如收文，会签公文，联合发文
 *
 * @author camel
 * @date 2010-9-9
 */
@Entity(table = "ODRECEIVEBASE", keys = "receiveId")
@Indexes({
        @Index(columns = {"DEPTID", "STATE", "TYPE"}),
        @Index(columns = {"DOCUMENTID", "TYPE"}),
        @Index(columns = {"SENDTIME", "NOTIFIED"})
})
public class ReceiveBase
{
    @Inject
    private static Provider<DeptService> deptServiceProvider;

    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    /**
     * 收文ID
     */
    @Generatable(length = 12)
    private Long receiveId;

    /**
     * 收文部门
     */
    private Integer deptId;

    /**
     * 关联收文部门对象
     */
    @NotSerialized
    private Dept dept;


    /**
     * 发文时间
     */
    private Date sendTime;

    /**
     * 接收时间
     */
    private Date acceptTime;

    /**
     * 办结时间
     */
    private Date endTime;

    /**
     * 文件状态
     */
    private ReceiveState state;

    /**
     * 类别
     */
    private ReceiveType type;

    /**
     * 收文方式，分为系统发送过来的公文，手工录入的公文，从其他接口过来的公文
     */
    @ColumnDescription(defaultValue = "0")
    private ReceiveMethod method;

    /**
     * 文件接收人的用户ID
     */
    private Integer receiver;

    @NotSerialized
    @ToOne("RECEIVER")
    private User receiverUser;

    /**
     * 接收人的姓名
     */
    @ColumnDescription(type = "varchar(50)")
    private String receiverName;

    /**
     * 接收人电话
     */
    @ColumnDescription(type = "varchar(50)")
    private String receiverPhone;

    /**
     * 发送类型，如主送，抄送等
     */
    @ColumnDescription(type = "varchar(50)")
    private String sendType;

    /**
     * 文档ID
     */
    private Long documentId;

    @Lazy(false)
    @NotSerialized
    private OfficeDocument document;

    private Integer sourceDeptId;

    @NotSerialized
    @ToOne("SOURCEDEPTID")
    private Dept sourceDept;

    private Boolean notified;

    private Date deadline;

    @NotSerialized
    @ComputeColumn("select t from Timeout t where t.typeId='" + OdTimeout.RECEIVE_ID +
            "' and recordId=receiveId order by timeoutTime")
    private List<Timeout> timeouts;

    public ReceiveBase()
    {
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
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

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
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

    public ReceiveState getState()
    {
        return state;
    }

    public void setState(ReceiveState state)
    {
        this.state = state;
    }

    public ReceiveType getType()
    {
        return type;
    }

    public void setType(ReceiveType type)
    {
        this.type = type;
    }

    public ReceiveMethod getMethod()
    {
        return method;
    }

    public void setMethod(ReceiveMethod method)
    {
        this.method = method;
    }

    public Integer getReceiver()
    {
        return receiver;
    }

    public void setReceiver(Integer receiver)
    {
        this.receiver = receiver;
    }

    public User getReceiverUser()
    {
        return receiverUser;
    }

    public void setReceiverUser(User receiverUser)
    {
        this.receiverUser = receiverUser;
    }

    public String getReceiverName()
    {
        return receiverName;
    }

    public void setReceiverName(String receiverName)
    {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone()
    {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone)
    {
        this.receiverPhone = receiverPhone;
    }

    public Boolean getNotified()
    {
        return notified;
    }

    public void setNotified(Boolean notified)
    {
        this.notified = notified;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    @NotSerialized
    public String getReceiverString()
    {
        if (!StringUtils.isEmpty(receiverName))
            return receiverName;

        User user = getReceiverUser();
        if (user != null)
            return user.getUserName();

        return null;
    }

    @NotSerialized
    public String getSourceDeptName()
    {
        Dept sourceDept = getSourceDept();
        if (sourceDept == null)
            return getDocument().getSourceDept();
        else
            return sourceDept.getDeptName();
    }

    public String getSendType()
    {
        return sendType;
    }

    public void setSendType(String sendType)
    {
        this.sendType = sendType;
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public String getEncodedDocumentId()
    {
        return OfficeDocument.encodeId(documentId);
    }

    public OfficeDocument getDocument()
    {
        return document;
    }

    public void setDocument(OfficeDocument document)
    {
        this.document = document;
    }

    public Integer getSourceDeptId()
    {
        return sourceDeptId;
    }

    public void setSourceDeptId(Integer sourceDeptId)
    {
        this.sourceDeptId = sourceDeptId;
    }

    public Dept getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(Dept sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public List<Timeout> getTimeouts()
    {
        return timeouts;
    }

    public void setTimeouts(List<Timeout> timeouts)
    {
        this.timeouts = timeouts;
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

    public static String[] getReceiveUrls(ReceiveType type)
    {
        if (type == ReceiveType.copy)
            return null;

        String typeString;

        if (type == ReceiveType.union || type == ReceiveType.uniondeal ||
                type == ReceiveType.uniondeal)
        {
            typeString = "type=union&type=unionseal&type=uniondeal";
        }
        else
        {
            typeString = "type=" + type;
        }

        return new String[]{"/ods/receivelist?state=noAccepted&" + typeString,
                "/ods/receivelist?state=noAccepted&state=accepted&" + typeString,
                "/ods/receivelist?state=noAccepted",
                "/ods/receivelist?state=noAccepted&state=accepted",
                "/ods/receivelist?" + typeString,
                "/ods/receivelist"};
    }

    @NotSerialized
    public List<User> getReceivers() throws Exception
    {
        if (type == ReceiveType.copy)
            return null;

        menuContainerProvider.get().getMenuIdsByUrl();

        DeptService deptService = deptServiceProvider.get();

        return deptService.getUsersByApp(ExchangeNotifyService.NOTIFY_APP, getDeptId());
    }

    @NotSerialized
    public String getReceiversInfo() throws Exception
    {
        try
        {

            List<User> users = getReceivers();
            if (users != null && users.size() > 0)
            {
                StringBuilder buffer = new StringBuilder();

                for (User user : users)
                {
                    if (buffer.length() > 0)
                        buffer.append("\r\n");

                    buffer.append(user.getUserName());

                    String phone = user.getPhone();
                    if (!StringUtils.isEmpty(user.getOfficePhone()))
                    {
                        if (StringUtils.isEmpty(phone))
                            phone = user.getOfficePhone();
                        else
                            phone += "," + user.getOfficePhone();
                    }

                    if (!StringUtils.isEmpty(phone))
                        buffer.append("(").append(phone).append(")");
                }

                return buffer.toString();
            }
            else if (!StringUtils.isEmpty(receiverName))
            {
                String s = receiverName;
                if (!StringUtils.isEmpty(receiverPhone))
                {
                    s += "(" + receiverPhone + ")";
                }
                else if (receiver != null)
                {
                    User user = getReceiverUser();
                    String phone = user.getPhone();
                    if (!StringUtils.isEmpty(user.getOfficePhone()))
                    {
                        if (StringUtils.isEmpty(phone))
                            phone = user.getOfficePhone();
                        else
                            phone += "," + user.getOfficePhone();
                    }

                    if (!StringUtils.isEmpty(phone))
                        s += "(" + phone + ")";
                }
                return s;
            }
            else
            {
                return null;
            }
        }
        catch (Exception ex)
        {
            Tools.log(ex);
            return null;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof ReceiveBase))
            return false;

        ReceiveBase that = (ReceiveBase) o;

        return receiveId.equals(that.receiveId);
    }

    @Override
    public int hashCode()
    {
        return receiveId.hashCode();
    }
}
