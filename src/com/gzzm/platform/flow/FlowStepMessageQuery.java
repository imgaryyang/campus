package com.gzzm.platform.flow;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.KeyValue;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CCombox;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2015/7/15
 */
@Service(url = "/flow/message")
public class FlowStepMessageQuery extends UserOwnedNormalCrud<FlowStepMessage, Long>
{
    private Boolean readed;

    @Like("instance.title")
    private String title;

    @In("instance.flowTag")
    private String[] flowTag;

    @Inject
    private DefaultSystemFlowDao dao;

    public FlowStepMessageQuery()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String[] getFlowTag()
    {
        return flowTag;
    }

    public void setFlowTag(String[] flowTag)
    {
        this.flowTag = flowTag;
    }

    public Boolean getReaded()
    {
        return readed;
    }

    public void setReaded(Boolean readed)
    {
        this.readed = readed;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        readed = false;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.setClass("${!readed?'new_bold':''}");

        view.addComponent("标题", "title");
        view.addComponent("状态", new CCombox("readed", new Object[]{
                new KeyValue<String>("false", "未阅读"),
                new KeyValue<String>("true", "已阅读")
        }));

        view.addColumn("标题", new HrefCell("(instance.title==null||instance.title.length()==0)?'无标题':instance.title")
                .setAction("display(${instanceId},'${instance.flowTag}')")).setOrderFiled("instance.title")
                .setWidth("400");
        view.addColumn("内容", "content").setAutoExpand(true);
        view.addColumn("来源", "sendUser.userName").setWidth("100");
        view.addColumn("时间", "sendTime");

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        view.addButton("批量阅读", "setReaded();");

        view.importJs("/platform/flow/message.js");

        return view;
    }

    @Service
    @ObjectResult
    public Long getStepId(Long instanceId) throws Exception
    {
        return Long.valueOf(dao.getLastStepIdWithReceiver(instanceId.toString(), getUserId().toString()));
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void setReaded()
    {
        dao.setMessageReaded(getKeys());
    }
}
