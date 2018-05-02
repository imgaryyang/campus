package com.gzzm.ods.bak;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.file.CommonFile;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * @author zjw
 * @date 12-10-16
 */
@Service(url = "/ods/bak/instance")
public class OdDeptInstanceBakCrud extends DeptOwnedNormalCrud<OdDeptInstanceBak, Integer>
{
    @Inject
    private OdDeptInstanceBakService service;

    @Lower(column = "createTime")
    private Date time_start;

    @Upper(column = "createTime")
    private Date time_end;

    public OdDeptInstanceBakCrud()
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
        super.beforeInsert();

        getEntity().setCreateTime(new java.util.Date());
        getEntity().setCreator(userOnlineInfo.getUserId());

        return true;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
        setDefaultDeptId();
    }

    @Service(method = HttpMethod.post)
    public void createZip(Integer[] bakIds) throws Exception
    {
        service.setLisenter(RequestContext.getContext().getProgressInfo(OdBakProgressInfo.class));

        for (Integer bakId : bakIds)
        {
            service.createZip(bakId, userOnlineInfo.getUserId());
        }
    }

    @Service(method = HttpMethod.post)
    public void deleteInstances(Integer bakId) throws Exception
    {
        service.deleteInstances(bakId);
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 1;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId") :
                new PageTableView(false);

        view.addComponent("创建时间", "time_start", "time_end");

        view.addColumn("名称", "backName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("创建人", "createUser.userName").setWidth("100");
        view.addColumn("查看备份文件", new CButton("查看备份文件", "showInstances(${backId})"));
        view.addColumn("生成压缩包", new CButton("生成压缩包", "createZip(${backId});"));
        view.addColumn("删除公文", new CButton("删除公文", "deleteInstances(${backId});"));
        view.addColumn("下载", new ConditionComponent().add("path!=null",
                new CHref("下载", "/ods/bak/instance/${backId}/down").setTarget("_blank"))).setWidth("60");

        view.defaultInit(false);

        view.importJs("/ods/bak/instance.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("名称", "backName");

        view.addDefaultButtons();

        return view;
    }

    /**
     * 下载文件
     *
     * @return 对应备份文件
     * @throws Exception 数据操作异常
     */
    @Service(url = "/ods/bak/instance/{$0}/down")
    public InputFile download(Integer backId) throws Exception
    {
        OdDeptInstanceBak bak = service.getDao().getOdDeptInstanceBak(backId);
        String path = bak.getPath();
        if (!StringUtils.isEmpty(path))
        {
            CommonFile file = service.getCommonFileService().getFile(path);
            if (file != null && file.exists())
                return new InputFile(file.getInputable(), bak.getBackName() + ".zip");
        }

        return null;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }
}
