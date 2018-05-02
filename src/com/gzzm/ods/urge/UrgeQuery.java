package com.gzzm.ods.urge;

import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CImage;

import java.sql.Date;

/**
 * 督办查询
 *
 * @author camel
 * @date 13-12-1
 */
@Service(url = "/od/urge")
public class UrgeQuery extends DeptOwnedQuery<OdFlowInstance, Long>
{
    /**
     * 状态
     */
    private OdFlowInstanceState state;

    @Like("document.title")
    private String title;

    @Lower(column = "startTime")
    private Date time_start;

    @Upper(column = "startTime")
    private Date time_end;

    /**
     * 发文编号
     */
    @Like("document.sendNumber")
    private String sendNumber;

    /**
     * 主题词
     */
    @Like("document.subject")
    private String subject;

    /**
     * 来文单位
     */
    @Like("document.sourceDept")
    private String sourceDept;

    @Contains("document.textContent")
    private String text;

    public UrgeQuery()
    {
        addOrderBy("startTime", OrderType.desc);
    }

    public OdFlowInstanceState getState()
    {
        return state;
    }

    public void setState(OdFlowInstanceState state)
    {
        this.state = state;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "state<2 and urged=1";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("标题", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("拟稿时间", "time_start", "time_end");
        view.addMoreComponent("发文字号", "sendNumber");
        view.addMoreComponent("来文单位", "sourceDept");
        view.addMoreComponent("编号", "serial");
        view.addMoreComponent("主题词", "subject");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment")))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("标题", new HrefCell("document.titleText").setProperty("style", "white-space:normal")
                .setAction("openDocument(${instanceId})")).setOrderFiled("document.title");
        view.addColumn("发文字号", "document.sendNumber");
        view.addColumn("来文单位", "document.sourceDept");
        view.addColumn("编号", "serial");
        view.addColumn("类型", "typeName").setWidth("45");
        view.addColumn("督办人", "urge.user.userName").setWidth("90");
        view.addColumn("督办时间", "urge.createTime").setWidth("125");
        view.addColumn("督办意见", "urge.content").setWidth("200").setWrap(true);


        view.addButton(Buttons.query());


        view.importJs("/ods/urge/list.js");

        return view;
    }
}
