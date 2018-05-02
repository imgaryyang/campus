package com.gzzm.portal.cms.information.search;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.*;

/**
 * @author sjy
 * @date 2018/3/8
 */
@Service(url = "/portal/info/hotWord")
public class HotWordCrud extends BaseNormalCrud<InfoHotWord, Integer>
{
    @Inject
    private SearchRecordDao dao;

    private Integer stationId;

    private HotWordSource source;

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(Tools.getBean(StationDisplay.class), "stationId");
        view.addComponent("来源","source");
        view.addColumn("热词", "keyword");
        view.addColumn("来源", "source");
        view.addDefaultButtons();
        view.makeEditable();
        view.addButton(Buttons.sort());
        view.importJs("/portal/cms/hotword/hotword.js");
        return view;
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
        view.addComponent("热词", "keyword", true);
        view.addDefaultButtons();
        return view;
    }

    @Override
    protected String getSortListCondition() throws Exception
    {
        return "stationId=:stationId";
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        Integer orderId = dao.queryMaxOrderId(stationId);
        if (orderId == null)
        {
            orderId = 0;
        }
        InfoHotWord entity = getEntity();
        entity.setOrderId(++orderId);
        entity.setStationId(stationId);
        entity.setSource(HotWordSource.EDIT);
        return super.beforeSave();
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public HotWordSource getSource()
    {
        return source;
    }

    public void setSource(HotWordSource source)
    {
        this.source = source;
    }
}
