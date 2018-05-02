package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.ComputeColumn;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

import java.util.List;

/**
 * 课程类目
 * @author yiuman
 * @date 2018/3/13
 */
@Entity(table = "SCTUTORSUBJECTTYPE", keys = "typeId")
public class TutorSubjectType {

    /**
     * 类目ID
     */
    @Generatable(length = 6)
    private Integer typeId;

    /**
     * 类目名称
     */
    @ColumnDescription(type = "varchar(25)")
    private String typeName;

    /**
     * 类目编码
     */
    @ColumnDescription(type = "varchar(25)")
    private String typeNo;

    @NotSerialized
    @ComputeColumn("select i from TutorSubjectTypeItem i where i.typeId=this.typeId ")
    private List<TutorSubjectTypeItem> itemList;

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public List<TutorSubjectTypeItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TutorSubjectTypeItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TutorSubjectType)) return false;

        TutorSubjectType that = (TutorSubjectType) o;

        return typeId != null ? typeId.equals(that.typeId) : that.typeId == null;
    }

    @Override
    public int hashCode() {
        return typeId != null ? typeId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
