package com.gzzm.oa.vote;

/**
 * 投票输入数据的统计信息
 *
 * @author camel
 * @date 12-2-7
 */
public class VoteInputStat
{
    /**
     * 数量
     */
    private Integer count;

    /**
     * 平均值
     */
    private Integer avg;

    /**
     * 最大值
     */
    private Integer max;

    /**
     * 最小值
     */
    private Integer min;

    public VoteInputStat()
    {
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }

    public Integer getAvg()
    {
        return avg;
    }

    public void setAvg(Integer avg)
    {
        this.avg = avg;
    }

    public Integer getMax()
    {
        return max;
    }

    public void setMax(Integer max)
    {
        this.max = max;
    }

    public Integer getMin()
    {
        return min;
    }

    public void setMin(Integer min)
    {
        this.min = min;
    }
}
