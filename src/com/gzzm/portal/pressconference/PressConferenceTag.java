package com.gzzm.portal.pressconference;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.tag.EntityQueryTag;

/**
 * 查询新闻发布信息的标签
 *
 * @author cqj
 * @date 2017-2-20
 */
@Tag(name = "conference")
public class PressConferenceTag extends EntityQueryTag<PressConference, Integer>
{

    public PressConferenceTag()
    {
    }

    private String order = "releaseDate desc";

    /**
     * 新闻发布会Id
     */
    private Integer conferenceId;

    private PressConferenceState state;

    @Override
    protected String getQueryString() throws Exception
    {
        String oql = "select t from PressConference t where t.state=2 ";
        if (conferenceId != null)
        {
            oql += " and t.conferenceId=?conferenceId ";
        }
        oql += " order by " + order;

        return oql;
    }

    public Integer getConferenceId()
    {
        return conferenceId;
    }

    public void setConferenceId(Integer conferenceId)
    {
        this.conferenceId = conferenceId;
    }

    public PressConferenceState getState()
    {
        return state;
    }

    public void setState(PressConferenceState state)
    {
        this.state = state;
    }

    public String getOrder()
    {
        return order;
    }

    public void setOrder(String order)
    {
        this.order = order;
    }
}
