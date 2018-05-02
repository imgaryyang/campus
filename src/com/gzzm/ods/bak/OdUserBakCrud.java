package com.gzzm.ods.bak;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.file.CommonFile;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author zjw
 * @date 12-10-16
 */
@Service(url = "/ods/bak/user")
public class OdUserBakCrud extends UserOwnedNormalCrud<OdUserBak, Long>
{
    @Inject
    private OdUserBakService service;

    @Lower(column = "createTime")
    private Date time_start;

    @Upper(column = "createTime")
    private Date time_end;

    public OdUserBakCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        java.util.Date createTime = new java.util.Date();
        getEntity().setCreateTime(createTime);
        return true;
    }

    @Service(method = HttpMethod.post)
    public void createZip(Long[] bakIds) throws Exception
    {
        service.setLisenter(RequestContext.getContext().getProgressInfo(OdBakProgressInfo.class));

        for (Long bakId : bakIds)
        {
            service.createZip(bakId);
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("创建时间", "time_start", "time_end");

        view.addColumn("名称", "backName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("开始时间", "startTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("下载", new ConditionComponent()
                .add("path!=null", new CHref("下载", "/ods/bak/user/${backId}/down").setTarget("_blank")));

        view.defaultInit(false);

        view.importJs("/ods/bak/user.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("名称", "backName");
        view.addComponent("开始时间", "startTime");
        view.addComponent("结束时间", "endTime");

        view.addDefaultButtons();

        view.importJs("/ods/bak/user.js");

        return view;
    }

    /**
     * 下载文件
     *
     * @return 对应备份文件
     * @throws Exception 数据操作异常
     */
    @Service(url = "/ods/bak/user/{$0}/down")
    public InputFile download(Long backId) throws Exception
    {
        OdUserBak odUserBak = service.getDao().getOdUserBak(backId);
        String path = odUserBak.getPath();
        if (!StringUtils.isEmpty(path))
        {
            CommonFile file = service.getCommonFileService().getFile(path);
            if (file != null && file.exists())
                return new InputFile(file.getInputable(), odUserBak.getBackName() + ".zip");
        }

        return null;
    }
}
