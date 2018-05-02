package com.gzzm.portal.datacollection;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

/**
 * 数据采集类型
 * @author ldp
 * @date 2018/4/25
 */
@Entity(table = "PLDATATYPE", keys = "typeId")
public class DataType {

    @Generatable(length = 7)
    private Integer typeId;

    /**
     * 报表名称
     */
    @Require
    @ColumnDescription(type = "varchar(500)")
    private String name;

    /**
     * 数据采集时间精度
     */
    @Require
    private TimeType timeType;


    /**
     * 采集数据单位
     */
    @Require
    @ColumnDescription(type = "varchar(100)")
    private String unit;

    /**
     * 数据来源
     */
    @ColumnDescription(type = "varchar(500)")
    private String source;

    public DataType() {
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataType)) return false;

        DataType dataType = (DataType) o;

        return typeId != null ? typeId.equals(dataType.typeId) : dataType.typeId == null;
    }

    @Override
    public int hashCode() {
        return typeId != null ? typeId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
