package com.gzzm.oa.vote;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 选项输入实体
 *
 * @author db
 * @date 11-12-1
 */
@Entity(table = "OAVOTEINPUT", keys = {"recordId", "problemId"})
@Indexes({
        @Index(columns = {"PROBLEMID", "NUMBERVALUE"})
})
public class VoteInput
{
    /**
     * 投票记录ID
     * 关联投票记录实体
     */
    @Index
    private Integer recordId;

    @Cascade
    @NotSerialized
    private VoteRecord record;

    /**
     * 投票问题ID
     * 关联投票问题实体
     */
    private Integer problemId;

    @NotSerialized
    private VoteProblem voteProblem;

    /**
     * 输入内容，对于文件，则保留文件名
     */
    @ColumnDescription(type = "varchar(4000)")
    private String inputContent;

    /**
     * 数值值
     */
    @ColumnDescription(type = "number(13,4)")
    private Float numberValue;

    /**
     * 时间值
     */
    private Date dateValue;

    /**
     * 文件
     */
    private InputStream fileContent;

    public VoteInput()
    {
    }

    public Integer getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Integer recordId)
    {
        this.recordId = recordId;
    }

    public VoteRecord getRecord()
    {
        return record;
    }

    public void setRecord(VoteRecord record)
    {
        this.record = record;
    }

    public Integer getProblemId()
    {
        return problemId;
    }

    public void setProblemId(Integer problemId)
    {
        this.problemId = problemId;
    }

    public VoteProblem getVoteProblem()
    {
        return voteProblem;
    }

    public void setVoteProblem(VoteProblem voteProblem)
    {
        this.voteProblem = voteProblem;
    }

    public String getInputContent()
    {
        return inputContent;
    }

    public void setInputContent(String inputContent)
    {
        this.inputContent = inputContent;
    }

    public Float getNumberValue()
    {
        return numberValue;
    }

    public void setNumberValue(Float numberValue)
    {
        this.numberValue = numberValue;
    }

    public Date getDateValue()
    {
        return dateValue;
    }

    public void setDateValue(Date dateValue)
    {
        this.dateValue = dateValue;
    }

    public InputStream getFileContent()
    {
        return fileContent;
    }

    public void setFileContent(InputStream fileContent)
    {
        this.fileContent = fileContent;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof VoteInput))
            return false;

        VoteInput that = (VoteInput) o;

        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }
}
