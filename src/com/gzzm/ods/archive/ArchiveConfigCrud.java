package com.gzzm.ods.archive;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/8/30
 */
@Service(url = "/ods/archive/config")
public class ArchiveConfigCrud extends BaseNormalCrud<ArchiveConfig, Integer>
{
    @Inject
    private ArchiveService service;

    public ArchiveConfigCrud()
    {
        setLog(true);
    }

    @Override
    public ArchiveConfig getEntity(Integer deptId) throws Exception
    {
        return service.getConfig(deptId);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("全宗名称", "generalName");
        view.addComponent("全宗号", "generalNo");

        view.addDefaultButtons();

        return view;
    }
}
