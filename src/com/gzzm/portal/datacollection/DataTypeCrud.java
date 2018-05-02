package com.gzzm.portal.datacollection;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Service;

/**
 * @author ldp
 * @date 2018/4/25
 */
@Service(url = "/portal/dc/datatype")
public class DataTypeCrud extends BaseNormalCrud<DataType, Integer> {

    private String name;

    public DataTypeCrud() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();

        view.addComponent("报表名称", "name");

        view.addColumn("报表名称", "name");
        view.addColumn("时间精度", "timeType");
        view.addColumn("数量单位", "unit");
        view.addColumn("数据来源", "source");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("报表名称", "name");
        view.addComponent("时间精度", "timeType");
        view.addComponent("数量单位", "unit");
        view.addComponent("数据来源", "source");

        view.addDefaultButtons();

        return view;
    }
}
