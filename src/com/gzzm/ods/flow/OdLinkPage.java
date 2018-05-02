package com.gzzm.ods.flow;

import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 公文关联相关的操作入口
 *
 * @author camel
 * @date 13-12-1
 */
@Service
public class OdLinkPage
{
    @Inject
    private OdLinkDao dao;

    @UserId
    private Integer userId;

    public OdLinkPage()
    {
    }

    @Service
    public List<OdLinkItem> getLinks(Long instanceId) throws Exception
    {
        List<OdFlowInstanceLink> links = dao.getLinks(instanceId);

        List<OdLinkItem> items = new ArrayList<OdLinkItem>(links.size());

        for (OdFlowInstanceLink link : links)
        {
            items.add(new OdLinkItem(link));
        }

        return items;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public boolean link(Long instanceId, List<Long> linkInstanceIds) throws Exception
    {
        Date now = new Date();
        boolean result = false;

        for (Long linkinstanceId : linkInstanceIds)
        {
            OdFlowInstanceLink link = dao.getLink(instanceId, linkinstanceId);

            if (link == null)
            {
                result = true;

                link = new OdFlowInstanceLink();
                link.setInstanceId(instanceId);
                link.setLinkInstanceId(linkinstanceId);
                link.setUserId(userId);
                link.setLinkTime(now);

                dao.add(link);
            }
        }

        return result;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public boolean linkWithStepIds(Long instanceId, List<Long> stepIds) throws Exception
    {
        return link(instanceId, dao.getInstanceIdsByStepIds(stepIds));
    }

    @Service
    public void deleteLink(Long instanceId, Long linkInstanceId) throws Exception
    {
        dao.delete(OdFlowInstanceLink.class, instanceId, linkInstanceId);
    }
}
