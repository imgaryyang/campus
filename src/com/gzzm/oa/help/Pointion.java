package com.gzzm.oa.help;


import net.cyan.commons.util.InputFile;
import net.cyan.commons.validate.annotation.MaxLen;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

import java.util.Date;

/**
 * 提示信息
 * @author ljb
 * @date 2017/3/31 0031
 */
@Entity(table = "OAPOINTION",keys = "pointId")
public class Pointion   {

    @Generatable(length = 9)
    private Integer pointId;

    /**
     * 标题
     */
    @ColumnDescription(type="varchar(200)")
    private String title;

    /**
     * 提示类型(预留字段：以防增加提示位置)
     */
    @ColumnDescription(type="number(2)",defaultValue = "0")
    private String type;

    /**
     * 提示内容
     */
    @ColumnDescription(type = "Clob")
    @MaxLen(value = 400,message="信息不能超过400字")
    private String pointContent;

    /**
     * 提示有效时间(必填)
     */
    @ColumnDescription(type = "Date")
    @Require
    private Date validTime;

    @ColumnDescription(type="number(1)",defaultValue = "0")
    private boolean publicTag;


    public Pointion() {
    }

    public boolean isPublicTag() {
        return publicTag;
    }

    public void setPublicTag(boolean publicTag) {
        this.publicTag = publicTag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public String getPointContent() {
        return pointContent;
    }

    public void setPointContent(String pointContent) {
        this.pointContent = pointContent;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
