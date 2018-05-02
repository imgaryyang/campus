package com.gzzm.oa.vote;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CImage;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * 超时列表
 *
 * @author camel
 * @date 12-3-27
 */
@Service(url = "/oa/vote/VoteScope")
public class VoteScopeQuery extends BaseQueryCrud<VoteScope, String>
{
    @Inject
    private VoteDao dao;

    /**
     * 用投票类型ID过滤
     */
    @Equals("vote.typeId")
    private Integer typeId;

    private VoteType type;

    @NotSerialized
    @AuthDeptIds
    @In("vote.deptId")
    private Collection<Integer> authDeptIds;

    private Integer day1;

    private Integer day2;

    @Like("vote.title")
    private String title;

    @Like
    private String objectName;

    /**
     * 关联的投票的ID
     */
    private Integer voteId;

    @NotSerialized
    private Vote vote;

    /**
     * 未投票
     */
    private boolean notVoted = true;

    private String tag;

    public VoteScopeQuery()
    {
        addOrderBy("vote.startTime", OrderType.desc);
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getDay1()
    {
        return day1;
    }

    public void setDay1(Integer day1)
    {
        this.day1 = day1;
    }

    public Integer getDay2()
    {
        return day2;
    }

    public void setDay2(Integer day2)
    {
        this.day2 = day2;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isNotVoted()
    {
        return notVoted;
    }

    public void setNotVoted(boolean notVoted)
    {
        this.notVoted = notVoted;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    protected VoteType getType() throws Exception
    {
        if (voteId != null && typeId == null)
            typeId = getVote().getTypeId();

        if (type == null && typeId != null)
            type = dao.getVoteType(typeId);

        return type;
    }

    @Override
    public String getKey(VoteScope entity) throws Exception
    {
        return entity.getVoteId() + "_" + entity.getType() + "_" + entity.getObjectId();
    }

    @NotSerialized
    public Vote getVote() throws Exception
    {
        if (vote == null)
            vote = dao.getVoteById(voteId);
        return vote;
    }

    @NotSerialized
    public String getActionName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getActionName();
    }

    @NotSerialized
    public String getTypeName() throws Exception
    {
        VoteType type = getType();

        if (type == null)
            return "投票";

        return type.getTypeName();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder buffer = null;

        if (notVoted)
        {
            buffer = new StringBuilder("(select r from VoteRecord r where " +
                    "r.voteId=votescope.voteId and (r.deptId=objectId and type=0 " +
                    "or r.userId=objectId and type=1) and r.state=1) is empty");
        }

        if (day1 != null && day1 >= 0)
        {
            if (buffer == null)
                buffer = new StringBuilder();
            else
                buffer.append(" and ");

            buffer.append("addday(vote.alarmTime,").append(day1 + 1).append(")<nvl(voteTime,sysdate())");
        }

        if (day2 != null && day2 > 0)
        {
            if (buffer == null)
                buffer = new StringBuilder();
            else
                buffer.append(" and ");

            buffer.append(" and addday(vote.alarmTime,").append(day2 + 1).append(")>nvl(voteTime,sysdate())");
        }

        return buffer == null ? null : buffer.toString();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        if (voteId != null && notVoted)
        {
            Vote vote = getVote();
            view.setTitle("未" + getActionName() + "列表-" + vote.getTitle());
        }

        if (voteId == null)
            view.addComponent("标题", "title");

        view.addComponent("对象名称", "objectName");

        if (tag != null)
        {
            String icon = Buttons.getIcon(tag);
            if (icon != null)
                view.addColumn("", new CImage(icon)).setLocked(true);
        }

        if (voteId == null)
        {
            view.addColumn(getTypeName() + "标题", "vote.title");
            view.addColumn(getActionName() + "对象", "objectName").setWidth("250");
        }
        else
        {
            view.addColumn(getActionName() + "对象", "objectName");
        }

        view.addColumn("警告时间", "vote.alarmTime");
        view.addColumn("截止时间", "vote.endTime");
        if (!notVoted)
            view.addColumn(getActionName() + "时间", "voteTime");

        view.defaultInit();

        return view;
    }
}
