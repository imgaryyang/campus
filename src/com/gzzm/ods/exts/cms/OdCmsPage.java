package com.gzzm.ods.exts.cms;

import com.gzzm.ods.document.*;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.cms.information.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2016/8/25
 */
@Service
public class OdCmsPage
{
    @Inject
    private InformationService service;

    @Inject
    private OdFlowDao odFlowDao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    public OdCmsPage()
    {
    }

    @Service(method = HttpMethod.post, url = "/od/cms/publish")
    public void publish(Long instanceId, List<Integer> channelIds) throws Exception
    {
        SendFlowInstance sendFlowInstance = odFlowDao.getSendFlowInstance(instanceId);
        OdFlowInstance odFlowInstance = odFlowDao.getOdFlowInstance(instanceId);
        OfficeDocument document = sendFlowInstance.getDocument();
        DocumentText text = document.getText();

        if (text != null)
        {
            InputFile inputFile = new InputFile(text.getTextBody(), document.getTitle() + "." +
                    (StringUtils.isEmpty(text.getType()) ? "doc" : text.getType()));

            InformationEdit information = new InformationEdit();
            information.setCreator(userOnlineInfo.getUserId());
            information.setDeptId(odFlowInstance.getDeptId());
            information.setTitle(document.getTitle());
            information.setFileCode(document.getSendNumber());
            information.setKeywords(document.getSubject());

            service.impFile(inputFile, channelIds, information);
        }
    }
}
