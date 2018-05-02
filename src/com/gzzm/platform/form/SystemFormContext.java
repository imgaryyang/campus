package com.gzzm.platform.form;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.FParallelText;
import net.cyan.valmiki.form.components.table.FRepeatTable;

import java.util.*;

/**
 * @author camel
 * @date 11-9-14
 */
public class SystemFormContext extends FormContext
{
    @Inject
    private static Provider<DeptService> deptServiceProvider;

    public static final String USER = "user";

    public static final String BUSINESSDEPT = "businessDept";

    public static final String DEPT = "dept";

    public static final String BUREAU = "bureau";

    /**
     * 表单数据ID
     */
    private Long bodyId;

    /**
     * 业务信息上下文
     */
    private BusinessContext businessContext;

    private String operationId;

    private Set<String> shareAuths;

    private String operatorType;

    public SystemFormContext(String formName, WebForm form, FormData formData) throws Exception
    {
        super(formName, form, formData);

        setOperatorProvider(new OperatorProvider()
        {
            public String getOperator(FormProperty property)
            {
                return SystemFormContext.this.getOperator(property);
            }

            public Set<String> getAuthority(FormProperty property, String operator)
            {
                if (USER.equals(getOperatorType(property)))
                {
                    String shareType = property.getProperty("shareType");
                    if (DEPT.equals(shareType) || BUREAU.equals(shareType))
                    {
                        User user = null;
                        try
                        {
                            user = deptServiceProvider.get().getDao().getUser(Integer.valueOf(operator));
                        }
                        catch (Exception ex)
                        {
                            Tools.wrapException(ex);
                        }

                        boolean b = true;
                        if (DEPT.equals(shareType))
                        {
                            //只要和操作者在同一个部门就可写
                            Integer deptId = getDeptId();
                            b = false;
                            if (deptId != null)
                            {
                                for (Dept dept : user.getDepts())
                                {
                                    if (dept.getDeptId().equals(deptId))
                                    {
                                        b = true;
                                        break;
                                    }
                                }
                            }
                        }
                        else if (BUREAU.equals(shareType))
                        {
                            Integer bureauId = getBureauId();
                            b = false;
                            if (bureauId != null)
                            {
                                for (Dept dept : user.getDepts())
                                {
                                    if (dept.getParentDept(1).getDeptId().equals(bureauId))
                                    {
                                        b = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (!b)
                        {
                            return Collections.singleton(FormRole.HIDDEN);
                        }
                    }
                }

                String s = property.getProperty("shareAuths");
                if (!StringUtils.isEmpty(s))
                {
                    return CollectionUtils.asSet(s.split(","));
                }

                if (shareAuths == null)
                {
                    if (property instanceof FRepeatTable && !((FRepeatTable) property).isEditSelfOnly())
                    {
                        return Collections.singleton(FormRole.WRITABLE);
                    }
                }

                return shareAuths;
            }

            public String getOperatorName(FormProperty property)
            {
                return SystemFormContext.this.getOperatorName(property);
            }

            public String getOperationId(FormProperty property)
            {
                return operationId == null ? getOperator(property) : operationId;
            }

            public String getOrgName(FormProperty property)
            {
                return SystemFormContext.this.getOrgName(property);
            }
        });
    }

    public SystemFormContext(WebForm form, FormData formData) throws Exception
    {
        this(null, form, formData);
    }

    public SystemFormContext(String formName, WebForm form) throws Exception
    {
        this(formName, form, form.createData());
    }

    public SystemFormContext(WebForm form) throws Exception
    {
        this(null, form);
    }

    public SystemFormContext(String formName, Long bodyId) throws Exception
    {
        this(formName, null, null);
        setBodyId(bodyId);
    }

    @Override
    protected void init(WebForm form, FormData formData)
    {
        super.init(form, formData);
    }

    @Override
    protected void init(WebForm form)
    {
        super.init(form);
    }

    public String getOperatorType()
    {
        return operatorType;
    }

    public void setOperatorType(String operatorType)
    {
        this.operatorType = operatorType;
    }

    public Set<String> getShareAuths()
    {
        return shareAuths;
    }

    public void setShareAuths(Set<String> shareAuths)
    {
        this.shareAuths = shareAuths;
    }

    public void setShareAuths(String... shareAuths)
    {
        this.shareAuths = CollectionUtils.asSet(shareAuths);
    }

    public String getOperatorType(FormProperty property)
    {
        String operatorType = property.getProperty("operator");

        if (operatorType == null)
            operatorType = this.operatorType;

        if (operatorType == null)
            operatorType = USER;

        return operatorType;
    }

    public String getOperator(FormProperty property)
    {
        String operatorType = getOperatorType(property);
        Object operator;

        if (USER.equals(operatorType))
        {
            operator = businessContext.getUserId();
        }
        else if (BUSINESSDEPT.equals(operatorType))
        {
            operator = businessContext.getBusinessDeptId();
        }
        else if (DEPT.equals(operatorType))
        {
            int level = DataConvert.toInt(property.getProperty("deptLevel"));

            operator = level == 0 ? businessContext.getDeptId() :
                    businessContext.getUser().getDeptId(level);
        }
        else if (BUREAU.equals(operatorType))
        {
            operator = businessContext.getBureauId();
        }
        else
        {
            operator = getUserId();
        }

        return operator.toString();
    }

    public String getOperatorName(FormProperty property)
    {
        String operatorType = getOperatorType(property);

        Object operatorName;

        if (USER.equals(operatorType))
        {
            operatorName = businessContext.getUserName();

            if (property instanceof FParallelText)
            {
                String operatorName1 = businessContext.getUser().getOperatorName();
                if (!StringUtils.isEmpty(operatorName1) && !operatorName.equals(operatorName1))
                {
                    operatorName = Tools.getMessage("platform.flow.agentSign", operatorName, operatorName1);
                }
            }
        }
        else if (BUSINESSDEPT.equals(operatorType))
        {
            operatorName = businessContext.getBusinessDeptName();
        }
        else if (DEPT.equals(operatorType))
        {
            int level = DataConvert.toInt(property.getProperty("deptLevel"));

            operatorName = level == 0 ? businessContext.getDeptName() :
                    businessContext.getUser().getDept(level).getDeptName();
        }
        else if (BUREAU.equals(operatorType))
        {
            operatorName = businessContext.getBureauName();
        }
        else
        {
            operatorName = businessContext.getUserName();

            if (property instanceof FParallelText)
            {
                String operatorName1 = businessContext.getUser().getOperatorName();
                if (!StringUtils.isEmpty(operatorName1) && !operatorName.equals(operatorName1))
                {
                    operatorName = Tools.getMessage("platform.flow.agentSign", operatorName, operatorName1);
                }
            }
        }

        return operatorName.toString();
    }

    public String getOrgName(FormProperty property)
    {
        String operatorType = getOperatorType(property);

        if (USER.equals(operatorType))
        {
            return businessContext.getDeptName();
        }
        else if (BUSINESSDEPT.equals(operatorType))
        {
            return null;
        }
        else if (DEPT.equals(operatorType))
        {
            return null;
        }
        else if (BUREAU.equals(operatorType))
        {
            return null;
        }
        else
        {
            return businessContext.getDeptName();
        }
    }

    public boolean isOperatorDept(String operatorType)
    {
        return BUSINESSDEPT.equals(operatorType) || DEPT.equals(operatorType) || BUREAU.equals(operatorType);
    }

    public boolean isOperatorDept(FormProperty property)
    {
        return isOperatorDept(getOperatorType(property));
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public BusinessContext getBusinessContext()
    {
        return businessContext;
    }

    public void setBusinessContext(BusinessContext businessContext)
    {
        this.businessContext = businessContext;

        setContext(businessContext);
    }

    public String getOperationId()
    {
        return operationId;
    }

    public void setOperationId(String operationId)
    {
        this.operationId = operationId;
    }

    public void init()
    {
        initFormData();
    }

    public void save() throws Exception
    {
        FormApi.saveFormBody(this);
    }

    public UserInfo getUser()
    {
        return businessContext.getUser();
    }

    public Integer getBusinessDeptId()
    {
        return businessContext.getBusinessDeptId();
    }

    public SimpleDeptInfo getBusinessDept()
    {
        return businessContext.getBusinessDept();
    }

    public String getBusinessDeptName()
    {
        return businessContext.getBusinessDeptName();
    }

    public Integer getUserId()
    {
        return businessContext.getUserId();
    }

    public String getUserName()
    {
        return businessContext.getUserName();
    }

    public SimpleDeptInfo getDept()
    {
        return businessContext.getDept();
    }

    public Integer getDeptId()
    {
        return businessContext.getDeptId();
    }

    public String getDeptName()
    {
        return businessContext.getDeptName();
    }

    public SimpleDeptInfo getBureau()
    {
        return businessContext.getBureau();
    }

    public Integer getBureauId()
    {
        return businessContext.getBureauId();
    }

    public String getBureauName()
    {
        return businessContext.getBureauName();
    }
}
