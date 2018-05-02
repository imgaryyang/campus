package com.gzzm.platform.opinion;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.*;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 11-10-19
 */
@Service(url = "/opinion/crud")
public class OpinionPage extends UserOwnedNormalCrud<Opinion, Integer>
{
    @ConfigValue(name = "OPINION_AUTOADD", defaultValue = "false")
    private Boolean defaultAutoAdd;

    @UserId
    private Integer userId;

    @Inject
    private OpinionService service;

    @NotSerialized
    private Boolean autoAdd;

    @NotSerialized
    private Boolean split;

    public OpinionPage()
    {
        addOrderBy("orderId");
        addOrderBy("frequency", OrderType.desc);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"userId"};
    }

    public Boolean getAutoAdd()
    {
        return autoAdd;
    }

    public void setAutoAdd(Boolean autoAdd)
    {
        this.autoAdd = autoAdd;
    }

    public Boolean getSplit() {
        return split;
    }

    public void setSplit(Boolean split) {
        this.split = split;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        Opinion entity = getEntity();
        if (StringUtils.isEmpty(entity.getTitle()))
            entity.setTitle(entity.getContent());

        CrudUtils.initOrderValue(entity, this, false);

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
//        getEntity().setTitle(getEntity().getContent());

        return super.beforeUpdate();
    }

    @Service(url = "/opinion/edit")
    public String edit()
    {
        OpinionConfig config = service.getDao().getOpinionConfig(userId);

        if (config != null) {
            autoAdd = config.getAutoAdd();
            split = config.getSplit() == null ? false : config.getSplit();
        }
        else
            autoAdd = defaultAutoAdd;

        return "opinion";
    }

    @Service(url = "/opinion/simple")
    public String edit_simple()
    {
        return "simple_opinion";
    }

    @Service(url = "/opinion/html")
    public String edit_html()
    {
        return "html_opinion";
    }

    @Service
    public void modifyAutoAdd(Boolean autoAdd) throws Exception
    {
        OpinionConfig config = new OpinionConfig();
        config.setUserId(userId);
        config.setAutoAdd(autoAdd);
        if(autoAdd == null || !autoAdd)
            config.setSplit(false);

        service.getDao().save(config);
    }

    @Service
    public void modifySplit(Boolean split) throws Exception
    {
        OpinionConfig config = new OpinionConfig();
        config.setUserId(userId);
        config.setSplit(split);
        if(split != null && split)
            config.setAutoAdd(true);
        service.getDao().save(config);
    }

    @Override
    protected Object createListView() throws Exception
    {
        if (!"edit".equals(getForward()))
        {
            PageTableView view = new PageTableView();

            view.addColumn("意见标题", "title");
            view.addColumn("意见内容", "content");

            view.defaultInit(false);
            view.addButton(Buttons.sort());

            return view;
        }
        else
        {
            return null;
        }
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("意见标题", "title");
        view.addComponent("意见内容", new CTextArea("content"));
        view.addDefaultButtons();

        return view;
    }

    @Service
    @ObjectResult
    public void addContent(String content, boolean split) throws Exception
    {
        if (content != null)
            service.addContent(content, userId, split);
    }
}
