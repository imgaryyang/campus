package com.gzzm.portal.eval;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;

/**
 * 评价类型管理
 *
 * @author sjy
 * @date 2018/2/23
 */
@Service(url = "/portal/eval/type")
public class EvalTpeCrud extends BaseNormalCrud<EvalType, Integer>
{
    private Integer stationId;

    @Like
    private String typeName;

    private TargetType type;

    private EvalCatalog catalog;

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(Tools.getBean(StationDisplay.class), "stationId");
        view.addComponent("类型名称", "typeName");
        view.addComponent("分类", "catalog");
        view.addComponent("类型", "type");
        view.addColumn("类型名称", "typeName");
        view.addColumn("类型", "type");
        view.addColumn("分类", "catalog");
        view.addDefaultButtons();
        view.makeEditable();
        view.importJs("/portal/eval/eval_type.js");
        return view;
    }

    @Override
    public void initEntity(EvalType entity) throws Exception
    {
        super.initEntity(entity);
        entity.setStationId(stationId);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("类型名称", "typeName",true);
        view.addComponent("类型", "type");
        view.addComponent("分类", "catalog",true);
        view.addDefaultButtons();
        return view;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public TargetType getType()
    {
        return type;
    }

    public void setType(TargetType type)
    {
        this.type = type;
    }

    public EvalCatalog getCatalog()
    {
        return catalog;
    }

    public void setCatalog(EvalCatalog catalog)
    {
        this.catalog = catalog;
    }
}
