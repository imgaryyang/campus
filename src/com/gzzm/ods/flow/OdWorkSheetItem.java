package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.workday.WorkDayService;
import net.cyan.commons.util.*;
import net.cyan.valmiki.flow.FlowWorkSheetItem;

import java.util.Date;

/**
 * 公文待办列表
 *
 * @author camel
 * @date 11-10-13
 */
public class OdWorkSheetItem extends FlowWorkSheetItem
{
    /**
     * 公文流转实例
     */
    private OdFlowInstance od;

    public OdWorkSheetItem()
    {
    }

    public OdFlowInstance getOd()
    {
        return od;
    }

    public void setOd(OdFlowInstance od)
    {
        this.od = od;
    }

    public String getTypeName()
    {
        if ("copy".equals(od.getType()))
        {
            return getNodeName();
        }

        if ("receive".equals(od.getType()))
        {
            return od.getReceiveBase().getSendType();
        }

        return od.getTypeName();
    }

    public String getTitleText()
    {
        OfficeDocument document = od.getDocument();
        if (document != null)
        {
            String title = document.getTitle();
            if (!StringUtils.isEmpty(title))
                return title;
        }

        if (StringUtils.isBlank(getTitle()))
        {
            return "无标题";
        }
        else
        {
            return getTitle();
        }
    }

    public boolean isAttachment()
    {
        return getOd().getDocument().isAttachment();
    }

    public String getOtherFileName()
    {
        OfficeDocument document = od.getDocument();
        if (document != null)
        {
            DocumentText text = document.getText();
            if (text != null)
                return text.getOtherFileName();
        }

        return null;
    }

    public Long getDocumentId()
    {
        return od.getDocumentId();
    }

    public String getEncodedDocumentId()
    {
        return OfficeDocument.encodeId(getDocumentId());
    }

    public String getSourceDept()
    {
        String type = od.getType();
        if (!"send".equals(type) && !"inner".equals(type))
        {
            return od.getDocument().getSourceDept();
        }

        return null;
    }

    public String getSource()
    {
        String type = od.getType();
        if ("send".equals(type) || "inner".equals(type))
        {
            return od.getCreateUser().getUserName();
        }
        else
        {
            return od.getDocument().getSourceDept();
        }
    }

    @Override
    public String getSourceName()
    {
        if (isMultiple())
        {
            if (getWaitForItems() != null && getWaitForItems().size() > 0)
            {
                String s1 = Tools.getMessage("ods.flow.waitForMessage1",
                        getSubItems().size() + getWaitForItems().size(), getSubItems().size());

                String receivers = getWaitForItems().get(0).getAllReceivers();
                String s2 = Tools.getMessage("ods.flow.waitForMessage2", new Object[]{receivers});

                if (StringUtils.isEmpty(s2))
                    return s1;
                else if (StringUtils.isEmpty(s1))
                    return s2;

                return s1 + "\n" + s2;
            }
            else
            {
                return getSubItems().get(0).getSourceName();
            }
        }
        else
        {
            return super.getSourceName();
        }
    }

    public Integer getWorkday() throws Exception
    {
        Date endTime;
        if (od.getState() == OdFlowInstanceState.unclosed)
            endTime = new Date();
        else
            endTime = od.getEndTime();

        if (endTime == null)
            endTime = new Date();

        return WorkDayService.getInstance().diff(od.getStartTime(), endTime, od.getDeptId());
    }

    public String getRemainingTime() throws Exception
    {
        if (od.getState() == OdFlowInstanceState.closed)
            return null;

        Date deadline = od.getDeadline();
        if (deadline == null)
            return null;

        Date now = new Date();
        if (now.after(deadline))
            return null;

        int day = WorkDayService.getInstance()
                .diff(DateUtils.truncate(now), DateUtils.truncate(deadline), od.getDeptId());

        if (day > 0)
            return day + "工作日";
        else
            return DateUtils.getHoursInterval(now, deadline) + "小时";
    }
}
