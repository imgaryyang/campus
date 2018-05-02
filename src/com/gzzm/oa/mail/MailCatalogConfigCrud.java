package com.gzzm.oa.mail;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2015/10/8
 */
@Service(url = "/oa/mail/catalogconfig")
public class MailCatalogConfigCrud extends UserOwnedNormalCrud<MailCatalogConfig, Integer>
{
    @Inject
    private MailDao dao;

    public MailCatalogConfigCrud()
    {
        setLog(true);
        addOrderBy("configId");
    }

    @Select(field = "entity.sender")
    public PageUserSelector getUserSelector()
    {
        return new PageUserSelector();
    }

    @NotSerialized
    @Select(field = "entity.catalogId")
    public List<MailCatalog> getCatalogs() throws Exception
    {
        return dao.getCatalogs(getUserId());
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("发件人", "senderName");
        view.addColumn("归档目录", "catalog.catalogName");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("邮件来源", "type").setProperty("onchange", "typeChange()");
        User senderUser = getEntity().getSenderUser();
        view.addComponent("发件人", "sender").setProperty("text", senderUser != null ? senderUser.getUserName() : "");
        view.addComponent("发件人", "mailFrom");
        view.addComponent("归档目录", "catalogId");

        view.importCss("/oa/mail/catalogconfig.css");
        view.importJs("/oa/mail/catalogconfig.js");

        view.addDefaultButtons();

        return view;
    }
}
