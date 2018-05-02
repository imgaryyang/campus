package com.gzzm.platform.form;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 表单记录
 *
 * @author camel
 * @date 11-9-15
 */
@Entity(table = "PFFORMRECORD", keys = "recordId")
public class FormRecord
{
    /**
     * 主键，recordId
     */
    @Generatable(length = 12)
    private Long recordId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 关联的表单数据对象
     */
    private Long bodyId;

    @NotSerialized
    private FormBody body;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建用户
     */
    private Integer userId;

    /**
     * 关联创建用户对象
     */
    @NotSerialized
    private User user;

    /**
     * 所属部门
     */
    private Integer deptId;

    /**
     * 关联所属部门对象
     */
    @NotSerialized
    private Dept dept;

    public FormRecord()
    {
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public FormBody getBody()
    {
        return body;
    }

    public void setBody(FormBody body)
    {
        this.body = body;
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

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FormRecord))
            return false;

        FormRecord that = (FormRecord) o;

        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }
}
