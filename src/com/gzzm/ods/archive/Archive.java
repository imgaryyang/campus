package com.gzzm.ods.archive;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.flow.OdFlowInstance;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.DateUtils;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * @author camel
 * @date 2016/5/10
 */
@Entity(table = "ODARCHIVE", keys = "archiveId")
@Indexes({
        @Index(columns = {"year", "deptId"}),
        @Index(columns = {"catalogId", "serial"})})
public class Archive
{
    @Generatable(length = 12)
    private Long archiveId;

    @ColumnDescription(type = "number(4)")
    private Integer year;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    private Integer catalogId;

    @NotSerialized
    private ArchiveCatalog catalog;

    private Long documentId;

    @NotSerialized
    private OfficeDocument document;

    /**
     * 关联的流程实例的ID
     */
    private Long instanceId;

    @NotSerialized
    private OdFlowInstance instance;

    /**
     * 发文或收文时间
     */
    private Date docTime;

    /**
     * 归档时间
     */
    private Date archiveTime;

    /**
     * 文件号
     */
    @ColumnDescription(type = "number(4)")
    private Integer serial;

    @ColumnDescription(type = "varchar(500)")
    private String title;

    @ColumnDescription(type = "varchar(250)")
    private String sendNumber;

    /**
     * 责任者，一般是来文单位名称
     */
    @ColumnDescription(type = "varchar(250)")
    private String author;

    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    /**
     * 操作归档的用户的ID
     */
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 保管期限
     */
    @ColumnDescription(type = "varchar(250)")
    private String timeLimit;

    /**
     * 页数
     */
    @ColumnDescription(type = "number(5)")
    private Integer pagesCount;

    public Archive()
    {
    }

    public Long getArchiveId()
    {
        return archiveId;
    }

    public void setArchiveId(Long archiveId)
    {
        this.archiveId = archiveId;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
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

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public ArchiveCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(ArchiveCatalog catalog)
    {
        this.catalog = catalog;
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public OdFlowInstance getInstance()
    {
        return instance;
    }

    public void setInstance(OdFlowInstance instance)
    {
        this.instance = instance;
    }

    public OfficeDocument getDocument()
    {
        return document;
    }

    public void setDocument(OfficeDocument document)
    {
        this.document = document;
    }

    public Date getDocTime()
    {
        return docTime;
    }

    public void setDocTime(Date docTime)
    {
        this.docTime = docTime;
    }

    public java.sql.Date getDocDate()
    {
        return DateUtils.toSQLDate(docTime);
    }

    public Date getArchiveTime()
    {
        return archiveTime;
    }

    public void setArchiveTime(Date archiveTime)
    {
        this.archiveTime = archiveTime;
    }

    public Integer getSerial()
    {
        return serial;
    }

    public void setSerial(Integer serial)
    {
        this.serial = serial;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public String getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit)
    {
        this.timeLimit = timeLimit;
    }

    public Integer getPagesCount()
    {
        return pagesCount;
    }

    public void setPagesCount(Integer pageCount)
    {
        this.pagesCount = pageCount;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Archive))
            return false;

        Archive archive = (Archive) o;

        return archiveId.equals(archive.archiveId);
    }

    @Override
    public int hashCode()
    {
        return archiveId.hashCode();
    }
}
