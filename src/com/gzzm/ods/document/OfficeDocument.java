package com.gzzm.ods.document;

import com.gzzm.ods.flow.Constants;
import com.gzzm.ods.receipt.Receipt;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.group.Member;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.ext.TextExtractors;
import net.cyan.commons.util.*;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.*;

/**
 * 公文表，公文的基本属性，共享公文的一些基本数据
 *
 * @author camel
 * @date 2010-9-17
 */
@Entity(table = "ODDOCUMENT", keys = "documentId")
public class OfficeDocument
{
    /**
     * 公文ID
     */
    @Generatable(length = 12)
    private Long documentId;

    /**
     * 公文标题
     */
    @ColumnDescription(type = "varchar(500)")
    private String title;

    /**
     * 附件列表ID，关联PFATTACHMENT表
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    private Long attachmentId;

    /**
     * 附件列表
     *
     * @see com.gzzm.platform.attachment.Attachment
     */
    @NotSerialized
    @EntityList(column = "ATTACHMENTID")
    private SortedSet<Attachment> attachments;

    /**
     * 来文单位
     */
    @ColumnDescription(type = "varchar(250)")
    private String sourceDept;

    /**
     * 来文单位组织机构代码
     */
    @ColumnDescription(type = "varchar(50)")
    private String sourceDeptCode;

    /**
     * 发文编号
     */
    @ColumnDescription(type = "varchar(250)")
    private String sendNumber;

    /**
     * 主题词
     */
    @ColumnDescription(type = "varchar(250)")
    private String subject;

    /**
     * 密级
     */
    @ColumnDescription(type = "varchar(50)")
    private String secret;

    /**
     * 优先级
     */
    @ColumnDescription(type = "varchar(50)")
    private String priority;

    /**
     * 份数
     */
    @ColumnDescription(type = "number(5)")
    private Integer sendCount;

    /**
     * 附加属性
     */
    @NotSerialized
    @ValueMap(table = "ODDOCUMENTATTRIBUTEVALUE", keyColumn = "ATTRIBUTENAME", valueColumn = "ATTRIBUTEVALUE",
            clearForUpdate = false)
    private Map<String, String> attributes;

    /**
     * 正文ID，对于没有正文的公文流程，为空
     */
    private Long textId;

    /**
     * 公文正文
     */
    @NotSerialized
    private DocumentText text;

    /**
     * 接收者列表ID
     */
    private Long receiverListId;

    /**
     * 关联接收者列表，仅发文和收文有效
     */
    @NotSerialized
    private DocumentReceiverList receiverList;

    /**
     * 最后的修改时间
     */
    @Index
    @IndexTimestamp
    private Date lastTime;

    /**
     * 原文档，当收文转发文时，发文文档的此id关联原公文的id
     */
    private Long sourceDocumentId;

    @ToOne("SOURCEDOCUMENTID")
    private OfficeDocument sourceDocument;

    @ColumnDescription(defaultValue = "0")
    private NoticeType noticeType;

    private Integer createDeptId;

    @NotSerialized
    @ToOne("CREATEDEPTID")
    private Dept createDept;

    /**
     * 签发人
     */
    @ColumnDescription(type = "varchar(50)")
    private String signer;

    /**
     * 签发时间
     */
    private Date signTime;

    /**
     * 成文时间
     */
    private Date finalTime;

    /**
     * 各种格式的正文，将word编辑的正文转化为pdf等各种格式保存起来
     */
    @NotSerialized
    @ManyToMany(table = "ODDOCUMENTTEXTS")
    private List<DocumentText> texts;

    /**
     * 文件的唯一ID，保证每份文件的唯一性，包括交换到其他服务器或者系统的时候
     */
    @Index
    @ColumnDescription(type = "varchar(32)")
    private String uuid;

    /**
     * 数据所属的年份，用于对数据进行分区
     */
    @ColumnDescription(type = "number(4)")
    private Integer year;

    @Lazy(false)
    @NotSerialized
    @ComputeColumn("count(attachments)")
    private Integer attachmentCount;

    @NotSerialized
    @ColumnDescription(type = "number(11)")
    private Long receiptId;

    @NotSerialized
    private Receipt receipt;

    public OfficeDocument()
    {
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitleText()
    {
        if (StringUtils.isBlank(title))
            return "无标题";
        else
            return title;
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

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getSourceDeptCode()
    {
        return sourceDeptCode;
    }

    public void setSourceDeptCode(String sourceDeptCode)
    {
        this.sourceDeptCode = sourceDeptCode;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public Integer getSendCount()
    {
        return sendCount;
    }

    public void setSendCount(Integer sendCount)
    {
        this.sendCount = sendCount;
    }

    public Long getTextId()
    {
        return textId;
    }

    public void setTextId(Long textId)
    {
        this.textId = textId;
    }

    public DocumentText getText()
    {
        return text;
    }

    public void setText(DocumentText text)
    {
        this.text = text;
    }

    public Long getReceiverListId()
    {
        return receiverListId;
    }

    public void setReceiverListId(Long receiverListId)
    {
        this.receiverListId = receiverListId;
    }

    public DocumentReceiverList getReceiverList()
    {
        return receiverList;
    }

    public void setReceiverList(DocumentReceiverList receiverList)
    {
        this.receiverList = receiverList;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public Date getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
    }

    public Long getSourceDocumentId()
    {
        return sourceDocumentId;
    }

    public void setSourceDocumentId(Long sourceDocumentId)
    {
        this.sourceDocumentId = sourceDocumentId;
    }

    public OfficeDocument getSourceDocument()
    {
        return sourceDocument;
    }

    public void setSourceDocument(OfficeDocument sourceDocument)
    {
        this.sourceDocument = sourceDocument;
    }

    public NoticeType getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeType(NoticeType noticeType)
    {
        this.noticeType = noticeType;
    }

    public Integer getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Integer createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    public Dept getCreateDept()
    {
        return createDept;
    }

    public void setCreateDept(Dept createDept)
    {
        this.createDept = createDept;
    }

    public String getSigner()
    {
        return signer;
    }

    public void setSigner(String signer)
    {
        this.signer = signer;
    }

    public Date getSignTime()
    {
        return signTime;
    }

    public void setSignTime(Date signTime)
    {
        this.signTime = signTime;
    }

    public Date getFinalTime()
    {
        return finalTime;
    }

    public void setFinalTime(Date finalTime)
    {
        this.finalTime = finalTime;
    }

    public List<DocumentText> getTexts()
    {
        return texts;
    }

    public void setTexts(List<DocumentText> texts)
    {
        this.texts = texts;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public Integer getAttachmentCount()
    {
        return attachmentCount;
    }

    public void setAttachmentCount(Integer attachmentCount)
    {
        this.attachmentCount = attachmentCount;
    }

    @NotSerialized
    @FullText
    public String getTextContent() throws Exception
    {
        DocumentText text = getText();

        String s = null;
        if (text != null)
        {
            InputStream otherFile = text.getOtherFile();
            String otherFileName = text.getOtherFileName();
            if (otherFile != null && !StringUtils.isEmpty(otherFileName))
            {
                s = TextExtractors.extract(otherFile, IOUtils.getExtName(otherFileName));
            }
            else
            {
                s = TextExtractors.extract(text.getTextBody(), text.getType());
            }
        }


        if (sourceDocumentId != null && !sourceDocumentId.equals(documentId))
        {
            //算上原文档的内容
            OfficeDocument sourceDocument = getSourceDocument();
            if (sourceDocument != null)
            {
                String s1 = sourceDocument.getTextContent();

                if (!StringUtils.isEmpty(s1))
                    s += " " + s1;
            }
        }

        SortedSet<Attachment> attachments = getAttachments();
        if (attachments != null)
        {
            for (Attachment attachment : attachments)
            {
                try
                {
                    Inputable content = attachment.getInputable();
                    if (content != null)
                    {
                        String s1 = TextExtractors.extract(content.getInputStream(),
                                IOUtils.getExtName(attachment.getFileName()));

                        if (!StringUtils.isEmpty(s1))
                            s += " " + s1;
                    }
                }
                catch (Throwable ex)
                {
                    Tools.log(ex);
                }
            }
        }

        return s;
    }

    @NotSerialized
    public boolean isAttachment()
    {
        return getAttachmentCount() > 0;
    }

    @NotSerialized
    public List<AttachmentInfo> getAttachmentInfos()
    {
        SortedSet<Attachment> attachments = getAttachments();
        if (attachments != null)
        {
            List<AttachmentInfo> result = new ArrayList<AttachmentInfo>(attachments.size());
            for (Attachment attachment : attachments)
            {
                result.add(new AttachmentInfo(attachment));
            }

            return result;
        }

        return null;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Receipt getReceipt()
    {
        return receipt;
    }

    public void setReceipt(Receipt receipt)
    {
        this.receipt = receipt;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OfficeDocument))
            return false;

        OfficeDocument that = (OfficeDocument) o;

        return documentId.equals(that.documentId);
    }

    @Override
    public int hashCode()
    {
        return documentId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }

    @BeforeAdd
    public void beforeAdd()
    {
        //设置唯一id
        setUuid(Tools.getUUID());
    }

    @FullText
    @NotSerialized
    public String getMainReceivers()
    {
        return getReceivers(Constants.Document.MAINSEND);
    }

    @NotSerialized
    public String getOtherReceivers()
    {
        return getReceivers(null, Constants.Document.MAINSEND);
    }

    @NotSerialized
    public String getReceivers()
    {
        return getReceivers(null);
    }

    public String getReceivers(String type)
    {
        return getReceivers(type, null);
    }

    public String getReceivers(String type, String excluedType)
    {
        DocumentReceiverList documentReceiverList = getReceiverList();
        if (documentReceiverList != null)
        {
            ReceiverListList receivers = documentReceiverList.getReceivers();
            if (receivers != null)
            {
                StringBuilder buffer = new StringBuilder();

                List<ReceiverList> receiverLists = receivers.getReceiverLists();
                if (receiverLists != null)
                {
                    for (ReceiverList receiverList : receiverLists)
                    {
                        String sendType = receiverList.getSendType();
                        List<Member> members = receiverList.getReceivers();
                        if (members != null)
                        {
                            for (Member member : members)
                            {
                                if ((type == null || type.equals(sendType)) &&
                                        (excluedType == null || !excluedType.equals(sendType)))
                                {
                                    if (buffer.length() > 0)
                                        buffer.append(",");

                                    buffer.append(member.getName());
                                }
                            }
                        }
                    }
                }

                return buffer.toString();
            }
        }

        return "";
    }

    public String getEncodedId()
    {
        return encodeId(documentId);
    }

    public static String encodeId(Long documentId)
    {
        if (documentId == null)
            return null;

        return IDEncoder.encode(documentId);
    }

    public static Long decodeId(String s) throws Exception
    {
        if (s == null)
            return null;

        return IDEncoder.decode(s);
    }
}
