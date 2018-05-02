package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.annotation.DeptId;
import com.gzzm.platform.commons.crud.BaseTreeDisplay;
import com.gzzm.platform.commons.crud.SelectableTreeView;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 校巴路线站点树形控件
 *
 * @author Neo
 * @date 2018/3/23 8:39
 */
public class BusSiteTreeModel extends BaseTreeDisplay<BusInfo, String>
{
    @Inject
    private BusDao dao;

    /**
     * 注入当前登录部门
     */
    @DeptId
    private Integer deptId;

    public BusSiteTreeModel()
    {
    }

    @Override
    public BusInfo getRoot() throws Exception
    {
        return new BusInfo("0", "全部");
    }

    @Override
    public List<BusInfo> getChildren(String parentKey) throws Exception
    {
        //根节点Id固定为0 校巴节点Id为校巴Id 站点节点Id为校巴Id-站点Id
        //获取校巴节点
        if ("0".equals(parentKey))
        {
            List<BusInfo> busInfos = dao.getBusInfos(deptId);
            for (BusInfo busInfo : busInfos)
            {
                busInfo.setBusSiteId(busInfo.getBusId().toString());
            }
            return busInfos;
        }
        //获取站点节点
        if (!parentKey.contains("-"))
        {
            //获取校巴路线的所有站点
            List<BusSite> busSites = dao.getBusSiteByBusId(Integer.valueOf(parentKey));
            List<BusInfo> busInfos = new ArrayList<>(busSites.size());
            for (BusSite busSite : busSites)
            {
                //校巴站点的拼接规则 busId-siteId
                busInfos.add(new BusInfo(parentKey + "-" + busSite.getSiteId(), busSite.getSiteName()));
            }
            return busInfos;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasChildren(BusInfo parent) throws Exception
    {
        if (parent == null)
            return false;
        List<BusInfo> busInfos = getChildren(parent.getBusSiteId());
        return busInfos != null && busInfos.size() > 0;
    }

    @Override
    public String getKey(BusInfo entity) throws Exception
    {
        return entity.getBusSiteId();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        return new SelectableTreeView();
    }
}
