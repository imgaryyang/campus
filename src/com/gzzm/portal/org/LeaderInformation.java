package com.gzzm.portal.org;

import com.gzzm.portal.cms.information.Information;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;

/**
 * 领导关联的信息采编
 *
 * @author sjy
 * @date 2018/1/2
 */
@Entity(table = "PLLEADERINFORMATION", keys = {"leaderId", "informationId"})
public class LeaderInformation
{
    private Integer leaderId;

    @NotSerialized
    private Leader leader;

    private Long informationId;

    @NotSerialized
    private Information information;

    public Integer getLeaderId()
    {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId)
    {
        this.leaderId = leaderId;
    }

    public Leader getLeader()
    {
        return leader;
    }

    public void setLeader(Leader leader)
    {
        this.leader = leader;
    }

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public Information getInformation()
    {
        return information;
    }

    public void setInformation(Information information)
    {
        this.information = information;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof LeaderInformation))
            return false;

        LeaderInformation that = (LeaderInformation) o;

        return leaderId.equals(that.leaderId);
    }

    @Override
    public int hashCode()
    {
        return leaderId.hashCode();
    }
}
