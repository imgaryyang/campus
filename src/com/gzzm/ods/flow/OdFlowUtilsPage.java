package com.gzzm.ods.flow;

import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * 公文流程一些和流程主线无关的公用方法的调用
 *
 * @author camel
 * @date 2016/5/19
 */
@Service
public class OdFlowUtilsPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private OdFlowDao odFlowDao;

    public OdFlowUtilsPage()
    {
    }

    @Service(url = "/ods/flow/qr/stepId")
    @Json
    public Long getLastStepIdByQr(String qr) throws Exception
    {
        String s = qr.split("[\r\n]+")[0];

        int index = s.lastIndexOf('/');
        if (index > 0)
            s = s.substring(index + 1);

        Long instanceId = Long.valueOf(s);

        return FlowApi.getLastStepId(instanceId, OdSystemFlowDao.getInstance(), userOnlineInfo.getUserId());
    }
}
