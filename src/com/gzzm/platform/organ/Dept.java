package com.gzzm.platform.organ;

import com.gzzm.platform.commons.UpdateTimeService;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Chinese;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 部门实体对象，对应数据库的部门表
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFDEPT", keys = "deptId")
@AutoAdd(false)
public class Dept implements Comparable<Dept>, DeptInfo
{
    private static final long serialVersionUID = 2928406941496574630L;

    /**
     * 部门id，长度为7,前2位为系统id，后5位为序列号
     */
    @Generatable(length = 7)
    private Integer deptId;

    /**
     * 部门名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String deptName;

    /**
     * 上级部门id
     */
    @Index
    private Integer parentDeptId;

    /**
     * 上级部门
     */
    @NotSerialized
    @ToOne("PARENTDEPTID")
    private Dept parentDept;

    /**
     * 部门级别，当-1时表示一个虚拟的部门，表示此部门无业务职能，预定的级别为0为一个科室，1为一个局，2为一个区域
     */
    @Require
    @ColumnDescription(type = "tinyint(1)", nullable = false, defaultValue = "0")
    private Byte deptLevel;

    /**
     * 状态，0为有效，1为删除
     */
    @ColumnDescription(type = "tinyint(1)", nullable = false, defaultValue = "0")
    private Byte state;

    /**
     * 部门编码，一般为部门的组织机构代码
     */
    @ColumnDescription(type = "varchar(250)")
    private String deptCode;

    /**
     * 部门简称，即简单点的部门名称，如xxx市工商行政管理局，简称为工商局
     */
    @ColumnDescription(type = "varchar(50)")
    private String shortName;

    /**
     * 部门简码，如工商局简码为GS
     */
    @ColumnDescription(type = "varchar(10)")
    private String shortCode;

    /**
     * 组织机构代码，一般组织机构代码用deptCode字段，不过对于有些系统，deptCode有其他用途，则将组织机构代码填在此处
     */
    @ColumnDescription(type = "varchar(20)")
    private String orgCode;


    /**
     * 行政区划代码
     */
    @ColumnDescription(type = "varchar(20)")
    private String divisionCode;

    /**
     * 左值，用于支持nested set结构
     *
     * @see net.cyan.crud.NestedSetTreeOrganizer
     */
    @Index
    @ColumnDescription(type = "number(8)")
    private Integer leftValue;

    /**
     * 右值，用于支持nested set结构
     *
     * @see net.cyan.crud.NestedSetTreeOrganizer
     */
    @ColumnDescription(type = "number(8)")
    private Integer rightValue;

    /**
     * 部门的说明
     */
    @ColumnDescription(type = "varchar(500)")
    private String remark;

    /**
     * 部门属性
     */
    @NotSerialized
    @ValueMap(table = "PFDEPTATTRIBUTE", keyColumn = "ATTRIBUTENAME", valueColumn = "ATTRIBUTEVALUE",
            clearForUpdate = false)
    private Map<String, String> attributes;

    /**
     * 子部门列表
     */
    @NotSerialized
    @OneToMany("PARENTDEPTID")
    private SortedSet<Dept> subDepts;

    /**
     * 将subDepts转化为list并去掉已删除的
     */
    private transient List<Dept> subDeptList;

    /**
     * 部门中的用户
     */
    @NotSerialized
    @ManyToMany(table = "PFUSERDEPT")
    @OrderBy(column = "ORDERID")
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private List<User> users;

    /**
     * 部门名称拼音
     */
    @ColumnDescription(type = "varchar(250)")
    private String spell;

    /**
     * 部门名称简拼
     */
    @ColumnDescription(type = "varchar(60)")
    private String simpleSpell;

    /**
     * 源Id，当做系统接口时，从其他系统同步部门数据到系统中，标识部门在源系统中的id
     * 如果数据不是从其他系统同步过来，则此字段为空
     */
    @ColumnDescription(type = "varchar(50)")
    @Index
    private String sourceId;

    @ColumnDescription(type = "number(9)")
    private Integer orderId;

    @ColumnDescription(type = "varchar(50)")
    private String phone;

    /**
     * 外部关联ID
     */
    @ColumnDescription(type = "varchar(50)")
    private String linkId;

    public Dept()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Integer getParentDeptId()
    {
        return parentDeptId;
    }

    public void setParentDeptId(Integer parentDeptId)
    {
        this.parentDeptId = parentDeptId;
    }

    public Dept getParentDept()
    {
        return parentDept;
    }

    public DeptInfo parentDept()
    {
        return getParentDept();
    }

    public void setParentDept(Dept parentDept)
    {
        this.parentDept = parentDept;
    }

    public Byte getDeptLevel()
    {
        return deptLevel;
    }

    public void setDeptLevel(Byte deptLevel)
    {
        this.deptLevel = deptLevel;
    }

    public int getLevel()
    {
        Byte level = getDeptLevel();
        return level == null ? -1 : level;
    }

    public Byte getState()
    {
        return state;
    }

    public void setState(Byte state)
    {
        this.state = state;
    }

    public String getDeptCode()
    {
        return deptCode;
    }

    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getShortCode()
    {
        return shortCode;
    }

    public void setShortCode(String shortCode)
    {
        this.shortCode = shortCode;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    public String getDivisionCode()
    {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode)
    {
        this.divisionCode = divisionCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getLeftValue()
    {
        return leftValue;
    }

    public void setLeftValue(Integer leftValue)
    {
        this.leftValue = leftValue;
    }

    public Integer getRightValue()
    {
        return rightValue;
    }

    public void setRightValue(Integer rightValue)
    {
        this.rightValue = rightValue;
    }

    public String getSpell()
    {
        return spell;
    }

    public void setSpell(String spell)
    {
        this.spell = spell;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public void setSimpleSpell(String simpleSpell)
    {
        this.simpleSpell = simpleSpell;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers(List<User> users)
    {
        this.users = users;
    }

    public SortedSet<Dept> getSubDepts()
    {
        return subDepts;
    }

    public String getLinkId()
    {
        return linkId;
    }

    public void setLinkId(String linkId)
    {
        this.linkId = linkId;
    }

    @SuppressWarnings("unchecked")
    public List<Dept> subDepts()
    {
        if (subDeptList == null)
        {
            for (Dept dept : getSubDepts())
            {
                if (dept.getState() == null || dept.getState() == 0)
                {
                    if (subDeptList == null)
                        subDeptList = new ArrayList<Dept>();
                    subDeptList.add(dept);
                }
            }

            if (subDeptList == null)
                subDeptList = Collections.EMPTY_LIST;
        }

        return subDeptList;
    }

    public void setSubDepts(SortedSet<Dept> subDepts)
    {
        this.subDepts = subDepts;
    }

    void setSubDeptList(List<Dept> subDeptList)
    {
        this.subDeptList = subDeptList;
    }

    /**
     * 获得所有上级部门
     *
     * @return 所有上级部门的列表
     */
    public List<Dept> allParentDepts()
    {
        List<Dept> depts = new ArrayList<Dept>();
        Dept dept = this;
        depts.add(dept);
        while ((dept = dept.getParentDept()) != null)
            depts.add(dept);
        return depts;
    }

    /**
     * 获得所有上级部门的id
     *
     * @return 所有上级部门的id列表
     */
    public List<Integer> allParentDeptIds()
    {
        List<Integer> deptIds = new ArrayList<Integer>();
        Dept dept = this;
        deptIds.add(dept.getDeptId());
        while ((dept = dept.getParentDept()) != null)
            deptIds.add(dept.getDeptId());
        return deptIds;
    }

    /**
     * 获得所有下属部门
     *
     * @return 所有下属部门的列表
     */
    public List<Dept> allSubDepts()
    {
        return getSubDepts(null);
    }

    public List<Dept> getSubDepts(Collection<Integer> authDeptIds)
    {
        List<Dept> depts = new ArrayList<Dept>();
        getAllSubDepts(depts, authDeptIds);
        return depts;
    }

    private void getAllSubDepts(List<Dept> depts, Collection<Integer> authDeptIds)
    {
        if (authDeptIds == null || authDeptIds.contains(getDeptId()))
            depts.add(this);
        for (Dept dept : getSubDepts())
        {
            if (dept.getState() == null || dept.getState() == 0)
                dept.getAllSubDepts(depts, authDeptIds);
        }
    }

    /**
     * 获得所有下属部门
     *
     * @return 所有下属部门的id列表
     */
    public List<Integer> allSubDeptIds()
    {
        return getSubDeptIds(null);
    }

    public void getAllSubDeptIds(List<Integer> deptIds)
    {
        getSubDeptIds(null, deptIds);
    }

    public List<Integer> getSubDeptIds(Collection<Integer> authDeptIds)
    {
        List<Integer> deptIds = new ArrayList<Integer>();
        getSubDeptIds(authDeptIds, deptIds);
        return deptIds;
    }

    public void getSubDeptIds(Collection<Integer> authDeptIds, List<Integer> deptIds)
    {
        if (authDeptIds == null || authDeptIds.contains(getDeptId()))
            deptIds.add(getDeptId());
        for (Dept dept : getSubDepts())
        {
            if (dept.getState() == null || dept.getState() == 0)
                dept.getSubDeptIds(authDeptIds, deptIds);
        }
    }

    public boolean isSubDeptIdsIn(Collection<Integer> deptIds)
    {
        if (deptIds.contains(deptId))
            return true;

        for (Dept dept : getSubDepts())
        {
            if (dept.isSubDeptIdsIn(deptIds))
                return true;
        }

        return false;
    }

    public boolean isParentDeptIdsIn(Collection<Integer> deptIds)
    {
        Dept dept = this;

        do
        {
            if (deptIds.contains(dept.getDeptId()))
                return true;
        } while ((dept = dept.getParentDept()) != null);

        return false;
    }

    public boolean containsLevel(int level)
    {
        if (this.deptLevel.intValue() == level)
            return true;

        for (Dept dept : getSubDepts())
        {
            if (dept.containsLevel(level))
                return true;
        }

        return false;
    }

    /**
     * 获得某一级别的上级部门
     *
     * @param level 上级部门的级别
     * @return 返回某一级别的上级部门
     */
    public Dept getParentDept(int level)
    {
        Dept dept = this;
        while (true)
        {
            if (dept.getDeptLevel() != null && dept.getDeptLevel() >= level)
                return dept;

            Dept parentDept;

            synchronized (this)
            {
                //惰性加载不线程安全
                parentDept = dept.getParentDept();
            }

            if (parentDept == null)
                return dept;

            dept = parentDept;
        }
    }

    /**
     * 获得部门从某一级别的部门以下的名称链
     *
     * @param level 部门级别
     * @return 部门名称链
     */
    public List<String> getAllNameList(int level)
    {
        Dept dept = this;
        List<String> deptNames = new ArrayList<String>();

        byte lastLevel = -1;

        while (true)
        {
            Dept parentDept;

            synchronized (this)
            {
                //惰性加载上级部门不是线程安全
                parentDept = dept.getParentDept();
            }

            //不加入根节点的名称
            if (parentDept == null && dept != this)
                break;

            Byte deptLevel = dept.getDeptLevel();

            //忽略虚拟部门
            if (dept == this || deptLevel != null && deptLevel > lastLevel)
            {
                deptNames.add(0, dept.getDeptName());
                lastLevel = deptLevel;
            }

            if (deptLevel != null && deptLevel >= level || parentDept == null)
                break;

            dept = parentDept;
        }

        return deptNames;
    }

    public String getAllName(int level)
    {
        return getAllName(getAllNameList(level));
    }

    /**
     * 获得部门从根节点以下的名称链
     *
     * @return 部门名称链
     */
    public List<String> allNameList()
    {
        Dept dept = this;
        List<String> deptNames = new ArrayList<String>();

        byte lastLevel = -1;

        while (true)
        {
            Dept parentDept;

            synchronized (this)
            {
                //惰性加载上级部门不是线程安全
                parentDept = dept.getParentDept();
            }

            //不加入根节点的名称
            if (parentDept == null && dept != this)
                break;

            Byte deptLevel = dept.getDeptLevel();

            //忽略虚拟部门
            if (dept == this || deptLevel != null && deptLevel > lastLevel)
            {
                deptNames.add(0, dept.getDeptName());
                lastLevel = deptLevel;
            }

            if (parentDept == null)
                break;

            dept = parentDept;
        }

        return deptNames;
    }

    public static String getAllName(List<String> deptNames)
    {
        StringBuilder buffer = new StringBuilder();
        int n = deptNames.size();
        for (int i = n - 1; i >= 0; i--)
        {
            boolean b = true;
            String deptName = deptNames.get(i);
            if (i < n - 1)
            {
                int l = deptName.length();

                for (int j = 0; j < l; j++)
                {
                    for (int k = l; k >= l - 2 && k >= j + 3; k--)
                    {
                        if (buffer.indexOf(deptName.substring(j, k)) >= 0)
                        {
                            buffer.insert(0, deptName.substring(0, j));
                            b = false;
                            break;
                        }
                    }

                    if (!b)
                        break;
                }
            }

            if (b)
                buffer.insert(0, deptName);
        }

        return buffer.toString();
    }

    public String allName()
    {
        return getAllName(allNameList());
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Integer valueOf()
    {
        return deptId;
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof Dept && deptId.equals(((Dept) o).deptId);
    }

    public int hashCode()
    {
        return deptId.hashCode();
    }

    public String toString()
    {
        return deptName;
    }

    public int compareTo(Dept o)
    {
        if (this == o)
            return 0;

        if (deptId != null && deptId.equals(o.deptId))
            return 0;

        int c;
        if (leftValue != null && o.leftValue != null)
        {
            c = leftValue.compareTo(o.leftValue);
            if (c != 0)
                return c;
        }

        if (deptId != null && o.deptId != null)
            return deptId.compareTo(o.deptId);

        return 0;
    }

    public static void setUpdated() throws Exception
    {
        //数据被修改之后更新菜单更新时间
        UpdateTimeService.updateLastTime("Dept", new Date());
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify() throws Exception
    {
        //设置简拼和全拼
        String deptName = getDeptName();
        if (deptName != null)
        {
            setSpell(Chinese.getLetters(deptName));
            setSimpleSpell(Chinese.getFirstLetters(deptName));
        }
    }
}
