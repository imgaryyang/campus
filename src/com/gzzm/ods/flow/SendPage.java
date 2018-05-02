package com.gzzm.ods.flow;

import com.gzzm.ods.exchange.Send;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * 和发文相关的一些操作的访问入口
 *
 * @author camel
 * @date 11-10-9
 */
@Service
public class SendPage
{
    @Inject
    private OdFlowDao dao;

    /**
     * 发文ID
     */
    private Long sendId;

    public SendPage()
    {
    }

    public Long getSendId()
    {
        return sendId;
    }

    public void setSendId(Long sendId)
    {
        this.sendId = sendId;
    }

    @Redirect
    @Service(url = "/ods/flow/send/{sendId}/open")
    public String open() throws Exception
    {
        Send send = dao.getSend(sendId);

        if (send != null)
        {
            SendFlowInstance sendFlowInstance = dao.getSendFlowInstanceByDocumentId(send.getDocumentId());
            if (sendFlowInstance != null)
            {
                OdFlowInstance odFlowInstance = dao.getOdFlowInstance(sendFlowInstance.getInstanceId());

                if (odFlowInstance != null)
                {
                    String url = OdFlowService.getLastStepUrl(odFlowInstance);
                    if (url.indexOf('?') < 0)
                        url += "?readOnly=true&saveOnly=true";
                    return url;
                }
            }
        }

        return null;
    }
}
