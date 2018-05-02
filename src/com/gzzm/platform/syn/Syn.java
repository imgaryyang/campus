package com.gzzm.platform.syn;

import java.util.Date;

/**
 * @author camel
 * @date 2011-4-19
 */
public class Syn
{
    /**
     * 同步ID，主键
     */
    private Long synId_;

    /**
     * 同步动作，0为增加，1为修改，2为删除
     */
    private Integer synAction_;

    /**
     * 同步状态，0为未同步，1为已同步
     */
    private Integer synState_;

    /**
     * 数据修改时间
     */
    private Date synTime_;

    public Syn()
    {
    }

    public Long getSynId_()
    {
        return synId_;
    }

    public void setSynId_(Long synId_)
    {
        this.synId_ = synId_;
    }

    public Integer getSynAction_()
    {
        return synAction_;
    }

    public void setSynAction_(Integer synAction_)
    {
        this.synAction_ = synAction_;
    }

    public Date getSynTime_()
    {
        return synTime_;
    }

    public void setSynTime_(Date synTime_)
    {
        this.synTime_ = synTime_;
    }

    public Integer getSynState_()
    {
        return synState_;
    }

    public void setSynState_(Integer synState_)
    {
        this.synState_ = synState_;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Syn))
            return false;

        Syn syn = (Syn) o;

        return synId_.equals(syn.synId_);
    }

    @Override
    public int hashCode()
    {
        return synId_.hashCode();
    }
}
