package com.gzzm.portal.cms.stat;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.appauth.AppAuthService;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptTreeModel;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Filter;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 按栏目统计
 *
 * @author camel
 * @date 2014/11/4
 */
@Service(url = "/portal/stat/channel/information")
public class InformationChannelStat extends BaseTreeStat<Channel, Integer>
        implements DeptOwnedCrud, Filter<ChannelCache>
{
    private Integer rootId = 0;

    @Inject
    private ChannelTree channelTree;

    @Inject
    private AppAuthService appAuthService;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Lower
    private Date time_start;

    @Upper
    private Date time_end;

    /**
     * 根级部门的id，表示查询此部门的所有子部门的数据
     */
    private Collection<Integer> topDeptIds;


    /**
     * 部门id列表，可同时查询多个部门
     */
    @NotSerialized
    private List<Integer> deptIds;

    /**
     * 拥有权限的部门列表，通过setAuthDeptIds注入，以方便子类覆盖注入方式
     */
    @NotSerialized
    private Collection<Integer> authDeptIds;

    /**
     * 部门id列表，可同时查询多个部门
     */
    @NotSerialized
    private Collection<Integer> queryDeptIds;

    /**
     * 用部门id作查询条件
     */
    private Integer deptId;

    private List<Integer> topChannelIds;

    protected AuthDeptTreeModel deptTree;

    private AuthChannelTreeModel channelTreeModel;

    /**
     * 拥有权限的栏目的id
     */
    private Collection<Integer> authChannelIds;

    public InformationChannelStat()
    {
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

    public Collection<Integer> getTopDeptIds()
    {
        return topDeptIds;
    }

    public void setTopDeptIds(Collection<Integer> topDeptIds)
    {
        this.topDeptIds = topDeptIds;
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @AuthDeptIds
    protected void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
    }

    public Collection<Integer> getQueryDeptIds()
    {
        return queryDeptIds;
    }

    public void setQueryDeptIds(Collection<Integer> queryDeptIds)
    {
        this.queryDeptIds = queryDeptIds;
    }

    public List<Integer> getDeptIds()
    {
        return deptIds;
    }

    public void setDeptIds(List<Integer> deptIds)
    {
        this.deptIds = deptIds;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public List<Integer> getTopChannelIds()
    {
        return topChannelIds;
    }

    public void setTopChannelIds(List<Integer> topChannelIds)
    {
        this.topChannelIds = topChannelIds;
    }

    private Collection<Integer> getAuthChannelIds() throws Exception
    {
        if (authDeptIds == null && deptId == null)
            return null;

        if (authChannelIds == null)
        {
            Collection<Integer> authDeptIds;

            if (deptId != null)
                authDeptIds = Collections.singleton(deptId);
            else if (queryDeptIds != null)
                authDeptIds = queryDeptIds;
            else
                authDeptIds = this.authDeptIds;

            authChannelIds = appAuthService.getAppIds(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(),
                    authDeptIds, ChannelAuthCrud.PORTAL_CHANNEL_EDIT);
        }

        return authChannelIds;
    }

    @Override
    protected String getParentField() throws Exception
    {
        return "parentChannelId";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "channel.deleteTag=0";
    }

    @Override
    protected Integer getRootKey() throws Exception
    {
        return rootId;
    }

    @Override
    public Map<String, Object> getRoot() throws Exception
    {
        Map<String, Object> root = new HashMap<String, Object>();

        root.put(KEY, -1);
        root.put("channelName", "");
        root.put("publishCount", 0);
        root.put(LEAF, false);

        return root;
    }

    @Override
    protected Boolean isLeaf(Integer key) throws Exception
    {
        return super.isLeaf(key);
    }

    @Override
    protected List<Integer> getChildKeys(Integer parent) throws Exception
    {
        Collection<Integer> authChannelIds = getAuthChannelIds();

        if (parent == -1)
        {
            if (topChannelIds == null)
            {
                Integer rootId = this.rootId;

                while (true)
                {
                    ChannelCache rootChannel = channelTree.getChannel(rootId);
                    List<Integer> childChannelIds = new ArrayList<Integer>();
                    boolean real = false;

                    for (ChannelCache channel : rootChannel.getChildren())
                    {
                        if ((authChannelIds == null || authChannelIds.contains(channel.getChannelId())) &&
                                channel.getType() == ChannelType.information)
                        {
                            childChannelIds.add(channel.getChannelId());
                            real = true;
                        }
                        else if (channel.containsChildChannel(this))
                        {
                            childChannelIds.add(channel.getChannelId());
                        }
                    }

                    if (childChannelIds.size() == 1)
                    {
                        rootId = childChannelIds.get(0);
                        if (real)
                            break;
                    }
                    else
                    {
                        break;
                    }
                }

                return Collections.singletonList(rootId);
            }
            else
            {
                List<Integer> keys = new ArrayList<Integer>(topChannelIds.size());
                for (Integer channelId : topChannelIds)
                {
                    boolean b = true;
                    for (Integer channelId0 : topChannelIds)
                    {
                        if (!channelId0.equals(channelId))
                        {
                            ChannelCache channel = channelTree.getChannel(channelId0);
                            if (channel != null && channel.containsChildChannel(channelId))
                            {
                                b = false;
                                break;
                            }
                        }
                    }

                    if (b)
                    {
                        keys.add(channelId);
                    }
                }

                return keys;
            }
        }
        else
        {
            ChannelCache parentChannel = channelTree.getChannel(parent);
            List<Integer> childChannelIds = new ArrayList<Integer>();
            boolean real = false;
            for (ChannelCache channel : parentChannel.getChildren())
            {
                if ((authChannelIds == null || authChannelIds.contains(channel.getChannelId())) &&
                        channel.getType() == ChannelType.information)
                {
                    childChannelIds.add(channel.getChannelId());
                    real = true;
                }
                else if (channel.containsChildChannel(this))
                {
                    childChannelIds.add(channel.getChannelId());
                }
            }

            if (childChannelIds.size() == 1 && !real)
            {
                childChannelIds = getChildKeys(childChannelIds.get(0));
            }

            return childChannelIds;
        }
    }

    @Override
    protected void beforeShowTree() throws Exception
    {
        super.beforeShowTree();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Override
    protected void beforeLoad() throws Exception
    {
        super.beforeLoad();

        DeptOwnedCrudUtils.beforeQuery(this);
    }

    @Select(field = {"deptIds", "topDeptIds"})
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
            deptTree.setShowBox(true);
        }

        return deptTree;
    }

    @Select(field = {"topChannelIds"})
    public AuthChannelTreeModel getChannelTreeModel() throws Exception
    {
        if (channelTreeModel == null)
        {
            channelTreeModel = Tools.getBean(AuthChannelTreeModel.class);
            channelTreeModel.setHasCheckBox(true);
            channelTreeModel.setEditType(EditType.edit);

            if (rootId != null && rootId > 0)
                channelTreeModel.setRootId(rootId);
        }

        return channelTreeModel;
    }

    @Override
    protected void initStats() throws Exception
    {
        join(Information.class, "i", "i.channelId=channel.channelId and i.publishTime>?time_start " +
                "and i.publishTime<=?time_end and i.deptId in ?queryDeptIds");

        addColumn("channelName", "channel.channelName");
        addStat("publishCount", "count(i.informationId)");
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        PageTreeTableView view = new PageTreeTableView();

        view.addComponent("开始时间", "time_start");
        view.addComponent("结束时间", "time_end");
        view.addComponent("部门", "topDeptIds");
        view.addComponent("栏目", "topChannelIds");

        view.addColumn("栏目名称", "channelName").setWidth("400");
        view.addColumn("发布数量", "publishCount").setWidth("200");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));

        return view;
    }

    public boolean accept(ChannelCache channelCache) throws Exception
    {
        return authChannelIds == null || authChannelIds.contains(channelCache.getChannelId());
    }
}