package com.gzzm.portal.cms.visit;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;

import java.sql.Date;
import java.util.*;

/**
 * 栏目访问量，点击量统计
 *
 * @author sjy
 * @date 2018/3/16
 */
@Service(url = "/visit/channelVisitDayQuery")
public class ChannelVisitStatPage extends BaseTreeStat<Channel, Integer>
{

    private String topChannelCode;

    private Date visitDay_start;

    private Date visitDay_end;

    @Inject
    private VisitDao dao;

    @Override
    protected void initStats() throws Exception
    {
        join(VisitDayTotal.class, "v", "v.type=1 and v.objectId=channel.channelId and v.visitDay>=?visitDay_start and v.visitDay<addDay(?visitDay_end,1)");
        addColumn("channelName", "channel.channelName");
        addStat("visitCountNum", "nvl(sum(v.visitCount),0)");
        addStat("clickCountNum", "nvl(sum(v.clickCount),0)");
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeTableView view = new PageTreeTableView();
        view.addComponent("访问时间", "visitDay_start", "visitDay_end");
        view.addColumn("栏目名称", "channelName").setWidth("200").setAutoExpand(true);
        view.addColumn("访问量", "visitCountNum").setWidth("107");
        view.addColumn("点击量", "clickCountNum").setWidth("107");
        view.defaultInit();
        view.addButton(Buttons.export("xls"));
        return view;
    }

    @Override
    protected Integer getRootKey() throws Exception
    {
        return null;
    }

    @Override
    public Map<String, Object> getRoot() throws Exception
    {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put(KEY, 0);
        root.put("channelName", "");
        root.put("visitCountNum", 0);
        root.put("clickCountNum", 0);
        root.put(LEAF, false);
        return root;
    }

    @Override
    protected String getParentField() throws Exception
    {
        return "parentChannelId";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "channel.deleteTag=0 ";
    }

    @Override
    protected String getChildKeysCondition() throws Exception
    {
        return "deleteTag=0 order by leftValue";
    }

    @Override
    public String getAlias()
    {
        return "channel";
    }

    @Override
    protected List<Integer> getChildKeys(Integer parent) throws Exception
    {
        if (StringUtils.isNotBlank(topChannelCode) && parent == 0)
        {
            Integer channelId = dao.getChannelId(topChannelCode);
            if (channelId != null)
            {
                List<Integer> keys = new ArrayList<Integer>();
                keys.add(channelId);
                return keys;
            }
        }
        return super.getChildKeys(parent);
    }

    public Date getVisitDay_start()
    {
        return visitDay_start;
    }

    public void setVisitDay_start(Date visitDay_start)
    {
        this.visitDay_start = visitDay_start;
    }

    public Date getVisitDay_end()
    {
        return visitDay_end;
    }

    public void setVisitDay_end(Date visitDay_end)
    {
        this.visitDay_end = visitDay_end;
    }

    public String getTopChannelCode()
    {
        return topChannelCode;
    }

    public void setTopChannelCode(String topChannelCode)
    {
        this.topChannelCode = topChannelCode;
    }
}
