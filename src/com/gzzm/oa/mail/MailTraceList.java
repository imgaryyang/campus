package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.KeyBaseCrud;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.LabelCell;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 显示邮件跟踪列表的页面
 *
 * @author camel
 * @date 2010-6-19
 */
@Service
public class MailTraceList extends BaseListCrud<MailTrace> implements KeyBaseCrud<MailTrace, String>
{
    @Inject
    private MailDao dao;

    private Long bodyId;

    private String title;

    private String[] keys;

    public MailTraceList()
    {
        setPageSize(-1);
    }

    public String[] getKeys()
    {
        return keys;
    }

    public void setKeys(String[] keys)
    {
        this.keys = keys;
    }

    @Override
    public Class<String> getKeyType()
    {
        return String.class;
    }

    @Override
    public String getKey(MailTrace entity) throws Exception
    {
        return entity.getKey();
    }

    @Service(url = "/oa/mail/trace/{bodyId}")
    public String showList(Integer pageNo) throws Exception
    {
        return super.showList(pageNo);
    }

    protected void loadList() throws Exception
    {
        //查询内部邮件和外部邮件的记录 
        List<Mail> mails = dao.getMailsByBodyId(bodyId);
        List<MailSmtpRecord> records = dao.getAllSmtpRecords(bodyId);

        List<MailTrace> traces = new ArrayList<MailTrace>(mails.size() + records.size());

        for (Mail mail : mails)
            traces.add(new MailTrace(mail));

        for (MailSmtpRecord record : records)
            traces.add(new MailTrace(record));

        sort(traces);
        setList(traces);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);
        view.setCheckable("${local&&readTime==null}");

        view.setTitle("邮件跟踪-{title}");
        view.setRemark("跟踪邮件接收者的阅读和回复情况");

        view.addButton("撤回", "back()").setIcon(Tools.getCommonIcon("left3"));
        view.addButton("查看回复", "showReplies(" + bodyId + ")").setIcon("/oa/mail/icons/link.gif");
        view.addButton(Buttons.export("xls"));

        view.addColumn("接收者", "receiver").setWidth("300");
        view.addColumn("状态", "state");
        view.addColumn("阅读时间", "readTime");

        view.addColumn("错误信息", new LabelCell("error").setProperty("style", "white-space:normal;"))
                .setAutoExpand(true);

        view.importJs("/oa/mail/mail.js");

        return view;
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    @NotSerialized
    public String getTitle() throws Exception
    {
        if (title == null)
            title = dao.getBody(bodyId).getTitle();

        return title;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("邮件跟踪_{title}");
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void back() throws Exception
    {
        if (keys != null)
        {
            for (String key : keys)
            {
                if (key.startsWith("local_"))
                {
                    dao.backMail(Long.valueOf(key.substring(6)));
                }
            }
        }
    }
}
