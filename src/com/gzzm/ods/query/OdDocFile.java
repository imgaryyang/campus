package com.gzzm.ods.query;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 单位发文、收文文件表
 * 用于存储批量下载公文文件包
 *
 * @author LDP
 * @date 2015/11/27
 */
@Entity(table = "ODDOCFILE", keys = "docFileId")
public class OdDocFile
{
    @Generatable(length = 9)
    private Integer docFileId;

    /**
     * 导出文件的用户
     */
    @Index
    private Integer userId;

    @NotSerialized
    @ToOne("USERID")
    private User user;

    /**
     * 用户所属部门
     * 便于同一单位下的用户可看到相同的记录
     */
    private Integer deptId;

    @NotSerialized
    @ToOne("DEPTID")
    private Dept dept;

    /**
     * 文件类型
     * 0为发文簿；1为收文簿；2为发文json文件；3为收文json文件
     */
    @ColumnDescription(type = "number(1)")
    private Integer docFileType;

    /**
     * 导出操作时间
     */
    private Date actionTime;

    /**
     * 文件生成完成时间
     */
    private Date finishTime;

    /**
     * 文件生成状态
     * 0为文件生成中，1为生成完成，2为生成失败
     */
    @ColumnDescription(type = "number(1)")
    private Integer state;

    /**
     * 文件内容
     * 取消使用这个字段，由于文件内存都存到数据库了，导致迁移和备份数据库变得更加笨重
     */
    @Deprecated
    private InputStream content;

    /**
     * 文件描述
     * 批量导出公文是，录入相关描述，用于区分本记录所导出的是哪些公文
     */
    @Require
    @ColumnDescription(type = "varchar2(500)")
    private String remark;

    /**
     * 附件ID，关联框架附件表
     * 用于代替content字段，将生成的文件保存到框架附件表中，附件表会根据系统配置自动识别文件保存在硬盘还是数据库中
     */
    @ColumnDescription(type = "number(12)", nullable = true)
    private Long attachmentId;

    public OdDocFile()
    {
    }

    public Integer getDocFileId()
    {
        return docFileId;
    }

    public void setDocFileId(Integer docFileId)
    {
        this.docFileId = docFileId;
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

    public Integer getDocFileType()
    {
        return docFileType;
    }

    public void setDocFileType(Integer docFileType)
    {
        this.docFileType = docFileType;
    }

    public Date getActionTime()
    {
        return actionTime;
    }

    public void setActionTime(Date actionTime)
    {
        this.actionTime = actionTime;
    }

    public Date getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(Date finishTime)
    {
        this.finishTime = finishTime;
    }

    @Deprecated
    public InputStream getContent()
    {
        return content;
    }

    @Deprecated
    public void setContent(InputStream content)
    {
        this.content = content;
    }

    public Integer getState()
    {
        return state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof OdDocFile)) return false;

        OdDocFile docFile = (OdDocFile) o;

        if(!docFileId.equals(docFile.docFileId)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return docFileId.hashCode();
    }
}
