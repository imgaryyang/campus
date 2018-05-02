package com.gzzm.platform.form;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

/**
 * 表单数据表，每个表单数据是表单的一个实例
 *
 * @author camel
 * @date 11-8-31
 */
@Entity(table = "PFFORMBODY", keys = "bodyId")
public class FormBody
{
    /**
     * 表单数据ID
     */
    @Generatable(length = 12)
    private Long bodyId;

    /**
     * 表单ID
     *
     * @see com.gzzm.platform.form.FormInfo#formId
     */
    @ColumnDescription(type = "number(7)")
    private Integer formId;

    /**
     * 数据内容
     */
    @CommonFileColumn(pathColumn = "filePath", target = "{target}", defaultTarget = "form", path = "form", clear = true)
    private char[] content;

    /**
     * 关联的form对象
     */
    private FormInfo formInfo;

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

    public FormBody()
    {
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public Integer getFormId()
    {
        return formId;
    }

    public void setFormId(Integer formId)
    {
        this.formId = formId;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public FormInfo getFormInfo()
    {
        return formInfo;
    }

    public void setFormInfo(FormInfo formInfo)
    {
        this.formInfo = formInfo;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FormBody))
            return false;

        FormBody that = (FormBody) o;

        return bodyId.equals(that.bodyId);
    }

    @Override
    public int hashCode()
    {
        return bodyId.hashCode();
    }
}
