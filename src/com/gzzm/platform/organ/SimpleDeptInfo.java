package com.gzzm.platform.organ;

import net.cyan.commons.util.Value;

import java.util.List;

/**
 * 只提供DeptInfo中一部分信息的访问，屏蔽线程不安全部分
 *
 * @author camel
 * @date 2009-7-29
 */
public interface SimpleDeptInfo extends Value<Integer>
{
    //虚拟的根节点
    public static final SimpleDeptInfo ROOT = new SimpleDeptInfo()
    {
        private static final long serialVersionUID = -3018501630981665012L;

        public Integer getDeptId()
        {
            return -1;
        }

        public String getDeptName()
        {
            return "";
        }

        public int getLevel()
        {
            return -1;
        }

        public Integer getParentDeptId()
        {
            return null;
        }

        public String getDeptCode()
        {
            return null;
        }

        @Override
        public String getOrgCode()
        {
            return null;
        }

        public SimpleDeptInfo parentDept()
        {
            return null;
        }

        public SimpleDeptInfo getParentDept(int level)
        {
            return null;
        }

        public List<String> getAllNameList(int level)
        {
            return null;
        }

        public String getAllName(int level)
        {
            return null;
        }

        public List<String> allNameList()
        {
            return null;
        }

        public String allName()
        {
            return null;
        }

        public Integer valueOf()
        {
            return -1;
        }

        @Override
        public String toString()
        {
            return "root";
        }
    };

    public Integer getDeptId();

    public String getDeptName();

    public int getLevel();

    public Integer getParentDeptId();

    public String getDeptCode();

    public String getOrgCode();

    public SimpleDeptInfo parentDept();

    public SimpleDeptInfo getParentDept(int level);

    public List<String> getAllNameList(int level);

    public String getAllName(int level);

    public List<String> allNameList();

    public String allName();
}