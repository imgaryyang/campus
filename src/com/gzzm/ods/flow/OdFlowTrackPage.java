package com.gzzm.ods.flow;

import com.gzzm.platform.flow.*;
import net.cyan.arachne.annotation.Service;

/**
 * 公文流程跟踪
 *
 * @author camel
 * @date 11-11-17
 */
@Service(url = "/od/flow/track")
public class OdFlowTrackPage extends FlowTrackPage
{
    public OdFlowTrackPage()
    {
    }

    @Override
    protected SystemFlowDao createDao() throws Exception
    {
        return OdSystemFlowDao.getInstance(year);
    }

    @Override
    public String getPageUrl(String tag)
    {
        return OdFlowService.getPageUrl(tag);
    }
}
