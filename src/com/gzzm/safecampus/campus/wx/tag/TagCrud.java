package com.gzzm.safecampus.campus.wx.tag;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * 微信用户标签管理
 *
 * @author Neo
 * @date 2018/4/23 11:10
 */
@Service(url = "/campus/wx/tag/tag")
public class TagCrud extends BaseNormalCrud<Tag, Integer>
{
    @Inject
    private TagService tagService;

    public TagCrud()
    {
        addOrderBy("orderId");
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 3;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        view.addComponent("标签名称", "tagName");
        view.addColumn("标签Id", "tagId");
        view.addColumn("标签名称", "tagName");
        view.addColumn("关联身份", "identifyType");
        view.addButton(Buttons.add());
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("标签名称", "tagName");
        view.addComponent("关联身份", "identifyType");
        view.addDefaultButtons();
        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        Tag entity = getEntity();
        entity.setTagId(tagService.tagCreate(entity.getTagName()));
        return super.beforeInsert();
    }
}
