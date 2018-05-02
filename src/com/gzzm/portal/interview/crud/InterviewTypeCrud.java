package com.gzzm.portal.interview.crud;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.portal.cms.station.Station;
import com.gzzm.portal.commons.StationOwnedCrud;
import com.gzzm.portal.interview.entity.InterviewType;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;


/**
 * @author lk
 * @date 13-9-29
 */
@Service(url = "/portal/interview/InterviewTypeCrud")
public class InterviewTypeCrud extends StationOwnedCrud<InterviewType, Integer>
{
	public InterviewTypeCrud()
	{
		setLog(true);
	}

	//根据类别名称查询
	@Like
	private String typeName;

	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
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
		view.addColumn("说明", "remark");
		view.addColumn("所属网站", "station");

		view.addComponent("类型名称", "typeName");

		view.defaultInit();
		view.addButton(Buttons.sort());

		return view;
	}

	@Override
	protected Object createShowView() throws Exception
	{
		SimpleDialogView view = new SimpleDialogView();

		view.addComponent("类型名称", "typeName");
		Station station = getEntity().getStation();
		view.addComponent("所属网站", "stationId").setProperty("text", station == null ? "" : station.getStationName());
		view.addComponent("说明", "remark");

		view.addDefaultButtons();

		return view;
	}
}
