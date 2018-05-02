package com.gzzm.portal.eval;

import net.cyan.thunwind.annotation.*;

/**
 * 评价选项表
 *
 * @author sjy
 * @date 2018/2/23
 */
@Entity(table = "PLEVALOPTION", keys = "optionId")
public class EvalOption
{
    @Generatable(length = 5)
    private Integer optionId;

    /**
     * 选项名称
     */
    @ColumnDescription(type = "varchar(100)")
    private String optionName;

    /**
     * 类型id
     */
    private Integer typeId;

    private EvalType type;

    /**
     * 得分
     */
    @ColumnDescription(type = "number(5,2)", nullable = true)
    private Float score;

    /**
     * 删除标识
     */
    @ColumnDescription(defaultValue = "0")
    private Boolean deleteTag;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public Integer getOptionId()
    {
        return optionId;
    }

    public void setOptionId(Integer optionId)
    {
        this.optionId = optionId;
    }

    public String getOptionName()
    {
        return optionName;
    }

    public void setOptionName(String optionName)
    {
        this.optionName = optionName;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public EvalType getType()
    {
        return type;
    }

    public void setType(EvalType type)
    {
        this.type = type;
    }

    public Float getScore()
    {
        return score;
    }

    public void setScore(Float score)
    {
        this.score = score;
    }

    public Boolean getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Boolean deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof EvalOption))
        {
            return false;
        }

        EvalOption that = (EvalOption) o;

        return optionId.equals(that.optionId);

    }

    @Override
    public int hashCode()
    {
        return optionId.hashCode();
    }

    @Override
    public String toString()
    {
        return optionName;
    }
}
