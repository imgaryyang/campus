package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.chart.ChartType;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 11-9-28
 */
@Service(url = "/message/sms/vote/reply/stat")
public class SmsVoteReplyStat extends BaseStatCrud<SmsVoteReply>
{
    @Inject
    private SmsDao dao;

    private Integer voteId;

    public SmsVoteReplyStat()
    {
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
    }

    @Override
    protected void initStats() throws Exception
    {
        setGroupField("voteValue");

        addStat("voteValue", "voteValue");
        addStat("count", "count(*)");

        addOrderBy("voteValue");
    }

    @Override
    protected void afterQuery() throws Exception
    {
        SmsVote vote = dao.getVote(voteId);

        List<Map<String, Object>> list = getList();

        for (Map<String, Object> record : list)
        {
            Integer voteValue = (Integer) record.get("voteValue");

            record.put("text", vote.getValueText(voteValue));
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageChartView view = new PageChartView();

        view.setTitle("短信投票结果");
        view.importJs("/platform/message/smsvote.js");

        SmsVote vote = dao.getVote(voteId);
        view.setTitle(vote.getTitle());

        view.setType(ChartType.pie);

        view.setKey("voteValue");
        view.setName("text");
        view.addSerie("投票数", "count");

        return view;
    }
}
