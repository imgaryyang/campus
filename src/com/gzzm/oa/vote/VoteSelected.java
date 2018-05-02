package com.gzzm.oa.vote;

import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 12-2-2
 */
@Entity(table = "OAVOTESELECTED", keys = {"recordId", "optionId"})
public class VoteSelected
{
    private Integer recordId;

    @Cascade
    private VoteRecord record;

    @Index
    private Integer optionId;

    @Cascade
    private VoteOption voteOption;

    public VoteSelected()
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

    public Integer getOptionId()
    {
        return optionId;
    }

    public void setOptionId(Integer optionId)
    {
        this.optionId = optionId;
    }

    public VoteOption getVoteOption()
    {
        return voteOption;
    }

    public void setVoteOption(VoteOption voteOption)
    {
        this.voteOption = voteOption;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof VoteSelected))
            return false;

        VoteSelected that = (VoteSelected) o;

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
