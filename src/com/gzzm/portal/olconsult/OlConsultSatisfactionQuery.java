package com.gzzm.portal.olconsult;

import com.gzzm.platform.commons.crud.BaseStatCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.Lower;
import net.cyan.crud.annotation.Upper;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wym
 * Date: 13-6-5
 * Time: 下午4:48
 * 满意度统计相关类
 */
@Service(url="/portal/olconsult/statistics")
public abstract class OlConsultSatisfactionQuery extends BaseStatCrud<OlConsult> {

    @Inject
    private OlConsultDao dao;

    @Inject
    private OlConsultSatisfactionDao olConsultSatisfactionDao;

    private Integer typeId;

    @Like
    private String seatName;

    private Integer seatId;

    @Lower(column = "endTime")
    private Date time_start;

    @Upper(column = "endTime")
    private Date time_end;

    public OlConsultSatisfactionQuery()
    {
        setLoadTotal(true);
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Date getTime_start() {
        return time_start;
    }

    public void setTime_start(Date time_start) {
        this.time_start = time_start;
    }

    public Date getTime_end() {
        return time_end;
    }

    public void setTime_end(Date time_end) {
        this.time_end = time_end;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    @Override
    protected void initStats() throws Exception {
        //join(OlConsult.class,"olconsult","olconsult.seatId is not null");
        setGroupField("seatId");
        addOrderBy("seatId");
        addStat("seatName", "min(seat.seatName)","'合计'");

        addStat("total", "count(olConsultSatisfaction is not null)");
        List<OlConsultSatisfaction> olConsultSatisfactionList = olConsultSatisfactionDao.getAllSatisfaction();
        for (OlConsultSatisfaction olConsultSatisfaction : olConsultSatisfactionList)
        {
            addStat("count_" + olConsultSatisfaction.getSatisfactionId(), "count(satisfactionId=" +olConsultSatisfaction.getSatisfactionId() + ")");
        }
    }

    @Select(field = "seatId")
    @NotSerialized
    public List<OlConsultSeat> getSeats() throws Exception
    {
        return dao.getSeats(typeId);
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView(false);

        view.addComponent("客服名称", "seatId");
        view.addComponent("时间", "time_start", "time_end");

        view.addColumn("客服名称", "seatName");

        List<OlConsultSatisfaction> olConsultSatisfactionList = olConsultSatisfactionDao.getAllSatisfaction();
        for (OlConsultSatisfaction olConsultSatisfaction : olConsultSatisfactionList)
        {
            view.addColumn(olConsultSatisfaction.getSatisfaction(), "count_" + olConsultSatisfaction.getSatisfactionId());
        }


        view.addColumn("合计", "total");

        view.defaultInit();
        view.addButton(Buttons.export("xls"));

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception {
        return new ExportParameters("客服满意度统计");
    }
}