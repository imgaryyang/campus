package com.gzzm.oa.mail;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 个人文件夹维护
 *
 * @author : wmy
 * @date : 2010-3-19
 */
@Service(url = "/oa/mail/catalog")
public class MailCatalogCrud extends UserOwnedNormalCrud<MailCatalog, Long>
{
    @Like
    private String catalogName;

    /**
     * 邮件操作类
     */
    @Inject
    private MailDao dao;

    /**
     * 是否在桌面上显示
     */
    private boolean desktop;

    public MailCatalogCrud()
    {
        setLog(true);
    }

    @Override
    public MailCatalog getEntity(Long catalogId) throws Exception
    {
        if (catalogId < 0)
        {
            String catalogName = null;
            switch (catalogId.intValue())
            {
                case -1:
                    catalogName = "收件箱";
                    break;
                case -2:
                    catalogName = "草稿箱";
                    break;
                case -3:
                    catalogName = "发件箱";
                    break;
                case -4:
                    catalogName = "已删除";
                    break;
            }

            if (catalogName != null)
                return new MailCatalog(catalogId, catalogName, getUserId());
        }
        return super.getEntity(catalogId);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createListView()
    {
        PageTableView view = new PageTableView();

        view.addColumn("目录名称", new HrefCell("catalogName").setAction("openCatalog(${catalogId})"));
        view.addColumn("邮件数", new FieldCell("info.mailCount").setOrderable(false));
        view.addColumn("新邮件", new FieldCell("info.notReadCount").setOrderable(false));
        view.addColumn("占用空间", new FieldCell("info.usedSize").setFormat("bytesize").setOrderable(false));

        if (desktop)
        {
            view.setPage("table");
            view.importCss("/oa/mail/desktop.css");
        }
        else
        {
            view.addColumn("清空", new CButton("清空", "clearMails(${catalogId})"));
            view.addColumn(view.getEditColumn()).setDisplay("${catalogId>0}");

            view.addDefaultButtons();
            view.addButton(Buttons.sort());

            view.setCheckable("${catalogId>0}");
        }

        view.importJs("/oa/mail/catalog.js");

        return view;
    }

    /**
     * 清空 文件夹镇中的邮件，已删除的将被彻底删除
     *
     * @param catalogId 目录Id
     * @throws Exception 数据库更新操作异常
     */
    @ObjectResult
    @Service
    public void clearMails(Integer catalogId) throws Exception
    {
        if (catalogId == -1)
        {
            //收件箱
            dao.clearMailsWithType(getUserId(), MailType.received);
        }
        else if (catalogId == -2)
        {
            //草稿箱
            dao.clearMailsWithType(getUserId(), MailType.draft);
        }
        else if (catalogId == -3)
        {
            //发件箱
            dao.clearMailsWithType(getUserId(), MailType.sended);
        }
        else if (catalogId == -4)
        {
            //已删除
            dao.clearDeletedMails(getUserId());
        }
        else
        {
            dao.clearMailsWithCatalog(getUserId(), catalogId);
        }
    }

    @Override
    public boolean beforeDelete(Long key) throws Exception
    {
        if (dao.checkCatalogsUsed(key))
            throw new NoErrorException("选择的目录不为空");

        return super.beforeDelete(key);
    }

    @Override
    public boolean beforeDeleteAll() throws Exception
    {
        if (dao.checkCatalogsUsed(getKeys()))
            throw new NoErrorException("选择的目录不为空");

        return super.beforeDeleteAll();
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("目录名称", "catalogName");

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected void afterQuery() throws Exception
    {
        super.afterQuery();

        List<MailCatalog> list = getList();

        list.add(0, getEntity(-4L));
        list.add(0, getEntity(-3L));
        list.add(0, getEntity(-2L));
        list.add(0, getEntity(-1L));
    }

    public boolean isDesktop()
    {
        return desktop;
    }

    public void setDesktop(boolean desktop)
    {
        this.desktop = desktop;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
    }
}
