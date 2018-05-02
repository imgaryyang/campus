package com.gzzm.platform.flow.worksheet;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 工作表目录
 *
 * @author camel
 * @date 11-10-26
 */
@Entity(table = "PFWORKSHEETCATALOG", keys = "catalogId")
public class WorkSheetCatalog
{
    /**
     * 目录ID，主键
     */
    @Generatable(length = 9)
    private Integer catalogId;

    /**
     * 目录名
     */
    @ColumnDescription(type = "varchar(250)")
    @Unique(with = {"parentCatalogId", "userId"})
    @Require
    private String catalogName;

    private Integer parentCatalogId;

    @NotSerialized
    @ToOne("PARENTCATALOGID")
    private WorkSheetCatalog parentCatalog;

    @NotSerialized
    @OneToMany("PARENTCATALOGID")
    @OrderBy(column = "ORDERID")
    private List<WorkSheetCatalog> children;

    /**
     * 拥有此目录的用户
     */
    private Integer userId;

    private User user;

    /**
     * 类型，表明归档目录所用的用途，如od表示公文，ga表示审批等
     */
    @ColumnDescription(type = "varchar(10)")
    private String type;

    /**
     * 排序id，目录在同一级目录中的顺序，越小排越前面
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public WorkSheetCatalog()
    {
    }

    public WorkSheetCatalog(Integer catalogId, String catalogName)
    {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
    }

    public Integer getParentCatalogId()
    {
        return parentCatalogId;
    }

    public void setParentCatalogId(Integer parentCatalogId)
    {
        this.parentCatalogId = parentCatalogId;
    }

    public WorkSheetCatalog getParentCatalog()
    {
        return parentCatalog;
    }

    public void setParentCatalog(WorkSheetCatalog parentCatalog)
    {
        this.parentCatalog = parentCatalog;
    }

    public List<WorkSheetCatalog> getChildren()
    {
        return children;
    }

    public void setChildren(List<WorkSheetCatalog> children)
    {
        this.children = children;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof WorkSheetCatalog))
            return false;

        WorkSheetCatalog that = (WorkSheetCatalog) o;

        return catalogId.equals(that.catalogId);
    }

    @Override
    public int hashCode()
    {
        return catalogId.hashCode();
    }

    @Override
    public String toString()
    {
        return catalogName;
    }
}
