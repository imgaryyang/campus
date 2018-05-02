package com.gzzm.platform.form;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.util.Date;

/**
 * 备份表单的每一次修改记录
 *
 * @author camel
 * @date 2018/1/12
 */
@Entity(table = "PFFORMBODYBAK", keys = "bakId")
public class FormBodyBak
{
    @Generatable(length = 12)
    private Long bakId;

    /**
     * 表单数据ID
     */
    private Long bodyId;

    @NotSerialized
    private FormBody formBody;

    /**
     * 数据内容
     */
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "form",
            path = "bak/form/{yyyyMM}/{yyyyMMdd}/{bodyId}_{bakId}", clear = true)
    private char[] content;

    /**
     * 表单数据内容保存的路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String filePath;

    /**
     * 表单数据内容保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    private String target;

    /**
     * 更新时间
     */
    private Date updateTime;

    private Integer userId;

    @NotSerialized
    private User user;

    public FormBodyBak()
    {
    }

    public Long getBakId()
    {
        return bakId;
    }

    public void setBakId(Long bakId)
    {
        this.bakId = bakId;
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public FormBody getFormBody()
    {
        return formBody;
    }

    public void setFormBody(FormBody formBody)
    {
        this.formBody = formBody;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FormBodyBak))
            return false;

        FormBodyBak that = (FormBodyBak) o;

        return bakId.equals(that.bakId);
    }

    @Override
    public int hashCode()
    {
        return bakId.hashCode();
    }
}
