package com.gzzm.portal.cms.stat;

import com.gzzm.platform.appauth.AppAuthService;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.util.JoinType;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 根据栏目和部门统计信息发布量
 *
 * @author camel
 * @date 13-4-12
 */
@Service(url = "/portal/stat/information")
public class InformationStat extends DeptStat
{
    @Inject
    private AppAuthService appAuthService;

    @Inject
    private ChannelTree channelTree;

    private List<Integer> channelIds;

    private List<Integer> topChannelIds;

    private Integer rootId;

    @Lower
    private Date time_start;

    @Upper
    private Date time_end;

    private AuthChannelTreeModel channelTreeModel;

    /**
     * true表示只显示采编的部门，false表示显示所有部门
     */
    @Require
    private boolean publishOnly = true;

    private int pageSize;

    public InformationStat()
    {
    }

    @Override
    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public List<Integer> getChannelIds()
    {
        return channelIds;
    }

    public void setChannelIds(List<Integer> channelIds)
    {
        this.channelIds = channelIds;
    }

    public List<Integer> getTopChannelIds()
    {
        return topChannelIds;
    }

    public void setTopChannelIds(List<Integer> topChannelIds)
    {
        this.topChannelIds = topChannelIds;
    }

    public Integer getRootId()
    {
        return rootId;
    }

    public void setRootId(Integer rootId)
    {
        this.rootId = rootId;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public boolean isPublishOnly()
    {
        return publishOnly;
    }

    public void setPublishOnly(boolean publishOnly)
    {
        this.publishOnly = publishOnly;
    }

    @Select(field = {"topChannelIds", "channelIds"})
    public AuthChannelTreeModel getChannelTreeModel() throws Exception
    {
        if (channelTreeModel == null)
        {
            channelTreeModel = Tools.getBean(AuthChannelTreeModel.class);
            channelTreeModel.setHasCheckBox(true);
            channelTreeModel.setEditType(EditType.edit);

            if (rootId != null)
                channelTreeModel.setRootId(rootId);
        }

        return channelTreeModel;
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if ("exp".equals(getAction()))
        {
            setPageSize(0);
        }
        else if (pageSize <= 0)
        {
            setPageSize(100);
        }

        List<Integer> channelIds = getChannelIds();

        if (channelIds == null)
        {
            if (topChannelIds != null)
            {
                channelIds = new ArrayList<Integer>();
                for (Integer topChannelId : topChannelIds)
                {
                    channelIds.addAll(channelTree.getChannel(topChannelId).getAllChildrenChannelIds());
                }
                setChannelIds(channelIds);
            }
            else if (rootId != null)
            {
                channelIds = channelTree.getChannel(rootId).getAllChildrenChannelIds();
                setChannelIds(channelIds);
            }
            else
            {
                channelIds = channelTree.getRoot().getAllChildrenChannelIds();
            }
        }

        if (!publishOnly)
        {
            Collection<Integer> deptIds =
                    appAuthService.getDeptIdsForAuths(ChannelAuthCrud.PORTAL_CHANNEL_EDIT, channelIds);

            Collection<Integer> queryDeptIds = getQueryDeptIds();
            if (queryDeptIds == null)
            {
                setQueryDeptIds(deptIds);
            }
            else
            {
                deptIds.retainAll(queryDeptIds);
                setQueryDeptIds(deptIds);
            }
        }
    }

    @Override
    protected void initStats() throws Exception
    {
        join(Information.class, "i",
                "i.deptId=dept.deptId and i.publishTime>?time_start and i.publishTime<=?time_end and i.channelId in ?channelIds",
                publishOnly ? JoinType.inner : JoinType.left);

        addStat("deptId", "dept.deptId", "0");
        addStat("deptName", "min(dept.deptName)", "'合计'");
        addStat("deptSort", "min(dept.leftValue)", "'排序'");
        addStat("publishCount", "count(i.informationId)");

        setGroupField("dept.deptId");

        initOrders();
    }

    @Override
    protected void initOrders() throws Exception
    {
        addOrderBy("count(i.informationId)", OrderType.desc);
        addOrderBy("min(dept.leftValue)");
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();

        Map<String, Object> total = getTotal();
        if (total != null)
            getList().add(0, total);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("开始时间", "time_start");
        view.addComponent("结束时间", "time_end");
        view.addComponent("栏目", "topChannelIds");
        view.addComponent("部门", "topDeptIds");
        view.addComponent("只显示采编的部门", "publishOnly");

        view.addColumn("采编部门", "deptName").setOrderFiled("deptSort");
        view.addColumn("发布数量", "publishCount");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));

        return view;
    }
}
