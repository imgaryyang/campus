package com.gzzm.ods.flow;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * 接收收文
 *
 * @author camel
 * @date 11-10-3
 */
@Service
public class ReceivePage
{
    @Inject
    private OdFlowDao dao;

    @Inject
    private ReceiptDao receiptDao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 业务ID
     */
    private Integer businessId;

    /**
     * 收文ID
     */
    private Long receiveId;

    public ReceivePage()
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

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    @Redirect
    @Service(url = "/ods/flow/receive/{receiveId}/receive")
    public String receive() throws Exception
    {
        ReceiveFlowStartContext startContext = ReceiveFlowStartContext.create();

        startContext.setReceiveId(receiveId);

        BusinessContext businessContext = new BusinessContext();
        businessContext.setUser(userOnlineInfo);
        businessContext.put("ip", userOnlineInfo.getIp());

        startContext.setBusinessId(businessId);
        startContext.setBusinessContext(businessContext);

        startContext.start();

        return OdFlowService.getStepUrl(startContext.getStepId(), startContext.getFlowTag());
    }

    @Redirect
    @Service(url = "/ods/flow/receive/{receiveId}/open")
    public String open() throws Exception
    {
        return getFlowUrl();
    }

    @ObjectResult
    @Service(url = "/ods/flow/receive/{receiveId}/open")
    public String getFlowUrl() throws Exception
    {
        OdFlowInstance odFlowInstance = dao.getOdFlowInstanceByReceiveId(receiveId);

        if (odFlowInstance != null)
            return OdFlowService.getFirstStepUrl(odFlowInstance);

        return null;
    }

    @Redirect
    @Service(url = "/ods/flow/receive/{receiveId}/track")
    public String track() throws Exception
    {
        OdFlowInstance odFlowInstance = dao.getOdFlowInstanceByReceiveId(receiveId);

        if (odFlowInstance != null)
        {
            String url = "/od/flow/track/instance/" + odFlowInstance.getInstanceId();
            Integer year = odFlowInstance.getYear();
            if (year != null)
                url += "?year=" + year;

            return url;
        }

        return null;
    }

    @Redirect
    @Service(url = "/ods/receive/{receiveId}/receipt")
    public String showReceipt() throws Exception
    {
        ReceiveBase receiveBase = dao.getReceiveBase(receiveId);

        if (receiveBase != null)
        {
            ReceiveState state = receiveBase.getState();
            if (state == ReceiveState.accepted || state == ReceiveState.flowing || state == ReceiveState.end)
            {
                OdFlowInstance odFlowInstance = dao.getOdFlowInstanceByReceiveId(receiveId);
                if (odFlowInstance != null)
                {
                    Long stepId = FlowApi.getFirstStepId(odFlowInstance.getInstanceId(), OdSystemFlowDao.getInstance());
                    return "/ods/flow/receive/" + stepId + "/receipt";
                }
            }

            Receipt receipt = receiptDao.getReceiptByDocumentId(receiveBase.getDocumentId());
            if (receipt != null)
            {
                ReceiptComponent component = ReceiptComponents.getComponent(receipt.getType());

                if (component != null)
                    return component.getFillUrl(receipt, receiveBase.getDeptId(), true);
            }
        }

        return null;
    }
}
