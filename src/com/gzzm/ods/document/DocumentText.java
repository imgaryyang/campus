package com.gzzm.ods.document;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.storage.CommonFileColumn;

import java.io.InputStream;
import java.util.Date;

/**
 * 公文正文，和OfficeDocumet分开放，避免行过宽影响查询性能
 *
 * @author camel
 * @date 11-9-21
 */
@Entity(table = "ODDOCUMENTTEXT", keys = "textId")
public class DocumentText
{
    /**
     * 正文ID
     */
    @Generatable(length = 12)
    private Long textId;

    /**
     * 正文内容
     */
    @CommonFileColumn(pathColumn = "textFilePath", target = "{target}", defaultTarget = "od",
            path = "{yyyyMM}/{yyyyMMdd}/od/text/{textId}_text", clear = true)
    private InputStream textBody;

    /**
     * 正文内容保存的路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String textFilePath;

    /**
     * 正文类型，如doc等
     */
    @ColumnDescription(type = "varchar(20)")
    private String type;

    /**
     * 当前编辑者，如果为空表示没有人在编辑
     */
    private Integer editor;

    @ToOne("EDITOR")
    private User editorUser;

    /**
     * 最后的编辑时间
     */
    private Date editTime;

    /**
     * 创建文本的步骤，可以为空
     */
    @ColumnDescription(type = "number(13)")
    private Long stepId;

    /**
     * 文件大小
     */
    @ColumnDescription(type = "number(12)")
    private Long fileSize;

    /**
     * 未盖章的正文
     */
    @CommonFileColumn(pathColumn = "unsealedFilePath", target = "{target}", defaultTarget = "od",
            path = "{yyyyMM}/{yyyyMMdd}/od/text/{textId}_unsealed", clear = true)
    private InputStream unsealedText;

    /**
     * 未盖章正文内容保存的路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String unsealedFilePath;

    @ColumnDescription(type = "varchar(520)")
    private String otherFileName;

    @CommonFileColumn(pathColumn = "otherFilePath", target = "{target}", defaultTarget = "od",
            path = "{yyyyMM}/{yyyyMMdd}/od/text/{textId}_other", clear = true)
    private InputStream otherFile;

    /**
     * 其他格式正文内容保存的路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String otherFilePath;

    /**
     * 文件大小
     */
    @ColumnDescription(type = "number(12)")
    private Long otherFileSize;

    /**
     * 文件保存在哪个文件服务上，调用哪个文件服务读取文件
     */
    @ColumnDescription(type = "varchar(50)")
    private String target;

    public DocumentText()
    {
    }

    public Long getTextId()
    {
        return textId;
    }

    public void setTextId(Long textId)
    {
        this.textId = textId;
    }

    public InputStream getTextBody()
    {
        return textBody;
    }

    public void setTextBody(InputStream textBody)
    {
        this.textBody = textBody;
    }

    public String getTextFilePath()
    {
        return textFilePath;
    }

    public void setTextFilePath(String textFilePath)
    {
        this.textFilePath = textFilePath;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getEditor()
    {
        return editor;
    }

    public void setEditor(Integer editor)
    {
        this.editor = editor;
    }

    public User getEditorUser()
    {
        return editorUser;
    }

    public void setEditorUser(User editorUser)
    {
        this.editorUser = editorUser;
    }

    public Date getEditTime()
    {
        return editTime;
    }

    public void setEditTime(Date editTime)
    {
        this.editTime = editTime;
    }

    public Long getStepId()
    {
        return stepId;
    }

    public void setStepId(Long stepId)
    {
        this.stepId = stepId;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public InputStream getUnsealedText()
    {
        return unsealedText;
    }

    public void setUnsealedText(InputStream unsealedText)
    {
        this.unsealedText = unsealedText;
    }

    public String getUnsealedFilePath()
    {
        return unsealedFilePath;
    }

    public void setUnsealedFilePath(String unsealedFilePath)
    {
        this.unsealedFilePath = unsealedFilePath;
    }

    public String getOtherFileName()
    {
        return otherFileName;
    }

    public void setOtherFileName(String otherFileName)
    {
        this.otherFileName = otherFileName;
    }

    public InputStream getOtherFile()
    {
        return otherFile;
    }

    public void setOtherFile(InputStream otherFile)
    {
        this.otherFile = otherFile;
    }

    public String getOtherFilePath()
    {
        return otherFilePath;
    }

    public void setOtherFilePath(String otherFilePath)
    {
        this.otherFilePath = otherFilePath;
    }

    public Long getOtherFileSize()
    {
        return otherFileSize;
    }

    public void setOtherFileSize(Long otherFileSize)
    {
        this.otherFileSize = otherFileSize;
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

        if (!(o instanceof DocumentText))
            return false;

        DocumentText that = (DocumentText) o;

        return textId.equals(that.textId);
    }

    @Override
    public int hashCode()
    {
        return textId.hashCode();
    }
}
