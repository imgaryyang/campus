package com.gzzm.platform.group;

import com.gzzm.platform.annotation.MenuId;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.components.AbstractPageComponent;
import net.cyan.commons.util.*;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.nest.annotation.Inject;

import java.io.Serializable;
import java.util.*;

/**
 * 一个同时可以选择部门，用户，部门组，用户组的控件
 *
 * @author camel
 * @date 11-9-21
 */
@Service
public class PageMemberSelector extends AbstractPageComponent<Object>
{
    @Inject
    private GroupDao dao;

    @Inject
    private DeptService deptService;

    @MenuId
    private String menuId;

    public static final String ROOT = "root";

    public static final String DEPT = "dept";

    public static final String USER = "user";

    public static final String USER_ALL = "user_all";

    public static final String USER_SELF = "user_self";

    public static final String DEPTGROUP = "deptgroup";

    public static final String USERGROUP = "usergroup";

    public static final String STATION = "station";

    public static final String DEPT_PREFIX = DEPT + ":";

    public static final String USER_PREFIX = USER + ":";

    public static final String USER_ALL_PREFIX = USER_ALL + ":";

    public static final String USER_SELF_PREFIX = USER_SELF + ":";

    public static final String DEPTGROUP_PREFIX = DEPTGROUP + ":";

    public static final String USERGROUP_PREFIX = USERGROUP + ":";

    public static final String STATION_PREFIX = STATION + ":";

    /**
     * 树节点
     */
    public static class MemberTreeNode implements Serializable
    {
        private static final long serialVersionUID = -6874704027831375043L;

        /**
         * 节点id，root为根节点，dept为部门根，user为用户根节点，deptgroup为部门组根节点，usergroup为用户组根节点
         * dept:xxx为部门树中的部门 user:xxx为选择用户中的部门树的节点，deptgroup:xxx为部门组,usergroup:xxx为用户组
         * 上面的xxx为id
         *
         * @see #ROOT
         * @see #DEPT
         * @see #USER
         * @see #DEPTGROUP
         * @see #USERGROUP
         */
        private String id;

        private String text;

        private boolean leaf;

        private boolean real = true;

        private List<MemberTreeNode> children;

        public MemberTreeNode()
        {
        }

        public MemberTreeNode(String id, String text)
        {
            this.id = id;
            this.text = text;
        }

        public MemberTreeNode(String id, String text, boolean leaf)
        {
            this.id = id;
            this.text = text;
            this.leaf = leaf;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getText()
        {
            return text;
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public boolean isLeaf()
        {
            return leaf;
        }

        public void setLeaf(boolean leaf)
        {
            this.leaf = leaf;
        }

        public List<MemberTreeNode> getChildren()
        {
            return children;
        }

        public void setChildren(List<MemberTreeNode> children)
        {
            this.children = children;
        }

        protected boolean isReal()
        {
            return real;
        }

        protected void setReal(boolean real)
        {
            this.real = real;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Collection<Integer> deptSelectAuthDeptIds;

    private Collection<Integer> userSelectAuthDeptIds;

    private Collection<Integer> deptGroupAuthDeptIds;

    private Collection<Integer> userGroupAuthDeptIds;

    private Collection<Integer> stationSelectAuthDeptIds;

    private Collection<Integer> scopeDeptIds;

    private boolean deptSelectAuthDeptIdsLoaded;

    private boolean userSelectAuthDeptIdsLoaded;

    private boolean deptGroupAuthDeptIdsLoaded;

    private boolean userGroupAuthDeptIdsLoaded;

    private boolean stationSelectAuthDeptIdsLoaded;

    private boolean scopeDeptIdsLoaded;

    /**
     * 当前操作的部门ID，用于获得有权限的部门
     */
    private Integer deptId;

    /**
     * 范围ID，用于获得部门集合
     */
    private Integer scopeId;

    /**
     * 部门数据范围
     */
    private String scopeName;

    /**
     * 关联的权限
     */
    private String app;

    /**
     * 是否只查询部门，不显示用户
     */
    private MemberType[] types;

    private boolean deptGroupDisplay = true;

    private boolean userGroupDisplay = true;

    private boolean canSelectDeptInGroup;

    public PageMemberSelector()
    {
    }

    public Integer getDeptId()
    {
        if (deptId == null)
            deptId = userOnlineInfo.getDeptId();
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public String getScopeName()
    {
        return scopeName;
    }

    public void setScopeName(String scopeName)
    {
        this.scopeName = scopeName;
    }

    public String getApp()
    {
        return app;
    }

    public void setApp(String app)
    {
        if ("".equals(app))
            app = menuId;

        this.app = app;
    }

    public MemberType[] getTypes()
    {
        return types;
    }

    public void setTypes(MemberType[] types)
    {
        if (types != null)
            this.types = types;
    }

    public void setType(String type)
    {
        String[] ss = type.split(",");

        types = new MemberType[ss.length];
        for (int i = 0; i < ss.length; i++)
            types[i] = MemberType.valueOf(ss[i]);
    }

    @NotSerialized
    public String getType()
    {
        return StringUtils.concat(types, ",");
    }

    public boolean isDeptGroupDisplay()
    {
        return deptGroupDisplay;
    }

    public void setDeptGroupDisplay(boolean deptGroupDisplay)
    {
        this.deptGroupDisplay = deptGroupDisplay;
    }

    public boolean isUserGroupDisplay()
    {
        return userGroupDisplay;
    }

    public void setUserGroupDisplay(boolean userGroupDisplay)
    {
        this.userGroupDisplay = userGroupDisplay;
    }

    public boolean isCanSelectDeptInGroup()
    {
        return canSelectDeptInGroup;
    }

    public void setCanSelectDeptInGroup(boolean canSelectDeptInGroup)
    {
        this.canSelectDeptInGroup = canSelectDeptInGroup;
    }

    private boolean containsType(MemberType type)
    {
        if (type == null)
            return true;

        if (getTypes() != null)
        {
            for (MemberType type1 : getTypes())
            {
                if (type1 == type)
                    return true;
            }
        }

        return false;
    }

    private Collection<Integer> getScopeDeptIds() throws Exception
    {
        if (!scopeDeptIdsLoaded)
        {
            Integer scopeId = this.scopeId;
            if (scopeId == null)
            {
                RoleScope scope = deptService.getRoleScopeByName(deptId, scopeName);
                if (scope != null)
                    scopeId = scope.getScopeId();
            }

            if (scopeId != null)
            {
                Scopes scopes = new Scopes(scopeId, userOnlineInfo.getDeptId(), getDeptId(), deptService);
                scopeDeptIds = scopes.getDeptIds();
            }

            scopeDeptIdsLoaded = true;
        }
        return scopeDeptIds;
    }

    private Collection<Integer> getDeptSelectAuthDeptIds() throws Exception
    {
        if (!deptSelectAuthDeptIdsLoaded)
        {
            if (scopeId != null)
            {
                deptSelectAuthDeptIds = getScopeDeptIds();
            }
            else
            {
                deptSelectAuthDeptIds = userOnlineInfo.getAuthDeptIds(app == null ? Member.DEPT_SELECT_APP : app);
            }

            deptSelectAuthDeptIdsLoaded = true;
        }

        return deptSelectAuthDeptIds;
    }

    private Collection<Integer> getUserSelectAuthDeptIds() throws Exception
    {
        if (!userSelectAuthDeptIdsLoaded)
        {
            if (scopeId != null)
            {
                userSelectAuthDeptIds = getScopeDeptIds();
            }
            else
            {
                userSelectAuthDeptIds = userOnlineInfo.getAuthDeptIds(app == null ? Member.USER_SELECT_APP : app);

                if (userSelectAuthDeptIds != null && userSelectAuthDeptIds.isEmpty())
                {
                    userSelectAuthDeptIds = deptService.getDept(deptId).allSubDeptIds();
                }
            }

            userSelectAuthDeptIdsLoaded = true;
        }

        return userSelectAuthDeptIds;
    }

    private Collection<Integer> getDeptGroupAuthDeptIds() throws Exception
    {
        if (!deptGroupAuthDeptIdsLoaded)
        {
            if (scopeId != null || !StringUtils.isEmpty(scopeName))
            {
                deptGroupAuthDeptIds = getScopeDeptIds();
            }
            else
            {
                deptGroupAuthDeptIds = userOnlineInfo.getAuthDeptIds(Member.DEPT_GROUP_SELECT_APP);

                if (deptGroupAuthDeptIds != null && deptGroupAuthDeptIds.isEmpty())
                {
                    deptGroupAuthDeptIds = Arrays.asList(getDeptId(), userOnlineInfo.getDeptId(), 1);
                }
            }

            deptGroupAuthDeptIdsLoaded = true;
        }

        return deptGroupAuthDeptIds;
    }

    private Collection<Integer> getUserGroupAuthDeptIds() throws Exception
    {
        if (!userGroupAuthDeptIdsLoaded)
        {
            if (scopeId != null || !StringUtils.isEmpty(scopeName))
            {
                userGroupAuthDeptIds = getScopeDeptIds();
            }
            else
            {
                userGroupAuthDeptIds = userOnlineInfo.getAuthDeptIds(Member.USER_GROUP_SELECT_APP);

                if (userGroupAuthDeptIds != null && userGroupAuthDeptIds.isEmpty())
                {
                    userGroupAuthDeptIds = Arrays.asList(getDeptId(), userOnlineInfo.getDeptId(), 1);
                }
            }

            userGroupAuthDeptIdsLoaded = true;
        }

        return userGroupAuthDeptIds;
    }

    private Collection<Integer> getStationSelectAuthDeptIds() throws Exception
    {
        if (!stationSelectAuthDeptIdsLoaded)
        {
            if (scopeId != null || !StringUtils.isEmpty(scopeName))
            {
                stationSelectAuthDeptIds = getScopeDeptIds();
            }
            else
            {
                stationSelectAuthDeptIds = userOnlineInfo.getAuthDeptIds(Member.STATION_SELECT_APP);

                if (stationSelectAuthDeptIds != null && stationSelectAuthDeptIds.isEmpty())
                {
                    stationSelectAuthDeptIds = deptService.getDept(deptId).allSubDeptIds();
                }
            }

            stationSelectAuthDeptIdsLoaded = true;
        }

        return stationSelectAuthDeptIds;
    }

    private MemberTreeNode getRoot() throws Exception
    {
        MemberTreeNode node = new MemberTreeNode();
        node.setId(ROOT);
        node.setText("");
        node.setChildren(loadChildren(ROOT));

        return node;
    }

    /**
     * 加载树的子节点
     *
     * @param parent 父节点的id，命名规则见MemberTreeNode.id的注释
     * @return 子节点列表
     * @throws Exception 异常
     * @see MemberTreeNode#id
     */
    @Service
    public List<MemberTreeNode> loadChildren(String parent) throws Exception
    {
        if (ROOT.equals(parent))
        {
            //根节点，显示所有类型
            List<MemberTreeNode> nodes = new ArrayList<MemberTreeNode>(4);

            if (containsType(MemberType.dept))
                nodes.add(new MemberTreeNode(DEPT, Tools.getMessage("com.gzzm.platform.organ.Dept")));

            if ((containsType(MemberType.dept) && deptGroupDisplay) || containsType(MemberType.deptgroup))
                nodes.add(new MemberTreeNode(DEPTGROUP, Tools.getMessage("com.gzzm.platform.group.DeptGroup")));

            if (containsType(MemberType.user))
                nodes.add(new MemberTreeNode(USER, Tools.getMessage("com.gzzm.platform.organ.User")));

            if ((containsType(MemberType.user) && userGroupDisplay) || containsType(MemberType.usergroup))
                nodes.add(new MemberTreeNode(USERGROUP, Tools.getMessage("com.gzzm.platform.group.UserGroup")));

            if (containsType(MemberType.station))
                nodes.add(new MemberTreeNode(STATION, Tools.getMessage("com.gzzm.platform.organ.Station")));

            return nodes;
        }
        else if (DEPT.equals(parent))
        {
            return loadDeptNodes(null, DEPT, getDeptSelectAuthDeptIds(), true);
        }
        else if (USER.equals(parent))
        {
            return loadDeptNodes(null, USER, getUserSelectAuthDeptIds(), true);
        }
        else if (DEPTGROUP.equals(parent))
        {
            List<MemberTreeNode> nodes = new ArrayList<MemberTreeNode>();
            Collection<Integer> authDeptIds = getDeptGroupAuthDeptIds();

            if (authDeptIds == null || authDeptIds.contains(userOnlineInfo.getDeptId()))
            {
                for (DeptGroup deptGroup : dao.getDeptGroupsByDept(userOnlineInfo.getDeptId(),
                        userOnlineInfo.getUserId()))
                {
                    nodes.add(new MemberTreeNode(DEPTGROUP_PREFIX + deptGroup.getGroupId(),
                            deptGroup.getGroupName(), true));
                }
            }

            DeptInfo dept = deptService.getDept(getDeptId());
            while (dept != null)
            {
                if (!dept.getDeptId().equals(userOnlineInfo.getDeptId()) &&
                        (authDeptIds == null || authDeptIds.contains(dept.getDeptId())))
                {
                    for (DeptGroup deptGroup : dao.getDeptGroupsByDept(dept.getDeptId(), userOnlineInfo.getUserId()))
                    {
                        nodes.add(new MemberTreeNode(DEPTGROUP_PREFIX + deptGroup.getGroupId(),
                                deptGroup.getGroupName(), true));
                    }
                }

                dept = dept.parentDept();
            }

            return nodes;
        }
        else if (USERGROUP.equals(parent))
        {
            List<MemberTreeNode> nodes = new ArrayList<MemberTreeNode>();

            for (UserGroup userGroup : dao.getUserGroupsByUser(userOnlineInfo.getUserId()))
            {
                nodes.add(new MemberTreeNode(USERGROUP_PREFIX + userGroup.getGroupId(),
                        userGroup.getGroupName(), true));
            }

            Collection<Integer> authDeptIds = getUserGroupAuthDeptIds();
            if (authDeptIds == null || authDeptIds.contains(userOnlineInfo.getDeptId()))
            {
                for (UserGroup userGroup : dao.getUserGroupsByDept(userOnlineInfo.getDeptId()))
                {
                    nodes.add(new MemberTreeNode(USERGROUP_PREFIX + userGroup.getGroupId(),
                            userGroup.getGroupName(), true));
                }
            }

            DeptInfo dept = deptService.getDept(getDeptId());

            while (dept != null)
            {
                if (!dept.getDeptId().equals(userOnlineInfo.getDeptId()) &&
                        (authDeptIds == null || authDeptIds.contains(dept.getDeptId())))
                {
                    for (UserGroup userGroup : dao.getUserGroupsByDept(dept.getDeptId()))
                    {
                        nodes.add(new MemberTreeNode(USERGROUP_PREFIX + userGroup.getGroupId(),
                                userGroup.getGroupName(), true));
                    }
                }

                dept = dept.parentDept();
            }

            return nodes;
        }
        else if (STATION.equals(parent))
        {
            return loadDeptNodes(null, STATION, getStationSelectAuthDeptIds(), true);
        }
        else if (parent.startsWith(DEPT_PREFIX))
        {
            Integer deptId = Integer.valueOf(parent.substring(DEPT_PREFIX.length()));
            return loadDeptNodes(deptId, DEPT, getDeptSelectAuthDeptIds(), true);
        }
        else if (parent.startsWith(USER_PREFIX))
        {
            Integer deptId = Integer.valueOf(parent.substring(USER_PREFIX.length()));
            return loadDeptNodes(deptId, USER, getUserSelectAuthDeptIds(), true);
        }
        else if (parent.startsWith(USER_ALL_PREFIX))
        {
            Integer deptId = Integer.valueOf(parent.substring(USER_ALL_PREFIX.length()));
            return loadDeptNodes(deptId, USER_ALL, getUserSelectAuthDeptIds(), true);
        }
        else if (parent.startsWith(USER_SELF_PREFIX))
        {
            Integer deptId = Integer.valueOf(parent.substring(USER_SELF_PREFIX.length()));
            return loadDeptNodes(deptId, USER_SELF, getUserSelectAuthDeptIds(), true);
        }
        else if (parent.startsWith(STATION_PREFIX))
        {
            Integer deptId = Integer.valueOf(parent.substring(STATION_PREFIX.length()));
            return loadDeptNodes(deptId, STATION, getStationSelectAuthDeptIds(), true);
        }


        return null;
    }

    private List<MemberTreeNode> loadDeptNodes(Integer parent, String type, Collection<Integer> authDeptIds,
                                               boolean root) throws Exception
    {
        DeptInfo parentDeptInfo = parent == null ? deptService.getRoot() : deptService.getDept(parent);

        List<MemberTreeNode> nodes = new ArrayList<MemberTreeNode>();

        if (parent == null && (authDeptIds == null || authDeptIds.contains(parentDeptInfo.getDeptId())))
        {
            Integer deptId = parentDeptInfo.getDeptId();
            boolean hasChildren = deptService.containsSubAuthDept(deptId, null, authDeptIds);
            nodes.add(new MemberTreeNode(type + ":" + deptId, parentDeptInfo.getDeptName(), !hasChildren));
        }
        else
        {
            for (DeptInfo deptInfo : parentDeptInfo.subDepts())
            {
                Integer deptId = deptInfo.getDeptId();

                boolean hasChildren = deptService.containsSubAuthDept(deptId, null, authDeptIds);

                if (authDeptIds == null || authDeptIds.contains(deptId))
                {
                    nodes.add(new MemberTreeNode(type + ":" + deptId, deptInfo.getDeptName(), !hasChildren));
                }
                else if (hasChildren)
                {
                    List<MemberTreeNode> children = loadDeptNodes(deptId, type, authDeptIds, false);

                    if (children.size() > 1)
                    {
                        MemberTreeNode node = new MemberTreeNode(type + ":" + deptId, deptInfo.getDeptName());

                        node.setReal(false);
                        node.setChildren(children);

                        nodes.add(node);
                    }
                    else
                    {
                        nodes.addAll(children);
                    }
                }
            }


            if (root)
            {
                while (nodes.size() == 1 && !nodes.get(0).isReal())
                {
                    nodes = nodes.get(0).getChildren();
                }
            }
        }

        if (USER.equals(type) && parent == null)
        {
            Integer bureauId = userOnlineInfo.getBureauId();
            if (bureauId != 1 && (authDeptIds == null || authDeptIds.contains(bureauId)))
            {
                boolean exists = false;
                for (MemberTreeNode node : nodes)
                {
                    if (node.getId().endsWith(":" + bureauId))
                    {
                        exists = true;
                        break;
                    }
                }

                SimpleDeptInfo bureau = userOnlineInfo.getBureau();
                String bureauName = bureau.getDeptName();
                if (exists)
                {
                    if (((DeptInfo) bureau).getSubDeptIds(authDeptIds).size() > 1)
                    {
                        nodes.add(new MemberTreeNode(USER_ALL_PREFIX + bureauId, bureauName + "所有人员", true));
                    }
                }
                else
                {
                    boolean containsSub = deptService.containsSubAuthDept(bureauId, null, authDeptIds);
                    nodes.add(new MemberTreeNode(USER_SELF_PREFIX + bureauId, bureauName, !containsSub));
                    if (containsSub)
                        nodes.add(new MemberTreeNode(USER_ALL_PREFIX + bureauId, bureauName + "所有人员", true));
                }
            }
        }

        return nodes;
    }

    /**
     * 加载某个父节点里的成员
     *
     * @param parent 父节点，命名规则见MemberTreeNode.id的注释
     * @return 成员列表
     * @throws Exception 查询数据异常
     */
    @Service
    public List<Member> loadMembers(String parent) throws Exception
    {
        if (DEPT.equals(parent))
        {
            //部门根节点
            return loadDeptMembers(null, getDeptSelectAuthDeptIds());
        }
        else if (USER.equals(parent))
        {
            //用户根节点，返回空集
            return null;
        }
        else if (DEPTGROUP.equals(parent))
        {
            if (!containsType(MemberType.deptgroup))
                return null;

            Collection<Integer> authDeptIds = getDeptGroupAuthDeptIds();

            //返回所有部门组
            List<Member> members = new ArrayList<Member>();

            if (authDeptIds == null || authDeptIds.contains(userOnlineInfo.getDeptId()))
            {
                for (DeptGroup deptGroup : dao.getDeptGroupsByDept(userOnlineInfo.getDeptId(),
                        userOnlineInfo.getUserId()))
                {
                    members.add(new Member(deptGroup));
                }
            }

            DeptInfo dept = deptService.getDept(getDeptId());
            while (dept != null)
            {
                if (!dept.getDeptId().equals(userOnlineInfo.getDeptId()) &&
                        (authDeptIds == null || authDeptIds.contains(dept.getDeptId())))
                {
                    for (DeptGroup deptGroup : dao.getDeptGroupsByDept(dept.getDeptId(), userOnlineInfo.getUserId()))
                    {
                        members.add(new Member(deptGroup));
                    }
                }

                dept = dept.parentDept();
            }

            return members;
        }
        else if (USERGROUP.equals(parent))
        {
            if (!containsType(MemberType.usergroup))
                return null;


            Collection<Integer> authDeptIds = getUserGroupAuthDeptIds();

            //返回所有用户组
            List<Member> members = new ArrayList<Member>();

            for (UserGroup userGroup : dao.getUserGroupsByUser(userOnlineInfo.getUserId()))
            {
                members.add(new Member(userGroup));
            }

            if (authDeptIds == null || authDeptIds.contains(userOnlineInfo.getDeptId()))
            {
                for (UserGroup userGroup : dao.getUserGroupsByDept(userOnlineInfo.getDeptId()))
                {
                    members.add(new Member(userGroup));
                }
            }

            DeptInfo dept = deptService.getDept(getDeptId());

            while (dept != null)
            {
                if (!dept.getDeptId().equals(userOnlineInfo.getDeptId()) &&
                        (authDeptIds == null || authDeptIds.contains(dept.getDeptId())))
                {
                    for (UserGroup userGroup : dao.getUserGroupsByDept(dept.getDeptId()))
                    {
                        members.add(new Member(userGroup));
                    }
                }

                dept = dept.parentDept();
            }

            return members;
        }
        else if (STATION.equals(parent))
        {
            //岗位根节点，返回空集
            return null;
        }
        else if (parent.startsWith(DEPT_PREFIX))
        {
            Collection<Integer> deptSelectAuthDeptIds = getDeptSelectAuthDeptIds();

            //部门
            Integer deptId = Integer.valueOf(parent.substring(DEPT_PREFIX.length()));

            List<Member> members = null;
            if (deptId != null)
                members = loadDeptMembers(deptId, deptSelectAuthDeptIds);

            if ((members == null || members.isEmpty()) &&
                    (deptSelectAuthDeptIds == null || deptSelectAuthDeptIds.contains(deptId)))
            {
                //没有子部门，返回部门本身
                return Collections.singletonList(new Member(deptService.getDept(deptId)));
            }

            return members;
        }
        else if (parent.startsWith(USER_PREFIX) || parent.startsWith(USER_SELF_PREFIX))
        {
            //用户
            Integer deptId = Integer.valueOf(parent.substring(parent.indexOf(':') + 1));

            List<User> users = null;
            if (getUserSelectAuthDeptIds() == null || getUserSelectAuthDeptIds().contains(deptId))
                users = deptService.getDao().getUsersInDept(deptId);

            List<Member> members;

            if (users == null || users.size() == 0)
            {
                members = new ArrayList<Member>();
                loadUsers(deptService.getDept(deptId), members, deptService.getDao());
            }
            else
            {
                members = new ArrayList<Member>(users.size());

                for (User user : users)
                {
                    members.add(new Member(user, deptId));
                }
            }

            return members;
        }
        else if (parent.startsWith(USER_ALL_PREFIX))
        {
            //用户
            Integer deptId = Integer.valueOf(parent.substring(USER_ALL_PREFIX.length()));

            List<Member> members = new ArrayList<Member>();
            loadUsers(deptService.getDept(deptId), members, deptService.getDao());

            return members;
        }
        else if (parent.startsWith(DEPTGROUP_PREFIX))
        {
            if (containsType(MemberType.deptgroup) && !canSelectDeptInGroup)
            {
                Integer groupId = Integer.valueOf(parent.substring(DEPTGROUP_PREFIX.length()));
                DeptGroup deptGroup = dao.getDeptGroup(groupId);
                return Collections.singletonList(new Member(deptGroup));
            }
            else
            {
                if (!containsType(MemberType.dept))
                    return null;

                //部门组，加载组里的部门
                Integer groupId = Integer.valueOf(parent.substring(DEPTGROUP_PREFIX.length()));

                List<Member> members = new ArrayList<Member>();
                for (Dept dept : dao.getDeptGroup(groupId).getDepts())
                {
                    if (dept.getState() == 0)
                        members.add(new Member(dept));
                }

                return members;
            }
        }
        else if (parent.startsWith(USERGROUP_PREFIX))
        {
            if (!containsType(MemberType.user))
                return null;

            //用户组，加载组里的用户
            Integer groupId = Integer.valueOf(parent.substring(USERGROUP_PREFIX.length()));

            List<Member> members = new ArrayList<Member>();
            for (User user : dao.getUserGroup(groupId).getUsers())
            {
                if (user.getState() == null || user.getState() != 2)
                    members.add(new Member(user, null));
            }

            return members;
        }
        else if (parent.startsWith(STATION_PREFIX))
        {
            //用户
            Integer deptId = Integer.valueOf(parent.substring(STATION_PREFIX.length()));

            List<Station> stations = null;
            if (getStationSelectAuthDeptIds() == null || getStationSelectAuthDeptIds().contains(deptId))
                stations = deptService.getDao().getStationsInDept(deptId);

            List<Member> members = new ArrayList<Member>(stations.size());

            for (Station station : stations)
            {
                members.add(new Member(station));
            }

            return members;
        }

        return null;
    }

    private List<Member> loadDeptMembers(Integer deptId, Collection<Integer> authDeptIds) throws Exception
    {
        if (deptId == null)
            return Collections.emptyList();

        List<Member> result = new ArrayList<Member>();

        DeptInfo dept = deptService.getDept(deptId);

        if (authDeptIds == null || authDeptIds.contains(deptId))
        {
            result.add(new Member(dept));
        }
        loadDeptMembers(dept, authDeptIds, result);

        return result;
    }

    private boolean loadDeptMembers(DeptInfo dept, Collection<Integer> authDeptIds, List<Member> result)
            throws Exception
    {
        boolean b = false;
        List<? extends DeptInfo> depts = dept.subDepts();

        for (DeptInfo subDept : depts)
        {
            if (authDeptIds == null || authDeptIds.contains(subDept.getDeptId()))
            {
                b = true;
                if (subDept.getLevel() == 1)
                {
                    result.add(new Member(subDept));
                }
                else
                {
                    if (!loadDeptMembers(subDept, authDeptIds, result))
                    {
                        result.add(new Member(subDept));
                    }
                }
            }
            else
            {
                if (loadDeptMembers(subDept, authDeptIds, result))
                    b = true;
            }
        }

        return b;
    }

    /**
     * 根据一个字符串查询相关的成员，
     *
     * @param s 输入的字符串，可以是姓名的，也可以是拼音
     * @return 匹配的接收者列表
     * @throws Exception 异常
     */
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    @Service
    public List<Member> queryMember(String s) throws Exception
    {
        List<Member> members = new ArrayList<Member>();

        if (containsType(MemberType.dept))
        {
            //先查询部门
            int index = 0;
            for (DeptInfo dept : deptService.searchDept(s, getDeptSelectAuthDeptIds()))
            {
                if (index++ > 50)
                    break;
                Member member = new Member(dept);

                if (dept.getLevel() == 0)
                    member.setDeptName(dept.getParentDept(1).getDeptName());

                members.add(member);
            }
        }

        if (containsType(MemberType.user))
        {
            //查询用户
            int index = 0;
            for (User user : deptService.getDao().queryUsersByName(s, getUserSelectAuthDeptIds(), 50 - members.size()))
            {
                if (index++ > 50)
                    break;

                Member member = new Member(user, null);
                for (Dept dept : user.getDepts())
                {
                    member.setDeptName(dept.getAllName(1));
                    break;
                }

                members.add(member);
            }
        }

        boolean containsDeptGroup = containsType(MemberType.deptgroup);
        boolean containsUserGroup = containsType(MemberType.usergroup);

        if (containsDeptGroup)
        {
            Collection<Integer> authDeptIds = getDeptGroupAuthDeptIds();

            if (authDeptIds == null || authDeptIds.contains(userOnlineInfo.getDeptId()))
            {
                for (DeptGroup deptGroup : dao.getDeptGroupsByDept(userOnlineInfo.getDeptId(),
                        userOnlineInfo.getUserId()))
                {
                    if (Tools.matchText(deptGroup.getGroupName(), s))
                        members.add(new Member(deptGroup));
                }
            }

            DeptInfo dept = deptService.getDept(getDeptId());

            while (dept != null)
            {
                if (!dept.getDeptId().equals(userOnlineInfo.getDeptId()) &&
                        (authDeptIds == null || authDeptIds.contains(dept.getDeptId())))
                {
                    for (DeptGroup deptGroup : dao
                            .getDeptGroupsByDept(dept.getDeptId(), userOnlineInfo.getUserId()))
                    {
                        if (Tools.matchText(deptGroup.getGroupName(), s))
                            members.add(new Member(deptGroup));
                    }
                }

                dept = dept.parentDept();
            }
        }

        if (containsUserGroup)
        {
            for (UserGroup userGroup : dao.getUserGroupsByUser(userOnlineInfo.getUserId()))
            {
                if (Tools.matchText(userGroup.getGroupName(), s))
                    members.add(new Member(userGroup));
            }

            Collection<Integer> authDeptIds = getUserGroupAuthDeptIds();

            if (authDeptIds == null || authDeptIds.contains(userOnlineInfo.getDeptId()))
            {
                for (UserGroup userGroup : dao.getUserGroupsByDept(userOnlineInfo.getDeptId()))
                {
                    if (Tools.matchText(userGroup.getGroupName(), s))
                        members.add(new Member(userGroup));
                }
            }

            DeptInfo dept = deptService.getDept(getDeptId());

            while (dept != null)
            {
                if (!dept.getDeptId().equals(userOnlineInfo.getDeptId()) &&
                        (authDeptIds == null || authDeptIds.contains(dept.getDeptId())))
                {
                    for (UserGroup userGroup : dao.getUserGroupsByDept(dept.getDeptId()))
                    {
                        if (Tools.matchText(userGroup.getGroupName(), s))
                            members.add(new Member(userGroup));
                    }
                }

                dept = dept.parentDept();
            }
        }

        return members;
    }

    private boolean loadUsers(DeptInfo dept, List<Member> users, OrganDao dao) throws Exception
    {
        Integer deptId = dept.getDeptId();

        if (getUserSelectAuthDeptIds() == null || getUserSelectAuthDeptIds().contains(deptId))
        {
            for (User user : dao.getUsersInDept(deptId))
            {
                Integer userId = user.getUserId();
                boolean exists = false;
                for (Member user1 : users)
                {
                    if (user1.getId().equals(userId))
                    {
                        exists = true;
                        break;
                    }
                }

                if (!exists)
                {
                    users.add(new Member(user, deptId));
                    if (users.size() > 300)
                    {
                        return false;
                    }
                }
            }
        }

        for (DeptInfo subDept : dept.subDepts())
        {
            if (!loadUsers(subDept, users, dao))
                return false;
        }

        return true;
    }

    public String createHtml(String tagName, Map<String, Object> attributes) throws Exception
    {
        String id = StringUtils.toString(attributes.get("id"));
        if (id == null)
            id = "memberSelector";

        String name = StringUtils.toString(attributes.get("name"));

        ForwardContext context = RequestContext.getContext().getForwardContext();
        if (context != null)
        {
            context.importJs("/platform/group/memberselector.js");
            context.importCss("/platform/group/memberselector.css");
            context.importJs(context.getJsPath() + "/widgets/tree.js");
            context.importJs(context.getJsPath() + "/widgets/itemselector.js");
        }

        StringBuilder buffer = new StringBuilder("<").append(tagName);
        buffer.append(" id=\"").append(HtmlUtils.escapeAttribute(id)).append("\" class=\"memberselector\">\n");

        buffer.append("<script>\n");

        buffer.append(PageClass.getClassInfo(PageMemberSelector.class).getObjectScript(this, id));

        buffer.append("new System.MemberSelector(\"").append(HtmlUtils.escapeString(id)).append("\",");
        JsonSerializer serializer = new JsonSerializer(buffer, PageConfig.getIgnoreFilter());
        serializer.serialize(getRoot());
        buffer.append(",\"").append(HtmlUtils.escapeString(name)).append("\"");

        buffer.append(").create();\n");

        buffer.append("</script>\n");

        buffer.append("\n</").append(tagName).append(">");

        return buffer.toString();
    }
}