package com.gzzm.oa.vote;

import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 12-3-30
 */
@Entity(table = "OAVOTEOPTIONINPUT", keys = {"recordId", "optionId"})
public class VoteOptionInput
{
    private Integer recordId;

    private VoteRecord record;

    private Integer optionId;

    private VoteOption option;

    /**
     * 输入内容
     */
    @ColumnDescription(type = "varchar(250)")
    private String inputContent;

    public VoteOptionInput()
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

    public Integer getOptionId()
    {
        return optionId;
    }

    public void setOptionId(Integer optionId)
    {
        this.optionId = optionId;
    }

    public VoteRecord getRecord()
    {
        return record;
    }

    public void setRecord(VoteRecord record)
    {
        this.record = record;
    }

    public VoteOption getOption()
    {
        return option;
    }

    public void setOption(VoteOption option)
    {
        this.option = option;
    }

    public String getInputContent()
    {
        return inputContent;
    }

    public void setInputContent(String inputContent)
    {
        this.inputContent = inputContent;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof VoteOptionInput))
            return false;

        VoteOptionInput that = (VoteOptionInput) o;

        return optionId.equals(that.optionId) && recordId.equals(that.recordId);
    }

    @Override
    public int hashCode()
    {
        int result = recordId.hashCode();
        result = 31 * result + optionId.hashCode();
        return result;
    }
}
