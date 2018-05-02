package com.gzzm.safecampus.campus.device;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.safecampus.campus.bus.BusRoute;
import com.gzzm.safecampus.campus.bus.BusSite;
import net.cyan.arachne.components.CheckBoxTreeModel;
import net.cyan.arachne.components.LazyPageTreeModel;
import net.cyan.arachne.components.SearchablePageTreeModel;
import net.cyan.arachne.components.SelectableModel;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RouteSiteModel implements LazyPageTreeModel<RouteSiteModel.Node>, SelectableModel<RouteSiteModel.Node>,
        SearchablePageTreeModel<RouteSiteModel.Node>, CheckBoxTreeModel<RouteSiteModel.Node> {

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    private Integer deptId;

    @Inject
    private DeviceDao dao;

    private static final RouteSiteModel.Node ROOT = new RouteSiteModel.Node("0", "根节点");

    private List<BusRoute> busrouteList;

    private Boolean checkBox;

    public RouteSiteModel(){}

    public Boolean getCheckBox() {
        if (checkBox == null) checkBox = false;
        return checkBox;
    }

    public void setCheckBox(Boolean checkBox) {
        this.checkBox = checkBox;
    }

    public RouteSiteModel(boolean cx){this.checkBox = cx;}

    public List<BusRoute> getBusrouteList() {
        if(busrouteList==null){
            busrouteList = dao.getRouteBydeptIds(deptId);
        }
        return busrouteList;
    }

    public void setBusrouteList(List<BusRoute> busrouteList) {
        this.busrouteList = busrouteList;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    @Override
    public boolean hasCheckBox(Node node) throws Exception {
        return false;
    }

    @Override
    public Boolean isChecked(Node node) throws Exception {
        return getCheckBox();
    }

    @Override
    public boolean isLazyLoad(Node node) throws Exception {
        return true;
    }

    @Override
    public void beforeLazyLoad(String s) throws Exception {

    }

    @Override
    public boolean isSearchable() throws Exception {
        return true;
    }

    @Override
    public Collection<Node> search(String s) throws Exception {
        List<BusSite> busSiteList = dao.getBusRoutesByName("%" + s + "%");
        if (CollectionUtils.isNotEmpty(busSiteList)) {
            List<RouteSiteModel.Node> nodes = new ArrayList<RouteSiteModel.Node>();
            for (BusSite busSite : busSiteList) {
                nodes.add(new RouteSiteModel.Node(busSite));
            }
            return nodes;
        }
        return null;
    }

    @Override
    public Node getParent(Node node) throws Exception {
        String id = node != null ? node.getId() : "";

        if ("0".equals(node.getId())) return null;
        if (!id.startsWith("g")) {
            BusRoute gra = dao.getBusRouteBySiteId(Integer.valueOf(id));
            if (gra != null && gra.getRouteId() != null) {
                return new RouteSiteModel.Node(gra);
            }
        }

        return ROOT;
    }

    @Override
    public Node getRoot() throws Exception {
        return ROOT;
    }

    @Override
    public boolean isLeaf(Node node) throws Exception {
        String id = node != null ? node.getId() : "";
        //根节点
        return !"0".equals(id) && !id.startsWith("s");
    }

    @Override
    public int getChildCount(Node node) throws Exception {
        String id = node.getId();
        if ("0".equals(id)) {
            return getBusrouteList() == null ? 0 : getBusrouteList().size();
        }
        /*else if (id.startsWith("g")) {
            return getClassesList() == null ? 0 : getClassesList().size();
        }*/
        String graId = id.substring(1);
        List<BusSite> busSites = dao.getBusSiteByGra(Integer.valueOf(graId));
        if (busSites == null) return 0;
        return busSites.size();
    }

    @Override
    public Node getChild(Node node, int i) throws Exception {
        if ("0".equals(node.getId())) {
            if (getBusrouteList() != null && getBusrouteList().size() > 0)
                return new RouteSiteModel.Node(getBusrouteList().get(i));
            else
                return null;
        }
        List<BusSite> busSites = dao.getBusSiteByGra(Integer.valueOf(node.getId().substring(1)));
        if (busSites != null && i < busSites.size()) {
            BusSite busSite = busSites.get(i);
            if (busSite != null) {
                return new RouteSiteModel.Node(busSite);
            }
        }
        return null;
    }

    @Override
    public String getId(Node node) throws Exception {
        return node != null ? node.getId() : "";
    }

    @Override
    public String toString(Node node) throws Exception {
        return node != null ? node.getName() : "";
    }

    @Override
    public Node getNode(String s) throws Exception {
        if ("0".equals(s)) return ROOT;
        if (s.startsWith("s")) {
            BusRoute route = dao.get(BusRoute.class, Integer.valueOf(s.substring(1)));
            return new RouteSiteModel.Node(route);
        } else {
            BusSite busSite = dao.get(BusSite.class, Integer.valueOf(s));
            if (busSite != null) return new RouteSiteModel.Node(busSite);
        }
        return null;
    }

    @Override
    public Boolean isRootVisible() {
        return false;
    }

    @Override
    public boolean isSelectable(Node node) throws Exception {
        return isLeaf(node);
    }

    public static class Node {
        private String id;

        private String name;

        private BusRoute busRoute;

        private BusSite busSite;


        public Node(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Node(BusSite cls) {
            this.id = cls.getSiteId().toString();
            this.name = cls.getSiteName();
            this.busSite = cls;
        }

        public Node(BusRoute gra) {
            this.id = "s" + gra.getRouteId();
            this.name = gra.getRouteName();
            this.busRoute = gra;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
