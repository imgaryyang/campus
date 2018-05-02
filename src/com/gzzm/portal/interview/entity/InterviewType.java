package com.gzzm.portal.interview.entity;

import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

/**
 * @author lk
 * @date 13-9-29
 */
@Entity(table = "PLINTERVIEWTYPE", keys = "typeId")
public class InterviewType
{
	public InterviewType()
	{
	}

	@Generatable(length = 6)
	private Integer typeId;

	@Unique(with = "stationid")
	@Require
	@ColumnDescription(type = "varchar(50)", nullable = false)
	private String typeName;

	private char[] remark;

	@Require
	@ColumnDescription(type = "number(8)", nullable = false)
	private Integer stationId;

	@NotSerialized
	private Station station;

	@ColumnDescription(type = "number(9)")
	private Integer orderId;

	public Integer getOrderId()
	{
		return orderId;
	}

	public void setOrderId(Integer orderId)
	{
		this.orderId = orderId;
	}

	public Station getStation()
	{
		return station;
	}

	public void setStation(Station station)
	{
		this.station = station;
	}

	public Integer getStationId()
	{
		return stationId;
	}

	public void setStationId(Integer stationId)
	{
		this.stationId = stationId;
	}

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

	public char[] getRemark()
	{
		return remark;
	}

	public void setRemark(char[] remark)
	{
		this.remark = remark;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		InterviewType that = (InterviewType) o;

		if (!typeId.equals(that.typeId))
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return typeId.hashCode();
	}

	@Override
	public String toString()
	{
		return typeName;
	}
}
