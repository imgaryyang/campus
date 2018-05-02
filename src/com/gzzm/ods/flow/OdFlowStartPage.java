package com.gzzm.ods.flow;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.DeptOwnedCrudUtils;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * 发起流程
 *
 * @author camel
 * @date 11-9-21
 */
@Service
public class OdFlowStartPage
{
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 业务ID
     */
    private Integer businessId;

    /**
     * 业务部门
     */
    private Integer deptId;

    private boolean test;

    public OdFlowStartPage()
    {
    }

    public Integer getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public boolean isTest()
    {
        return test;
    }

    public void setTest(boolean test)
    {
        this.test = test;
    }

    @Redirect
    @Service(url = "/ods/flow/start")
    public String start() throws Exception
    {
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        if (deptId == null)
            deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);

        OdFlowStartContext startContext = OdFlowStartContext.create();

        BusinessContext businessContext = new BusinessContext();
        businessContext.setUser(userOnlineInfo);
        businessContext.setBusinessDeptId(deptId);
        businessContext.put("ip", userOnlineInfo.getIp());

        startContext.setBusinessId(businessId);
        startContext.setTest(test);
        startContext.setBusinessContext(businessContext);

        startContext.start();

        return OdFlowService.getStepUrl(startContext.getStepId(), startContext.getFlowTag());
    }
}