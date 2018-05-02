package com.gzzm.ods.receipt;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * @author camel
 * @date 13-1-23
 */
@Service(url = "/ods/receipt/query")
public class ReceiptQuery extends DeptOwnedQuery<Receipt, Long>
{
    @Inject
    private ReceiptDao dao;

    private String type;

    @Like("document.title")
    private String title;

    @Like("document.sendNumber")
    private String sendNumber;

    @Upper(column = "sendTime")
    private Date time_start;

    @Lower(column = "sendTime")
    private Date time_end;

    @Contains("document.textContent")
    private String text;

    private Boolean sended = true;

    /**
     * 是否只看新消息
     */
    private boolean newOnly;

    public ReceiptQuery()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Boolean getSended()
    {
        return sended;
    }

    public void setSended(Boolean sended)
    {
        this.sended = sended;
    }

    public boolean isNewOnly()
    {
        return newOnly;
    }

    public void setNewOnly(boolean newOnly)
    {
        this.newOnly = newOnly;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (newOnly)
            return "newReplyCount>0";
        else
            return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = (authDeptIds == null || authDeptIds.size() > 3);

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", false) :
                new PageTableView(false);

        view.setClass("${newReplyCount>0?'new_bold':''}");

        view.addComponent("文件标题", "title");
        view.addComponent("发文字号", "sendNumber");
        view.addComponent("发送时间", "time_start", "time_end");
        view.addComponent("文件内容", "text");
        view.addComponent("状态", new CCombox("newOnly", new Object[]{
                new KeyValue<String>("false", "所有反馈"),
                new KeyValue<String>("true", "新的反馈")
        })).setNullable(false);

        view.addColumn("文件标题", "document.title").setWrap(true);
        view.addColumn("发文字号", "document.sendNumber").setWidth("150").setWrap(true);
        view.addColumn("发送时间", new FieldCell("sendTime", "yyy-MM-dd HH:mm")).setWidth("140");
        view.addColumn("查看反馈", new CHref("查看反馈").setAction("showReceipt(${receiptId})")).setWidth("75");

        view.defaultInit();

        view.importJs("/ods/receipt/query.js");

        return view;
    }
}
