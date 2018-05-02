package com.gzzm.ods.urge;

import com.gzzm.ods.flow.*;
import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 督办操作
 *
 * @author camel
 * @date 11-11-17
 */
@Service
public class UrgePage
{
    @Inject
    private UrgeDao dao;

    @UserId
    private Integer userId;

    private Long instanceId;

    private OdFlowUrge urge;

    public UrgePage()
    {
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public OdFlowUrge getUrge()
    {
        return urge;
    }

    public void setUrge(OdFlowUrge urge)
    {
        this.urge = urge;
    }

    @Service(url = "/od/urge/{instanceId}/edit")
    public String edit() throws Exception
    {
        urge = dao.getUrge(instanceId);

        if (urge == null)
        {
            urge = new OdFlowUrge();
            urge.setInstanceId(instanceId);
            urge.setUserId(userId);
            urge.setCreateTime(new Date());
        }

        return "edit";
    }

    @Service(url = "/od/urge/{instanceId}/show")
    public String show() throws Exception
    {
        urge = dao.getUrge(instanceId);

        return "show";
    }

    @Service(method = HttpMethod.post, validateType = ValidateType.server)
    @Transactional
    public void save() throws Exception
    {
        dao.save(urge);

        OdFlowInstance instance = new OdFlowInstance();
        instance.setUrged(true);
        instance.setDeadline(urge.getLimitTime());
        instance.setInstanceId(instanceId);
        dao.update(instance);

        UrgeJob.updateJob(urge);
    }
}
