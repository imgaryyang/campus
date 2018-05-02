package com.gzzm.oa.notice;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 内部信息类型维护界面
 *
 * @author czf
 * @date 2010-3-16
 */
@Service(url = "/oa/notice/type")
public class NoticeTypeCrud extends DeptOwnedNormalCrud<NoticeType, Integer>
{
    @Inject
    private NoticeDao dao;

    @Like
    private String typeName;

    private Integer sortId;

    public NoticeTypeCrud()
    {
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public Integer getSortId()
    {
        return sortId;
    }

    public void setSortId(Integer sortId)
    {
        this.sortId = sortId;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        if (sortId == null)
            return super.getOrderWithFields();
        else
            return new String[]{"deptId", "sortId"};
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        if (sortId != null)
            getEntity().setSortId(sortId);

        return true;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
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
        PageTableView view;
        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
        {
            view = new PageTableView();
        }
        else
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);
        }

        view.addComponent("名称", "typeName");

        view.addColumn("名称", "typeName");
        if (sortId == null)
            view.addColumn("类别", "sort.sortName");
        view.addColumn("模板", "template.templateName");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("名称", "typeName");
        if (sortId == null)
            view.addComponent("类别", "sortId");
        view.addComponent("模板", "templateId");

        view.addDefaultButtons();

        return view;
    }

    @NotSerialized
    @Select(field = "entity.templateId")
    public List<NoticeTemplate> getTemplates() throws Exception
    {
        return dao.getAllNoticeTemplates();
    }

    @NotSerialized
    @Select(field = "entity.sortId")
    public List<NoticeSort> getSorts() throws Exception
    {
        if (getEntity() != null)
        {
            return dao.getSorts(dao.getDept(getEntity().getDeptId()).allParentDeptIds());
        }

        return null;
    }
}
