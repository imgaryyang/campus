package com.gzzm.ods.archive;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 2016/8/28
 */
@Service(url = "/ods/archive/timelimit")
public class ArchiveTimeLimitCrud extends BaseNormalCrud<ArchiveTimeLimit, Integer>
{
    public ArchiveTimeLimitCrud()
    {
        setLog(true);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("保管期限", "timeLimit");
        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();


        view.addColumn("保管期限", "timeLimit");
        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }
}
