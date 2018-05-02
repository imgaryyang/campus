package com.gzzm.ods.bak;

import com.gzzm.ods.receivetype.ReceiveTypeTreeModel;
import com.gzzm.ods.sendnumber.SendNumber;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.file.CommonFile;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author zjw
 * @date 12-10-16
 */
@Service(url = "/ods/bak/dept")
public class OdDeptBakCrud extends DeptOwnedNormalCrud<OdDeptBak, Integer>
{
    @Inject
    private OdDeptBakService service;

    @NotSerialized
    private ReceiveTypeTreeModel receiveTypeTree;

    @Lower(column = "createTime")
    private Date time_start;

    @Upper(column = "createTime")
    private Date time_end;

    private Integer receiveTypeId;

    private OdDeptBakType type;

    public OdDeptBakCrud()
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

    public OdDeptBakType getType()
    {
        return type;
    }

    public void setType(OdDeptBakType type)
    {
        this.type = type;
    }

    public Integer getReceiveTypeId()
    {
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    @Override
    public void initEntity(OdDeptBak entity) throws Exception
    {
        super.initEntity(entity);

        entity.setType(OdDeptBakType.SEND);
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

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = authDeptIds == null || authDeptIds.size() > 1;

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId") :
                new PageTableView(true);

        view.addComponent("创建时间", "time_start", "time_end");

        view.addColumn("名称", "backName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("创建人", "createUser.userName").setWidth("100");
        view.addColumn("文件类型", "type").setWidth("60");
        view.addColumn("发文类型", "sendNumber.sendNumberName");
        view.addColumn("收文类别", "receiveType.receiveTypeName");
        view.addColumn("开始时间", "startTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("下载", new ConditionComponent().add("path!=null",
                new CHref("下载", "/ods/bak/dept/${backId}/down").setTarget("_blank"))).setWidth("60");

        view.defaultInit(false);

        view.importJs("/ods/bak/dept.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("名称", "backName");
        view.addComponent("文件类型", "type").setProperty("onchange", "typeChange()");
        view.addComponent("发文类型", "sendNumberId");
        view.addComponent("收文类别", "receiveTypeId").setProperty("text",
                getEntity().getReceiveType() != null ? getEntity().getReceiveType().getReceiveTypeName() : "");
        view.addComponent("开始时间", "startTime");
        view.addComponent("结束时间", "endTime");

        view.addDefaultButtons();

        view.importJs("/ods/bak/dept.js");
        view.importCss("/ods/bak/dept.css");
        return view;
    }

    @NotSerialized
    @Select(field = {"sendNumberId", "entity.sendNumberId"})
    public List<SendNumber> getAllSendNumbers() throws Exception
    {
        return service.getDao().getSendNumbers(this.getDeptId());
    }

    @NotSerialized
    @Select(field = {"receiveTypeId", "entity.receiveTypeId"})
    public ReceiveTypeTreeModel getAllReceiveTypes() throws Exception
    {
        if (receiveTypeTree == null)
        {
            receiveTypeTree = new ReceiveTypeTreeModel();
            if (getEntity() != null && getEntity().getDeptId() != null)
                receiveTypeTree.setDeptId(getEntity().getDeptId());
            else
                receiveTypeTree.setDeptId(getDeptId());
        }
        return receiveTypeTree;
    }

    /**
     * 下载文件
     *
     * @return 对应备份文件
     * @throws Exception 数据操作异常
     */
    @Service(url = "/ods/bak/dept/{$0}/down")
    public InputFile download(Integer backId) throws Exception
    {
        OdDeptBak odDeptBak = service.getDao().getOdDeptBak(backId);
        String path = odDeptBak.getPath();
        if (!StringUtils.isEmpty(path))
        {
            CommonFile file = service.getCommonFileService().getFile(path);
            if (file != null && file.exists())
                return new InputFile(file.getInputable(), odDeptBak.getBackName() + ".zip");
        }

        return null;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }
}
