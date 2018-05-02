package com.gzzm.portal.cms.stat;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;
import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;

import java.sql.Date;
import java.util.*;

/**
 * 按栏目统计部门的信息采编数量
 *
 * @author sjy
 * @date 2016/7/29
 */
@Service(url = "/portal/stat/channel/dept/information")
public class InformationChannelDeptStat extends BaseTreeStat<Channel, Integer> {

    //统计该站点的栏目
    @NotCondition
    private Integer stationId;

    //key为局委部门ID，value为局委部门ID（和key一样）和该局委部门下的有信息发布的部门ID集合
    @NotSerialized
    private Map<Integer, List<Integer>> deptIdMap;

    @Lower
    private Date time_start;

    @Upper
    private Date time_end;

    public InformationChannelDeptStat() {
        addOrderBy("leftValue");
    }

    public Date getTime_start() {
        return time_start;
    }

    public void setTime_start(Date time_start) {
        this.time_start = time_start;
    }

    public Date getTime_end() {
        return time_end;
    }

    public void setTime_end(Date time_end) {
        this.time_end = time_end;
    }

    @Override
    protected String getParentField() throws Exception {
        return "parentChannelId";
    }

    @Override
    protected String getComplexCondition() throws Exception {
        String res = "channel.deleteTag=0 ";
        return res;
    }

    @Override
    protected Integer getRootKey() throws Exception {
        return null;
    }

    @Override
    protected List<Integer> getChildKeys(Integer parent) throws Exception {
        //如果parent==0，那么是点击了统计按钮来的，
        if (parent == null) return new ArrayList<Integer>();
        if (parent.equals(0) && stationId != null && stationId > 0) {
            Integer stationChannelId = getCrudService().get(Station.class, stationId).getChannelId();
            if (stationChannelId == null) {
                throw new MessageException("站点未设置主栏目");
            }
            List<Integer> keys = new ArrayList<Integer>();
            keys.add(stationChannelId);
            return keys;
        }
        return super.getChildKeys(parent);
    }


    @Override
    protected String getChildKeysCondition() throws Exception {
        //未能找到添加order by 的地方 暂放在condition里
        return "deleteTag=0 order by leftValue";
    }

    @Override
    protected void beforeLoad() throws Exception {
        initDeptIdMap();
        super.beforeLoad();
    }

    private void initDeptIdMap() throws Exception {
        CrudDao dao = getCrudService().getDao();
        Integer channelId = null;
        if (stationId != null) {
            Station station = dao.get(Station.class, stationId);
            channelId = station.getChannelId();
        }
        String oql = "select distinct t.dept from com.gzzm.portal.cms.information.Information t,com.gzzm.portal.cms.channel.Channel c where " +
                " t.channel.deleteTag=0 ";
        if (channelId != null) {
            oql += " and c.channelId=" + channelId+" and t.channel.leftValue >=c.leftValue and t.channel.leftValue<c.rightValue ";
        }
        oql += " order by t.dept.leftValue";
        List<Dept> depts = dao.oqlQuery(oql, channelId);
        //保证局委部门顺序
        deptIdMap = new ListMap<Integer, List<Integer>>();
        for (Dept d : depts) {
            //获得局委部门
            Dept parentDept = d.getParentDept(1);
            if (deptIdMap.containsKey(parentDept.getDeptId())) {
                deptIdMap.get(parentDept.getDeptId()).add(d.getDeptId());
            } else {
                List<Integer> deptIds = new ArrayList<Integer>();
                //添加局委部门
                deptIds.add(parentDept.getDeptId());
                deptIds.add(d.getDeptId());
                deptIdMap.put(parentDept.getDeptId(), deptIds);
            }
        }
    }

    @Override
    public boolean hasChildren(Map<String, Object> parent) throws Exception {
        return super.hasChildren(parent);
    }

    @Override
    public Map<String, Object> getRoot() throws Exception {
        if (deptIdMap == null || deptIdMap.size() == 0) return null;
        Map<String, Object> root = new HashMap<String, Object>();
        root.put(KEY, 0);
        root.put("channelName", "");
        root.put("publishCount", 0);
        for (Integer deptId : deptIdMap.keySet()) {
            root.put("deptId" + deptId, 0);
        }
        root.put(LEAF, false);
        return root;
    }


    @Override
    protected void initStats() throws Exception {
        join(Information.class, "i", "i.channelId=channel.channelId and i.publishTime>?time_start " +
                "and i.publishTime<=?time_end");
        addColumn("channelName", "channel.channelName");
        if (deptIdMap != null && deptIdMap.size() > 0) {
            for (Integer deptId : deptIdMap.keySet()) {
                addStat("deptId" + deptId, "sum(case when (i.deptId in(" + initSqlDeptIds(deptIdMap.get(deptId)) + ")) then 1 else 0 end)");
            }
        }
    }

    @Override
    protected Object createTreeView() throws Exception {
        CrudDao dao = getCrudService().getDao();
        if (deptIdMap == null) {
            initDeptIdMap();
        }
        PageTreeTableView view = new PageTreeTableView();
//        view.addComponent("所属网站", "stationId");
        view.addComponent("开始时间", "time_start");
        view.addComponent("结束时间", "time_end");
        view.addColumn("栏目名称", "channelName").setWidth("200").setAutoExpand(true);
        if (deptIdMap != null && deptIdMap.size() > 0)
            for (Integer deptId : deptIdMap.keySet()) {
                view.addColumn(dao.get(Dept.class, deptId).getDeptName(), "deptId" + deptId).setWidth("107");
            }
        view.defaultInit();
        view.addButton(Buttons.export("xls"));
        return view;
    }

    private static String initSqlDeptIds(List<Integer> deptIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deptIds.size(); i++) {
            sb.append(deptIds.get(i));
            if (i != deptIds.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

}