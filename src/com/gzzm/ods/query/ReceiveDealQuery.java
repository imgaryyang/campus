package com.gzzm.ods.query;

import com.gzzm.ods.exchange.*;
import com.gzzm.platform.commons.crud.DeptOwnedQuery;
import com.gzzm.platform.organ.DeptTreeModel;
import net.cyan.arachne.annotation.Select;
import net.cyan.crud.annotation.*;

import java.sql.Date;

/**
 * @author camel
 * @date 13-11-18
 */
public class ReceiveDealQuery extends DeptOwnedQuery<Receive, Long>
{
    /**
     * 文件名称
     */
    @Like("receiveBase.document.title")
    private String title;

    /**
     * 原文号
     */
    @Like("receiveBase.document.sendNumber")
    private String sendNumber;

    /**
     * 来文单位
     */
    @Like("receiveBase.document.sourceDept")
    private String sourceDept;

    /**
     * 主题词
     */
    @Like("receiveBase.document.subject")
    private String subject;

    @Lower(column = "receiveBase.sendTime")
    private Date sendTime_start;

    @Upper(column = "receiveBase.sendTime")
    private Date sendTime_end;

    @Lower(column = "receiveBase.acceptTime")
    private Date acceptTime_start;

    @Upper(column = "receiveBase.acceptTime")
    private Date acceptTime_end;

    /**
     * 状态
     */
    @In("receiveBase.state")
    private ReceiveState[] state = new ReceiveState[]{
            ReceiveState.accepted,
            ReceiveState.flowing,
            ReceiveState.end
    };

    @Contains("receiveBase.document.textContent")
    private String text;

    private Integer dealDeptId;

    private DeptTreeModel dealDeptTree;

    private Integer dealUserId;

    public ReceiveDealQuery()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Date getSendTime_start()
    {
        return sendTime_start;
    }

    public void setSendTime_start(Date sendTime_start)
    {
        this.sendTime_start = sendTime_start;
    }

    public Date getSendTime_end()
    {
        return sendTime_end;
    }

    public void setSendTime_end(Date sendTime_end)
    {
        this.sendTime_end = sendTime_end;
    }

    public Date getAcceptTime_start()
    {
        return acceptTime_start;
    }

    public void setAcceptTime_start(Date acceptTime_start)
    {
        this.acceptTime_start = acceptTime_start;
    }

    public Date getAcceptTime_end()
    {
        return acceptTime_end;
    }

    public void setAcceptTime_end(Date acceptTime_end)
    {
        this.acceptTime_end = acceptTime_end;
    }

    public ReceiveState[] getState()
    {
        return state;
    }

    public void setState(ReceiveState[] state)
    {
        this.state = state;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Integer getDealDeptId()
    {
        return dealDeptId;
    }

    public void setDealDeptId(Integer dealDeptId)
    {
        this.dealDeptId = dealDeptId;
    }

    public Integer getDealUserId()
    {
        return dealUserId;
    }

    public void setDealUserId(Integer dealUserId)
    {
        this.dealUserId = dealUserId;
    }

    @Select(field = "dealDeptId")
    public DeptTreeModel getDealDeptTree()
    {
        if (dealDeptTree == null)
        {
            dealDeptTree = new DeptTreeModel();
            dealDeptTree.setRootId(getDeptId());
        }

        return dealDeptTree;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }
}
