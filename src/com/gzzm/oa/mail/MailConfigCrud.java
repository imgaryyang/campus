package com.gzzm.oa.mail;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

/**
 * 邮箱容量维护
 *
 * @author lfx
 * @date 2010-4-2
 */
@Service(url = "/oa/mail/config")
public class MailConfigCrud extends UserConfigCrud
{
    public static class Item
    {
        private int userId;

        private long capacity;

        private long usedSize;

        private long remainingSize;

        private double usedPercent;

        public long getCapacity()
        {
            return capacity;
        }

        public long getUsedSize()
        {
            return usedSize;
        }

        public long getRemainingSize()
        {
            return remainingSize;
        }

        public double getUsedPercent()
        {
            return usedPercent;
        }
    }

    /**
     * 邮件容量配置
     */
    private MailConfig config;

    @Inject
    private MailService service;


    private Item item;

    public MailConfig getConfig()
    {
        return config;
    }

    public void setConfig(MailConfig config)
    {
        this.config = config;
    }

    public Item getItem(Integer userId) throws Exception
    {
        if (item == null || item.userId != userId)
        {
            item = new Item();
            item.userId = userId;

            item.capacity = service.getCapacity(userId);
            item.usedSize = service.getWastage(userId);
            item.remainingSize = item.capacity - item.usedSize;
            if (item.remainingSize < 0)
                item.remainingSize = 0;

            item.usedPercent = (double) item.usedSize / item.capacity;
            if (item.usedPercent > 1)
                item.usedPercent = 1;
        }

        return item;
    }

    public MailConfigCrud()
    {
        setLog(true);
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new AuthDeptDisplay(),
                "deptId", true);

        view.addComponent("姓名", "userName");

        view.addColumn("姓名", "userName");
        view.addColumn("总容量", new FieldCell("crud$.getItem(userId).capacity")
                .setFormat("bytesize"));
        view.addColumn("已使用空间", new FieldCell("crud$.getItem(userId).usedSize")
                .setFormat("bytesize"));
        view.addColumn("剩余空间", new FieldCell("crud$.getItem(userId).remainingSize")
                .setFormat("bytesize"));
        view.addColumn("使用率", new FieldCell("crud$.getItem(userId).usedPercent")
                .setFormat("##.##%"));

        view.addButton(Buttons.query());
        view.makeEditable();

        view.importJs("/platform/organ/userconfig.js");
        view.addButton(new CButton("批量修改容量", "batchShow()"));

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("姓名", "userName").setProperty("readonly", true);

        initComponents(view);
        view.importCss("/oa/mail/userconfig.css");

        view.addDefaultButtons();
        return view;
    }

    @Override
    protected Object createBatchShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        initComponents(view);

        view.addButton("crud.save", "batchSave()");
        view.addButton(Buttons.close());
        view.importCss("/oa/mail/userconfig.css");
        view.importJs("/platform/organ/userconfig.js");

        return view;
    }

    private void initComponents(SimpleDialogView view)
    {
        view.addComponent("邮箱容量", "this.config.capacity").setProperty("datatype", "bytesize");
        if (!"batchShow".equals(getAction()))
        {
            view.addComponent("支持SMTP", "this.config.smtp").setProperty("require", "true");
            view.addComponent("支持POP3", "this.config.pop").setProperty("require", "true");
        }
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        config = service.getConfig(getEntity().getUserId());
    }

    @Override
    public void update(User user) throws Exception
    {
        config.setUserId(getEntity().getUserId());
        config.setCapacity(config.getCapacity());

        getCrudService().save(config);
    }

    @Override
    protected void save(Integer userId) throws Exception
    {
        MailConfig config = getConfig().copy();
        config.setUserId(userId);

        getCrudService().save(config);
    }
}
