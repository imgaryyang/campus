package com.gzzm.portal.cms.information.visit;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.information.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.*;

/**
 * @author sjy
 * @date 2018/3/21
 */
@Service(url = "/portal/informationVisitTotal")
public class InfoVisitTotalQueryPage extends InformationCrud
{

    @Inject
    private InfoVisitDao dao;

    protected void initListView(PageTableView view) throws Exception
    {
        view.setHasCheckbox(false);
        view.addComponent("标题", "title");
        addUpdateTimeComponent(view);
        view.addComponent("采编部门", "deptId");
        view.addComponent("包括下属部门", new CCombox("includeSubDepts").setNullable(false));
        view.addComponent("状态", "state");

        view.addColumn("所属栏目", "channel.channelName").setOrderFiled("channel.leftValue").setWidth("150");
        view.addColumn("标题", "title").setWidth("200").setAutoExpand(true);
        view.addColumn("采编人", "createUser.userName").setWidth("80");
        view.addColumn("更新时间", new FieldCell("updateTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("135");
        view.addColumn("采编部门", "dept.allName()").setWidth("150").setOrderFiled("dept.leftValue");
        view.addColumn("状态", "state");
        addVisitCountColumn(view);
        addClickCountColumn(view);
        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));
    }

    private void addVisitCountColumn(PageTableView view) throws Exception
    {
        view.addColumn("访问量", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                InformationEdit edit = (InformationEdit) entity;
                Long count = dao.countInfoVisitTotal(edit.getInformationId());
                if (count == null)
                {
                    count = 0L;
                }
                return count;
            }
        });
    }

    private void addClickCountColumn(PageTableView view) throws Exception
    {
        view.addColumn("点击量", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                InformationEdit edit = (InformationEdit) entity;
                Long count = dao.countInfoClickTotal(edit.getInformationId());
                if (count == null)
                {
                    count = 0L;
                }
                return count;
            }
        });
    }

    protected void initExportView(PageTableView view) throws Exception
    {
        view.addColumn("所属栏目", "channel.channelName");
        view.addColumn("索引号", "indexCode");
        view.addColumn("标题", "title").setWidth("250").setAutoExpand(true);
        view.addColumn("采编部门", "dept.allName()");
        view.addColumn("采编人", "createUser.userName");
        view.addColumn("发布时间", new FieldCell("publishTime").setFormat("yyyy-MM-dd"));
        view.addColumn("排序号", "orderId");
        addVisitCountColumn(view);
        addClickCountColumn(view);
    }
}
