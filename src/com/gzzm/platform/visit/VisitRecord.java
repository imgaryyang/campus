package com.gzzm.platform.visit;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 访问记录，记录某个对象的访问记录
 * <p/>
 * type,objectId,visitTime,visitCount为一个联合索引
 *
 * @author camel
 * @date 2011-6-21
 */
@Entity(table = "PFVISITRECORD", keys = "visitId")
@Indexes({@Index(columns = {"TYPE", "OBJECTID", "VISITTIME", "VISITCOUNT"})})
public class VisitRecord
{
    /**
     * 访问ID，主键，用uuid，不会重复
     */
    @Generatable(name = "uuid", length = 32)
    private String visitId;

    /**
     * 访问对象的类型，为一个整数，由各功能模块定义，由于此表数据量大，不允许用字符串，
     * 必须用整数,必须保证各功能模块所使用的数字不会冲突
     */
    @ColumnDescription(type = "number(3)", nullable = false)
    private Integer type;

    /**
     * 访问的对象的ID，即相关表的主键，要求必须是整数
     */
    private Integer objectId;

    /**
     * 访问时间
     */
    private Date visitTime;

    /**
     * 访问次数，默认为1
     */
    @ColumnDescription(type = "number(7)", nullable = false, defaultValue = "1")
    private Integer visitCount;

    /**
     * 访问的用户ID，可以是外网用户，也可以使内网用户，可以为空
     */
    @ColumnDescription(type = "number(9)")
    private Integer userId;

    /**
     * 访问的ip
     */
    @ColumnDescription(type = "varchar(250)")
    private String ip;

    public VisitRecord()
    {
    }

    public String getVisitId()
    {
        return visitId;
    }

    public void setVisitId(String visitId)
    {
        this.visitId = visitId;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Integer objectId)
    {
        this.objectId = objectId;
    }

    public Date getVisitTime()
    {
        return visitTime;
    }

    public void setVisitTime(Date visitTime)
    {
        this.visitTime = visitTime;
    }

    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof VisitRecord))
            return false;

        VisitRecord that = (VisitRecord) o;

        return visitId.equals(that.visitId);
    }

    @Override
    public int hashCode()
    {
        return visitId.hashCode();
    }
}
