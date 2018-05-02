package com.gzzm.platform.commons;

import com.gzzm.platform.organ.*;
import com.gzzm.platform.wordnumber.WordNumber;

import java.util.HashMap;

/**
 * @author camel
 * @date 11-9-20
 */
public class BusinessContext extends HashMap<String, Object>
{
    private static final long serialVersionUID = -8783203347253861649L;

    /**
     * 当前用户信息
     */
    private UserInfo user;

    /**
     * 业务部门信息
     */
    private Integer businessDeptId;

    private DeptInfo businessDept;

    private String businessDeptName;

    public BusinessContext()
    {
    }

    public UserInfo getUser()
    {
        return user;
    }

    public void setUser(UserInfo user)
    {
        if (this.user != null)
            throw new SystemRuntimeException("cannot set userInfo twice");

        this.user = user;
    }

    public Integer getBusinessDeptId()
    {
        return businessDeptId;
    }

    public void setBusinessDeptId(Integer businessDeptId)
    {
        if (this.businessDeptId != null)
            throw new SystemRuntimeException("cannot set businessDeptId twice");

        this.businessDeptId = businessDeptId;
    }

    public void setBusinessDeptName(String businessDeptName)
    {
        this.businessDeptName = businessDeptName;
    }

    public SimpleDeptInfo getBusinessDept()
    {
        if (businessDept == null && businessDeptId != null)
            businessDept = UserInfo.getDeptInfo(businessDeptId);

        return businessDept;
    }

    public String getBusinessDeptName()
    {
        if (businessDeptName != null)
            return businessDeptName;

        return getBusinessDept().getDeptName();
    }

    public Integer getUserId()
    {
        return user.getUserId();
    }

    public String getUserName()
    {
        return user.getUserName();
    }

    public SimpleDeptInfo getDept()
    {
        return user.getDept();
    }

    public Integer getDeptId()
    {
        Integer deptId = user.getDeptId();
        if (deptId == null || deptId < 0)
            deptId = businessDeptId;

        return deptId;
    }

    public String getDeptName()
    {
        Integer deptId = user.getDeptId();
        if (deptId == null || deptId < 0)
            return getBusinessDeptName();

        return user.getDeptName();
    }

    public SimpleDeptInfo getBureau()
    {
        return user.getBureau();
    }

    public Integer getBureauId()
    {
        return user.getBureauId();
    }

    public String getBureauName()
    {
        SimpleDeptInfo bureau = getBureau();

        return bureau == null ? null : bureau.getDeptName();
    }

    public String getPhone()
    {
        return user.getPhone();
    }

    public int getUserType()
    {
        return user.getUserType().ordinal();
    }

    /**
     * 获得字号
     *
     * @param s 定义字号的字符串
     * @return 根据当前上下文得到字号的结果
     * @see com.gzzm.platform.wordnumber.WordNumber
     */
    public String getWordNumber(String s)
    {
        try
        {
            WordNumber wordNumber = new WordNumber(s);
            wordNumber.setDeptId(getBusinessDeptId());
            wordNumber.setType("form");

            return wordNumber.getResult();
        }
        catch (Throwable ex)
        {
            Tools.wrapException(ex);

            return null;
        }
    }

    @Override
    public Object get(Object key)
    {
        if ("user".equals(key))
        {
            return user;
        }
        else if ("userId".equals(key))
        {
            return getUserId();
        }
        else if ("userName".equals(key))
        {
            return getUserName();
        }
        else if ("dept".equals(key))
        {
            return getDept();
        }
        else if ("deptId".equals(key))
        {
            return getDeptId();
        }
        else if ("deptName".equals(key))
        {
            return getDeptName();
        }
        else if ("bureau".equals(key))
        {
            return getBureau();
        }
        else if ("bureauId".equals(key))
        {
            return getBureauId();
        }
        else if ("bureauName".equals(key))
        {
            return getBureauName();
        }
        else if ("businessDept".equals(key))
        {
            return getBusinessDept();
        }
        else if ("businessDeptId".equals(key))
        {
            return getBusinessDeptId();
        }
        else if ("businessDeptName".equals(key))
        {
            return getBusinessDeptName();
        }
        else if ("phone".equals(key))
        {
            return getPhone();
        }
        else if ("userType".equals(key))
        {
            return getUserType();
        }
        else if ("businessContext".equals(key))
        {
            return this;
        }
        else if (((String) key).startsWith("wordnumber:"))
        {
            return getWordNumber(((String) key).substring(11));
        }

        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key)
    {
        if ("user".equals(key))
        {
            return true;
        }
        else if ("userId".equals(key))
        {
            return true;
        }
        else if ("userName".equals(key))
        {
            return true;
        }
        else if ("dept".equals(key))
        {
            return true;
        }
        else if ("deptId".equals(key))
        {
            return true;
        }
        else if ("deptName".equals(key))
        {
            return true;
        }
        else if ("bureau".equals(key))
        {
            return true;
        }
        else if ("bureauId".equals(key))
        {
            return true;
        }
        else if ("bureauName".equals(key))
        {
            return true;
        }
        else if ("businessDept".equals(key))
        {
            return true;
        }
        else if ("businessDeptId".equals(key))
        {
            return true;
        }
        else if ("businessDeptName".equals(key))
        {
            return true;
        }
        else if ("phone".equals(key))
        {
            return true;
        }
        else if ("businessContext".equals(key))
        {
            return true;
        }
        else if (((String) key).startsWith("wordnumber:"))
        {
            return true;
        }

        return super.containsKey(key);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }
}
