package com.gzzm.portal.org;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.ChannelDisplay;
import com.gzzm.portal.cms.information.Information;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Null;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author sjy
 * @date 2018/1/2
 */
@Service(url = "/portal/org/leaderRelateInformation")
public class LeaderInformationCrud extends BaseQueryCrud<Information, Long>
{
    @Inject
    private OrgInfoDao dao;

    @NotCondition
    private Integer channelId;

    @Like
    private String title;

    private Integer leaderId;

    private Leader leader;

    @Require
    private boolean related = false;

    protected String getComplexCondition() throws Exception
    {
        String condition = "valid=1";
        if (channelId != null && !Null.isNull(channelId))
        {
            //查询此栏目的所有子栏目
            condition +=
                    " and channel.channelId in (select c.channelId from com.gzzm.portal.cms.channel.Channel c,com.gzzm.portal.cms.channel.Channel cc where c.leftValue>=cc.leftValue and c.leftValue<cc.rightValue and cc.channelId=:channelId)";
        }
        if (related)
        {
            condition +=
                    " and informationId in (select relate.informationId from com.gzzm.portal.org.LeaderInformation relate where relate.leaderId=:leaderId)";
        }
        return condition;
    }

    public LeaderInformationCrud()
    {
        addOrderBy("updateTime", OrderType.desc);
    }

    public boolean relateInformation(Long informationId) throws Exception
    {
        return dao.getLeaderInformation(leaderId, informationId) != null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ChannelDisplay channelDisplay = Tools.getBean(ChannelDisplay.class);
        PageTableView view = new ComplexTableView(channelDisplay, "channelId");
        view.setTitle(getLeader().getLeaderName() + "-关联信息采编");
        view.addComponent("标题", "title");
        view.addComponent("已关联", "related");
        view.addColumn("所属栏目", "channel.channelName").setOrderFiled("channel.leftValue").setWidth("150");
        view.addColumn("标题",
                new CHref("${title}", "/portal/org/info_preview.ptl?infoId=${informationId}").setTarget("_blank"))
                .setAlign(Align.left);
        view.addColumn("关联领导", new ConditionComponent().add("!crud$.relateInformation(this.informationId)",
                new CButton("关联", "settingInfo(${informationId},true);").setWidth("60px"))
                .add(new CButton("取消关联", "settingInfo(${informationId},false);").setWidth("60px"))).setWidth("70");
        view.addButton(Buttons.query());
        view.addButton(new CButton("关联", "settingInfos(true)").setIcon(Buttons.getIcon("config")));
        view.addButton(new CButton("取消关联", "settingInfos(false)").setIcon(Buttons.getIcon("cancel")));
        view.importJs("/portal/org/leader.js");
        return view;
    }

    @Service
    @ObjectResult
    @Transactional
    public void relateLeadersServer(boolean relate) throws Exception
    {
        Long[] informationIds = getKeys();
        if (informationIds == null || informationIds.length == 0)
        {
            return;
        }
        if (relate)
        {
            for (Long informationId : informationIds)
            {
                LeaderInformation r = new LeaderInformation();
                r.setInformationId(informationId);
                r.setLeaderId(leaderId);
                dao.save(r);
            }
        }
        else
        {
            dao.deleteLeaderInformations(leaderId, informationIds);
        }
    }

    @Service
    @ObjectResult
    @Transactional
    public void relateLeaderServer(Long informationId, boolean relate) throws Exception
    {
        if (relate)
        {
            LeaderInformation r = new LeaderInformation();
            r.setInformationId(informationId);
            r.setLeaderId(leaderId);
            dao.save(r);
        }
        else
        {
            dao.deleteLeaderInformation(leaderId, informationId);
        }
    }

    @NotSerialized
    private Leader getLeader() throws Exception
    {
        if (leader == null)
        {
            leader = dao.load(Leader.class, leaderId);
        }
        return leader;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getLeaderId()
    {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId)
    {
        this.leaderId = leaderId;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public boolean isRelated()
    {
        return related;
    }

    public void setRelated(boolean related)
    {
        this.related = related;
    }
}
