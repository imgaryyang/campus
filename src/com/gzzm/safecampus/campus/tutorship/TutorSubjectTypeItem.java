package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 课外辅导--课程分类
 * @author yiuman
 * @date 2018/3/12
 */
@Entity(table = "SCTYPEITEM",keys = "typeItemId")
public class TutorSubjectTypeItem {

    /**
     * 分类ID
     */
    @Generatable(length = 6)
    private Integer typeItemId;

    /**
     * 分类名称
     */
    @ColumnDescription(type = "varchar(100)")
    private String typeItemName;

    /**
     * 分类编码
     */
    @ColumnDescription(type = "varchar(20)")
    private String typeItemNo;

    /**
     * 关联类目
     */
    private Integer typeId;

    @NotSerialized
     @ToOne("typeId")
    private TutorSubjectType subjectType;


    public Integer getTypeItemId() {
        return typeItemId;
    }

    public void setTypeItemId(Integer typeItemId) {
        this.typeItemId = typeItemId;
    }

    public String getTypeItemName() {
        return typeItemName;
    }

    public void setTypeItemName(String typeItemName) {
        this.typeItemName = typeItemName;
    }

    public String getTypeItemNo() {
        return typeItemNo;
    }

    public void setTypeItemNo(String typeItemNo) {
        this.typeItemNo = typeItemNo;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public TutorSubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(TutorSubjectType subjectType) {
        this.subjectType = subjectType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TutorSubjectTypeItem)) return false;

        TutorSubjectTypeItem that = (TutorSubjectTypeItem) o;

        return typeItemId != null ? typeItemId.equals(that.typeItemId) : that.typeItemId == null;
    }

    @Override
    public int hashCode() {
        return typeItemId != null ? typeItemId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return typeItemName;
    }
}
