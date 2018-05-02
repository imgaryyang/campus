package com.gzzm.mo;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 移动客户端管理
 *
 * @author camel
 * @date 2014/5/13
 */
@Service(url = "/mo/client")
public class MoClientCrud extends BaseNormalCrud<MoClient, Integer> {
    @Inject
    private MoDao dao;

    private Integer typeId;

    @Like
    private String clientName;

    private InputFile file;

    private boolean query;

    public MoClientCrud() {
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public InputFile getFile() {
        return file;
    }

    public void setFile(InputFile file) {
        this.file = file;
    }

    public boolean isQuery() {
        return query;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    @NotSerialized
    @Select(field = {"typeId", "entity.typeId"})
    public List<MoClientType> getTypes() throws Exception {
        return dao.getTypes();
    }

    @Override
    protected String getComplexCondition() throws Exception {
        if (query)
            return "clientId in (select max(clientId) from MoClient group by typeId)";
        else
            return null;
    }

    @Override
    protected void beforeQuery() throws Exception {
        if (isQuery()) {
            addOrderBy("type.orderId");
        } else {
            addOrderBy("publishTime", OrderType.desc);
        }
    }

    @Override
    public boolean beforeSave() throws Exception {
        super.beforeSave();

        if (file != null) {
            getEntity().setClientFile(file.getInputStream());
            getEntity().setFileSize(file.size());
            getEntity().setFileName(file.getName());
        }

        getEntity().setPublishTime(new Date());

        return true;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("名称", "clientName");
        view.addComponent("类型", "typeId");
        view.addComponent("版本号", "versionNo");
        view.addComponent("强制更新", "forceUpdate").setProperty("require","");
        view.addComponent("文件", new CFile("this.file"));
        view.addComponent("说明", "remark");
        view.addDefaultButtons();

        return view;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();

        view.addComponent("客户端名称", "clientName");

        if (!isQuery()) {
            view.addComponent("类型", "typeId");
        }

        view.addColumn("客户端名称", "clientName");
        view.addColumn("类型", "type.typeName").setOrderFiled("type.orderId").setWidth("200");
        view.addColumn("版本号", "versionNo").setWidth("150");
        view.addColumn("发布时间", "publishTime");
        view.addColumn("下载", new CHref("下载", "/mo/client/${clientId}/down"));

        if (isQuery()) {
            view.addButton(Buttons.query());
        } else {
            view.defaultInit();
        }

        return view;
    }

    @Service(url = "/mo/client/{$0}/down")
    public InputFile down(Integer clienId) throws Exception {
        MoClient client = getEntity(clienId);

        return new InputFile(client.getClientFile(), client.getFileName());
    }
}
