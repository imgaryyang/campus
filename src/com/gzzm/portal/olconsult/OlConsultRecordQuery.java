package com.gzzm.portal.olconsult;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.*;

/**
 * User: wym
 * Date: 13-6-3
 * Time: 上午11:47
 * 查看聊天记录
 */
@Service(url = "/portal/olconsult/record")
public class OlConsultRecordQuery extends BaseQueryCrud<OlConsultRecord, Integer>
{
    private Integer consultId;

    public OlConsultRecordQuery()
    {
        addOrderBy("chatTime");
    }

    public Integer getConsultId()
    {
        return consultId;
    }

    public void setConsultId(Integer consultId)
    {
        this.consultId = consultId;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("发送人", new FieldCell("flag")
        {
            @Override
            public String display(Object entity) throws Exception
            {
                OlConsultRecord record = (OlConsultRecord) entity;

                if (record.getFlag() == 0)
                {
                    return "咨询人(" + record.getConsult().getUserName() + ")";
                }
                else
                {
                    return record.getConsult().getSeat().getSeatName() + "(" +
                            record.getConsult().getAnserUser().getUserName() + ")";
                }
            }
        }).setWidth("300").setAlign(Align.left);
        view.addColumn("时间", "chatTime");
        view.addColumn("内容", "content");

        view.defaultInit();

        return view;
    }
}
