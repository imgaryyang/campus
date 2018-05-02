package com.gzzm.portal.datacollection;

import net.cyan.commons.validate.annotation.MaxVal;
import net.cyan.commons.validate.annotation.Pattern;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 数据列表
 * @author ldp
 * @date 2018/4/25
 */
@Entity(table = "PLDATALIST", keys = "listId")
public class DataList {

    @Generatable(name = "uuid", length = 32)
    private String listId;

    /**
     * 数据类型
     */
    private Integer typeId;

    @ToOne
    private DataType type;

    /**
     * 数据采集时间（实际展示的字符串）
     */
    @ColumnDescription(type = "varchar(200)")
    private String collectTime;

    /**
     * json字符串
     */
    @ColumnDescription(type = "varchar(200)")
    private String timeVal;

    /**
     * 数据具体的数值
     */
    @Require
    @MaxVal("99999999.99")
    @Pattern(value = "^\\d+(\\.\\d{1,2})?$", message = "只能输入两位小数")
    @ColumnDescription(type = "number(10,2)")
    private Double dataVal;

    public DataList() {
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public Double getDataVal() {
        return dataVal;
    }

    public void setDataVal(Double dataVal) {
        this.dataVal = dataVal;
    }

    public String getTimeVal() {
        return timeVal;
    }

    public void setTimeVal(String timeVal) {
        this.timeVal = timeVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataList)) return false;

        DataList dataList = (DataList) o;

        return listId != null ? listId.equals(dataList.listId) : dataList.listId == null;
    }

    @Override
    public int hashCode() {
        return listId != null ? listId.hashCode() : 0;
    }
}
