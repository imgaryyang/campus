package com.gzzm.oa.mail;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.CPassword;
import net.cyan.nest.annotation.Inject;

/**
 * 用户外部邮箱帐号配置
 *
 * @author lfx
 * @date 2010-4-2
 */
@Service(url = "/oa/mail/account")
public class MailAccountCrud extends UserOwnedNormalCrud<MailAccount, Integer>
{
    @Inject
    private MailService service;


    private MailConfig config;

    @UserId
    private Integer userId;

    public MailAccountCrud()
    {
        setLog(true);
    }

    private MailConfig getMailConfig() throws Exception
    {
        if (config == null)
            config = service.getConfig(userId);

        return config;
    }


    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 4;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("邮箱地址", "address");
        view.addColumn("昵称", "nickName");

        if (getMailConfig().isSmtp())
            view.addColumn("SMTP服务器", "smtpServer");

        if (getMailConfig().isPop())
            view.addColumn("POP3服务器", "pop3Server");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("邮件地址", "address");
        view.addComponent("昵称", "nickName");

        if (getMailConfig().isSmtp())
            view.addComponent("SMTP服务器", "smtpServer");

        if (getMailConfig().isPop())
            view.addComponent("POP3服务器", "pop3Server");

        view.addComponent("用户名", "userName");
        view.addComponent("密码", new CPassword("password"));

        view.addDefaultButtons();
        view.importJs("/oa/mail/account.js");

        return view;
    }
}
