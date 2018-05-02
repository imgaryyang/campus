package com.gzzm.portal.inquiry;

import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 咨询投诉
 *
 * @author camel
 * @date 12-11-6
 */
@Entity(table = "PLINQUIRY", keys = "inquiryId")
public class Inquiry
{
    @Generatable(length = 11)
    private Long inquiryId;

    /**
     * 编号，每个咨询给一个编号
     */
    @ColumnDescription(type = "varchar(50)")
    private String code;

    /**
     * 咨询的标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 咨询人的名称
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private String inquirerName;

    /**
     * 咨询人真实姓名
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private String realName;

    /**
     * 咨询人的Id,对于外网登录用户进行咨询时，记录其Id，没有登录的用户，此字段为空
     * 当登录内部系统代录入时，关联内部系统的用户ID，即代录入人的ID
     */
    private Integer inquirer;

    /**
     * 关联咨询人的用户对象，
     */
    @NotSerialized
    @ToOne("INQUIRER")
    private User inquireUser;

    /**
     * 当登录内部系统代录入时，此id为内部系统的部门ID
     */
    @Index
    private Integer inquireDeptId;

    @ToOne("INQUIREDEPTID")
    private Dept inquireDept;

    /**
     * 提交时间
     */
    @Index
    private Date sendTime;

    /**
     * 回复时间
     */
    private Date replyTime;

    /**
     * 最后的修改时间，在处理过程中实时更新此时间，用于生成全文索引
     */
    @IndexTimestamp
    @Index
    private Date lastTime;

    /**
     * 咨询的内容
     */
    @Require
    @NotSerialized
    private char[] content;

    /**
     * 电话号码
     */
    @ColumnDescription(type = "varchar(50)")
    private String phone;

    /**
     * 地址
     */
    @ColumnDescription(type = "varchar(500)")
    private String address;

    /**
     * 电子邮箱
     */
    @Pattern(Pattern.EMAIL)
    @ColumnDescription(type = "varchar(50)")
    private String email;

    /**
     * 邮政编码
     */
    @ColumnDescription(type = "varchar(50)")
    private String postcode;

    /**
     * ip地址
     */
    @ColumnDescription(type = "varchar(50)")
    private String ip;

    /**
     * 被咨询的部门，第一次提交的部门
     */
    @Index
    @Require
    @ColumnDescription(nullable = false)
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 结果是否公开，true为公开，false为不公开，由申请人决定
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean publicity;

    /**
     * 发布方式，由处理人员或者监察人员决定
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private PublishType publishType;

    private Integer typeId;

    @NotSerialized
    private InquiryType type;

    private Integer catalogId;

    @NotSerialized
    private InquiryCatalog catalog;

    private Integer wayId;

    @NotSerialized
    private InquiryWay way;

    @ColumnDescription(nullable = false, defaultValue = "0")
    private InquiryState state;

    /**
     * 查看密码，当不公开信息时，用于查看信息的密码
     */
    @NotSerialized
    @ColumnDescription(type = "varchar(50)")
    private String password;

    /**
     * 处理过程
     */
    @NotSerialized
    @OneToMany
    @OrderBy(column = "startTime")
    private List<InquiryProcess> processes;

    @ColumnDescription(type = "number(12)")
    private Long attachmentId;

    /**
     * 附件列表，提交人提交的附件
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    @NotSerialized
    @EntityList(column = "ATTACHMENTID")
    private SortedSet<Attachment> attachments;

    /**
     * 是否被删除
     */
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Boolean deleted;

    /**
     * 附加属性
     */
    @NotSerialized
    @ValueMap(table = "PLINQUIRYATTRIBUTE", keyColumn = "ATTRIBUTENAME", valueColumn = "ATTRIBUTEVALUE",
            clearForUpdate = false)
    private Map<String, String> attributes;

    @NotSerialized
    private InquiryReadTimes readTimes;

    public Inquiry()
    {
    }

    public Long getInquiryId()
    {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId)
    {
        this.inquiryId = inquiryId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getInquirerName()
    {
        return inquirerName;
    }

    public void setInquirerName(String inquirerName)
    {
        this.inquirerName = inquirerName;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public Integer getInquirer()
    {
        return inquirer;
    }

    public void setInquirer(Integer inquirer)
    {
        this.inquirer = inquirer;
    }

    public User getInquireUser()
    {
        return inquireUser;
    }

    public void setInquireUser(User inquireUser)
    {
        this.inquireUser = inquireUser;
    }

    public Integer getInquireDeptId()
    {
        return inquireDeptId;
    }

    public void setInquireDeptId(Integer inquireDeptId)
    {
        this.inquireDeptId = inquireDeptId;
    }

    public Dept getInquireDept()
    {
        return inquireDept;
    }

    public void setInquireDept(Dept inquireDept)
    {
        this.inquireDept = inquireDept;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public Date getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
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

    public Boolean isPublicity()
    {
        return publicity;
    }

    public void setPublicity(Boolean publicity)
    {
        this.publicity = publicity;
    }

    public PublishType getPublishType()
    {
        return publishType;
    }

    public void setPublishType(PublishType publishType)
    {
        this.publishType = publishType;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public InquiryType getType()
    {
        return type;
    }

    public void setType(InquiryType type)
    {
        this.type = type;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public InquiryCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(InquiryCatalog catalog)
    {
        this.catalog = catalog;
    }

    public Integer getWayId()
    {
        return wayId;
    }

    public void setWayId(Integer wayId)
    {
        this.wayId = wayId;
    }

    public InquiryWay getWay()
    {
        return way;
    }

    public void setWay(InquiryWay way)
    {
        this.way = way;
    }

    public InquiryState getState()
    {
        return state;
    }

    public void setState(InquiryState state)
    {
        this.state = state;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public List<InquiryProcess> getProcesses()
    {
        return processes;
    }

    public void setProcesses(List<InquiryProcess> processes)
    {
        this.processes = processes;
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

    public Boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(Boolean deleted)
    {
        this.deleted = deleted;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    @NotSerialized
    public InquiryProcess getLastProcess()
    {
        List<InquiryProcess> processes = getProcesses();
        if (processes != null)
        {
            for (InquiryProcess process : processes)
            {
                if (process.getState() != ProcessState.TURNED)
                    return process;
            }
        }

        return null;
    }

    /**
     * 得到要生成索引的文字，包括来信内容和处理内容
     *
     * @return 包括来信内容和处理内容
     * @throws Exception 从附件提取文字错误
     */
    @NotSerialized
    @FullText
    public String getText() throws Exception
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append(title).append(" ").append(inquirerName);

        if (getContent() != null)
            buffer.append(" ").append(getContent());

        if (getAttachments() != null)
        {
            for (Attachment attachment : getAttachments())
                buffer.append(" ").append(attachment.getText());
        }

        List<InquiryProcess> processes = getProcesses();
        if (processes != null)
        {
            for (InquiryProcess process : processes)
            {
                if (process.getReplyContent() != null)
                    buffer.append(" ").append(process.getReplyContent());

                if (process.getAttachments() != null)
                {
                    for (Attachment attachment : process.getAttachments())
                        buffer.append(" ").append(attachment.getText());
                }
            }
        }

        return buffer.toString();
    }

    public InquiryReadTimes getReadTimes()
    {
        return readTimes;
    }

    public void setReadTimes(InquiryReadTimes readTimes)
    {
        this.readTimes = readTimes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof Inquiry))
            return false;

        Inquiry inquiry = (Inquiry) o;

        return inquiryId.equals(inquiry.inquiryId);
    }

    @Override
    public int hashCode()
    {
        return inquiryId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }
}