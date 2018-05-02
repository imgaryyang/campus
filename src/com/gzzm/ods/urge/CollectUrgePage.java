package com.gzzm.ods.urge;

import com.gzzm.ods.exchange.*;
import com.gzzm.ods.flow.OdFlowInstance;
import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 会签督办操作
 *
 * @author camel
 * @date 11-11-17
 */
@Service
public class CollectUrgePage
{
    @Inject
    private CollectUrgeDao dao;

    @UserId
    private Integer userId;

    private Long receiveId;

    private CollectUrge urge;

    public CollectUrgePage()
    {
    }

    public Long getReceiveId()
    {
        return receiveId;
    }

    public void setReceiveId(Long receiveId)
    {
        this.receiveId = receiveId;
    }

    public CollectUrge getUrge()
    {
        return urge;
    }

    public void setUrge(CollectUrge urge)
    {
        this.urge = urge;
    }

    @Service(url = "/od/collecturge/{receiveId}/edit")
    public String edit() throws Exception
    {
        urge = dao.getUrge(receiveId);

        if (urge == null)
        {
            urge = new CollectUrge();
            urge.setReceiveId(receiveId);
            urge.setUserId(userId);
            urge.setCreateTime(new Date());
        }

        return "edit";
    }

    @Service(url = "/od/collecturge/{receiveId}/show")
    public String show() throws Exception
    {
        urge = dao.getUrge(receiveId);

        return "show";
    }

    @Service(method = HttpMethod.post, validateType = ValidateType.server)
    @Transactional
    public void save() throws Exception
    {
        dao.save(urge);

        Collect collect = new Collect();
        collect.setUrged(true);
        collect.setReceiveId(receiveId);
        dao.update(collect);

        Date deadline = urge.getLimitTime();
        ReceiveBase receiveBase = new ReceiveBase();
        receiveBase.setDeadline(deadline);
        receiveBase.setReceiveId(receiveId);
        dao.update(receiveBase);

        OdFlowInstance odFlowInstance = dao.getOdFlowInstanceByReceiveId(receiveId);
        if (odFlowInstance != null)
        {
            odFlowInstance.setDeadline(deadline);
            dao.update(odFlowInstance);
        }

        CollectUrgeJob.updateJob(urge);
    }
}
