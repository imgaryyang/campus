package com.gzzm.platform.devolve;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.PageUserSelector;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;

/**
 * @author camel
 * @date 12-12-27
 */
@Service
public class DevolvePage
{
    @Inject
    private DevolveDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    @NotSerialized
    private List<DevolverItem> items;

    @Require
    private Integer fromUserId;

    @Require
    private Integer toUserId;

    @Require
    private String[] ids;

    private Date startTime;

    private Date endTime;

    private PageUserSelector formUserSelector;

    private PageUserSelector toUserSelector;

    public DevolvePage()
    {
    }

    public List<DevolverItem> getItems()
    {
        return items;
    }

    public Integer getFromUserId()
    {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId)
    {
        this.fromUserId = fromUserId;
    }

    public Integer getToUserId()
    {
        return toUserId;
    }

    public void setToUserId(Integer toUserId)
    {
        this.toUserId = toUserId;
    }

    public String[] getIds()
    {
        return ids;
    }

    public void setIds(String[] ids)
    {
        this.ids = ids;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    @Select(field = "fromUserId")
    public PageUserSelector getFormUserSelector()
    {
        if (formUserSelector == null)
            formUserSelector = new PageUserSelector();

        return formUserSelector;
    }

    @Select(field = "toUserId")
    public PageUserSelector getToUserSelector()
    {
        if (toUserSelector == null)
            toUserSelector = new PageUserSelector();

        return toUserSelector;
    }

    @Service(url = "/platform/devolve")
    public String show() throws Exception
    {
        return "devolve";
    }

    @Service(url = "/platform/devolve", method = HttpMethod.post)
    @Transactional
    public void devolve() throws Exception
    {
        if (items != null)
        {
            if (startTime != null && Null.isNull(startTime))
                startTime = null;

            if (endTime != null && Null.isNull(endTime))
                endTime = null;

            Date endTime = this.endTime;
            if (endTime != null)
                endTime = new Date(DateUtils.addDate(endTime, 1).getTime());

            for (String id : ids)
            {
                for (DevolverItem item : items)
                {
                    if (item.getId().equals(id))
                    {
                        item.getDevolver().devolve(fromUserId, toUserId, startTime, endTime);
                        break;
                    }
                }
            }

            Devolve devolve = new Devolve();
            devolve.setUserId(userOnlineInfo.getUserId());
            devolve.setDeptId(userOnlineInfo.getDeptId());
            devolve.setDevolveTime(new java.util.Date());
            devolve.setFromUserId(fromUserId);
            devolve.setToUserId(toUserId);
            devolve.setScopes(StringUtils.concat(ids, ","));
            devolve.setStartTime(startTime);
            devolve.setEndTime(this.endTime);

            dao.add(devolve);
        }
    }
}
