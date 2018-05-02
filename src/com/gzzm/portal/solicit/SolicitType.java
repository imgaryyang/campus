package com.gzzm.portal.solicit;

import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * @author lk
 * @date 13-10-17
 */
@Entity(table = "PLSOLICITTYPE", keys = "typeId")
public class SolicitType
{
	public SolicitType()
	{
	}

	@Generatable(length = 6)
	private Integer typeId;

	@Unique(with = "stationId")
	@Require
	@ColumnDescription(type = "varchar(100)", nullable = false)
	private String typeName;

	@Require
	@ColumnDescription(type = "number(8)", nullable = false)
	private Integer stationId;

	@NotSerialized
	private Station station;

	@ColumnDescription(type = "number(6)", nullable = false)
	private Integer orderId;

	public Integer getTypeId()
	{
		return typeId;
	}

	public void setTypeId(Integer typeId)
	{
		this.typeId = typeId;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public Integer getStationId()
	{
		return stationId;
	}

	public void setStationId(Integer stationId)
	{
		this.stationId = stationId;
	}

	public Station getStation()
	{
		return station;
	}

	public void setStation(Station station)
	{
		this.station = station;
	}

	public Integer getOrderId()
	{
		return orderId;
	}

	public void setOrderId(Integer orderId)
	{
		this.orderId = orderId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SolicitType)) return false;

		SolicitType type = (SolicitType) o;

		return typeId.equals(type.typeId);

	}

	@Override
	public int hashCode() {
		return typeId.hashCode();
	}

	@Override
	public String toString()
	{
		return typeName;
	}
}
