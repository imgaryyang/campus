package com.gzzm.safecampus.campus.siesta;

import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.classes.Teacher;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.validate.annotation.FieldValidator;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.commons.validate.annotation.Warning;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

/**
 * 午休室表
 * Created by Huangmincong on 2018/3/12.
 */
@Entity(table = "SCSIESTAROOM", keys = "sroomId")
public class SiestaRoom extends BaseBean{

    @Inject
    private static Provider<SiestaDao> daoProvider;

    @Generatable(length = 6)
    private Integer sroomId;

    /**
     * 午休室名称
     */
    @Require
    @ColumnDescription(type = "VARCHAR2(30)")
    private String name;

    /**
     * 生活老师ID
     */
    @Require
    private Integer teacherId;

    @NotSerialized
    @ToOne
    private Teacher teacher;

    /**
     * 床位数
     */
    @Require
    private Integer bedNum;

    /**
     * 备注
     */
    @ColumnDescription(type = "VARCHAR2(100)")
    private String remark;

    /**
     * 删除标识
     */
    @ColumnDescription(type = "NUMBER(1)")
    private Integer deleteTag;

    public SiestaRoom() {
    }

    public SiestaRoom(Integer sroomId, String name) {
        this.sroomId = sroomId;
        this.name = name;
    }

    public Integer getSroomId() {
        return sroomId;
    }

    public void setSroomId(Integer sroomId) {
        this.sroomId = sroomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Integer getBedNum() {
        return bedNum;
    }

    public void setBedNum(Integer bedNum) {
        this.bedNum = bedNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiestaRoom that = (SiestaRoom) o;

        return sroomId.equals(that.sroomId);

    }

    @Override
    public int hashCode() {
        return sroomId.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @FieldValidator("name")
    @Warning("siestaroom.name_exists")
    public SiestaRoom checkName() throws Exception
    {
        if (getSroomId()!=null){
            SiestaRoom siestaRoom= daoProvider.get().getByRoomId(getSroomId());
            if (siestaRoom.getName().equals(getName())){
                return null;
            }
        }
        return !StringUtils.isEmpty(getName()) ?
                daoProvider.get().getRoomByDeptId(getName(),getDeptId()) : null;
    }
}
