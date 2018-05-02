package com.gzzm.portal.solicit;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.commons.StationOwnedCrud;
import net.cyan.arachne.annotation.Service;

/**
 * @author lk
 * @date 13-10-17
 */
@Service(url = "/portal/solicit/SolicitTypeCrud")
public class SolicitTypeCrud extends StationOwnedCrud<SolicitType, Integer>
{
	public SolicitTypeCrud()
	{
		setLog(true);
	}

	@Override
	public String getOrderField()
	{
		return "orderId";
	}

	@Override
	protected Object createListView() throws Exception
	{
		PageTableView view = new PageTableView();

		view.addColumn("类型名称", "typeName");
		view.addColumn("所属网站", "station");
		view.addComponent("站点", "stationId");
		view.defaultInit();

		return view;
	}

	@Override
	protected Object createShowView() throws Exception
	{
		SimpleDialogView view = new SimpleDialogView();

		view.addComponent("类型名称", "typeName");
		com.gzzm.portal.cms.station.Station station = getEntity().getStation();
		view.addComponent("所属网站", "stationId").setProperty("text", station == null ? "" : station.getStationName());
		view.addDefaultButtons();

		return view;
	}
}
