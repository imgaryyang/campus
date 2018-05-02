package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.components.CheckBoxTreeModel;
import net.cyan.arachne.components.LazyPageTreeModel;
import net.cyan.arachne.components.SearchablePageTreeModel;
import net.cyan.arachne.components.SelectableModel;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 课程分类树
 *
 * @author yiuman
 * @date 2018/3/14
 */
public class SubjectItemModel implements LazyPageTreeModel<SubjectItemModel.Node>, SelectableModel<SubjectItemModel.Node>,
        SearchablePageTreeModel<SubjectItemModel.Node>, CheckBoxTreeModel<SubjectItemModel.Node> {

    @Inject
    private Provider<TutorDao> daoProvider;

    /**
     * 根节点
     */
    private static final Node ROOT = new Node("0", "根节点");

    private List<TutorSubjectType> typeList;

    public List<TutorSubjectType> getTypeList() throws Exception {
        if (typeList == null) {
            typeList = daoProvider.get().getAllEntities(TutorSubjectType.class);
        }
        return typeList;
    }

    public void setTypeList(List<TutorSubjectType> typeList) {
        this.typeList = typeList;
    }

    @Override
    public boolean hasCheckBox(Node node) throws Exception {
        return isLeaf(node);
    }

    @Override
    public Boolean isChecked(Node node) throws Exception {
        return false;
    }

    @Override
    public boolean isLazyLoad(Node node) throws Exception {
        return false;
    }

    @Override
    public void beforeLazyLoad(String s) throws Exception {

    }

    @Override
    public boolean isSearchable() throws Exception {
        return false;
    }

    @Override
    public Collection<Node> search(String s) throws Exception {
        List<TutorSubjectTypeItem> items = daoProvider.get().getItemsOrdName("%" + s + "%");
        if (items != null && items.size() > 0) {
            List<Node> nodes = new ArrayList<Node>();
            for (TutorSubjectTypeItem item : items) {
                nodes.add(new Node(item));
            }
            return nodes;
        }
        return null;
    }

    @Override
    public Node getParent(Node node) throws Exception {
        String id = node != null ? node.getId() : "";

        if ("0".equals(node.getId())) return null;
        if (!id.startsWith("d")) {
            TutorSubjectTypeItem item = daoProvider.get().get(TutorSubjectTypeItem.class, Integer.valueOf(id));
            if (item != null && item.getTypeId() != null) {
                return new Node(item.getSubjectType());
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
        return !"0".equals(id) && !id.startsWith("d");
    }

    @Override
    public int getChildCount(Node node) throws Exception {
        String id = node.getId();
        if ("0".equals(id)) {
            return getTypeList() == null ? 0 : getTypeList().size();
        }

        String dictcode = id.substring(1);
        List<TutorSubjectTypeItem> depts = daoProvider.get().getItemsBytypeId(Integer.valueOf(dictcode));
        if (depts == null) return 0;
        return depts.size();
    }

    @Override
    public Node getChild(Node node, int i) throws Exception {
        if ("0".equals(node.getId())) {
            if (getTypeList() != null && getTypeList().size() > 0)
                return new Node(getTypeList().get(i));
            else
                return null;
        }
        List<TutorSubjectTypeItem> items = daoProvider.get().getItemsBytypeId(Integer.valueOf(node.getId().substring(1)));
        if (items != null && i < items.size()) {
            TutorSubjectTypeItem item = items.get(i);
            if (item != null) {
                return new Node(item);
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

        if (s.startsWith("d")) {
            TutorSubjectType type = daoProvider.get().get(TutorSubjectType.class, Integer.valueOf(s.substring(1)));
            return new Node(type);
        } else {
            TutorSubjectTypeItem item = daoProvider.get().get(TutorSubjectTypeItem.class, Integer.valueOf(s));
            if (item != null) return new Node(item);
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

        private TutorSubjectTypeItem typeItem;

        private TutorSubjectType type;

        public Node(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Node(TutorSubjectTypeItem typeItem) {
            this.id = typeItem.getTypeItemId().toString();
            this.name = typeItem.getTypeItemName();
            this.typeItem = typeItem;
        }

        public Node(TutorSubjectType type) {
            this.id = "d" + type.getTypeId();
            this.name = type.getTypeName();
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
