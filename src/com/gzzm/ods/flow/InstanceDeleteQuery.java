package com.gzzm.ods.flow;

import com.gzzm.ods.business.BusinessTag;
import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CImage;

import java.sql.Date;
import java.util.Collection;

/**
 * 已删除的文件的查询
 *
 * @author camel
 * @date 13-12-4
 */
@Service(url = "/ods/flow/instance_delete")
public class InstanceDeleteQuery extends BaseQueryCrud<OdFlowInstanceDelete, Long>
{
    @AuthDeptIds
    @NotSerialized
    private Collection<Integer> authDeptIds;

    /**
     * 类型
     */
    private String[] type;

    @Equals("instance.business.tag")
    private BusinessTag tag;

    @Like("instance.document.title")
    private String title;

    @Lower(column = "deleteTime")
    private Date time_start;

    @Upper(column = "deleteTime")
    private Date time_end;

    /**
     * 发文编号
     */
    @Like("instance.document.sendNumber")
    private String sendNumber;

    /**
     * 主题词
     */
    @Like("instance.document.subject")
    private String subject;

    /**
     * 来文单位
     */
    @Like("instance.document.sourceDept")
    private String sourceDept;

    @Contains("instance.document.textContent")
    private String text;

    @Equals("instance.tag")
    private String odTag;

    public InstanceDeleteQuery()
    {
        addOrderBy("deleteTime", OrderType.desc);
    }

    public String[] getType()
    {
        return type;
    }

    public void setType(String[] type)
    {
        this.type = type;
    }

    public BusinessTag getTag()
    {
        return tag;
    }

    public void setTag(BusinessTag tag)
    {
        this.tag = tag;
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

    public String getOdTag()
    {
        return odTag;
    }

    public void setOdTag(String odTag)
    {
        this.odTag = odTag;
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (authDeptIds != null)
            return "deptId in ?authDeptIds or instance.deptId in ?authDeptIds or instance.dealDeptId in ?authDeptIds";
        else
            return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("标题", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("作废时间", "time_start", "time_end");
        view.addMoreComponent("发文字号", "sendNumber");
        view.addMoreComponent("来文单位", "sourceDept");
        view.addMoreComponent("编号", "serial");
        view.addMoreComponent("主题词", "subject");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment")))
                .setDisplay("${instance.document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("标题", new HrefCell("instance.document.titleText").setProperty("style", "white-space:normal")
                .setAction("openDocument(${instanceId},true)")).setOrderFiled("document.title");
        view.addColumn("发文字号", "instance.document.sendNumber");
        view.addColumn("来文单位", "instance.document.sourceDept");
        view.addColumn("编号", "instance.serial");
        view.addColumn("类型", "instance.typeName").setWidth("45");
        view.addColumn("作废时间", "deleteTime").setWidth("125");
        view.addColumn("作废人", "user.userName").setWidth("90");

        view.addButton(Buttons.query());
        view.importJs("/ods/flow/instance.js");

        return view;
    }
}
