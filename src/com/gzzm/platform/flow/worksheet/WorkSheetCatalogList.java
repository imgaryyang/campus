package com.gzzm.platform.flow.worksheet;

import net.cyan.thunwind.annotation.*;

/**
 * 工作表目录列表，保存一个工作表目录中存放的文件
 *
 * @author camel
 * @date 11-10-26
 */
@Entity(table = "PFWORKSHEETCATALOGLIST", keys = {"userId", "instanceId"})
public class WorkSheetCatalogList
{
    /**
     * 用户ID
     */
    @ColumnDescription(type = "number(9)")
    private Integer userId;

    /**
     * 流程实例ID，表示将此文件收藏到catalogId对应的目录中
     */
    @ColumnDescription(type = "number(12)")
    private Long instanceId;

    /**
     * 目录ID
     */
    @ColumnDescription(type = "number(9)")
    private Integer catalogId;

    public WorkSheetCatalogList()
    {
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof WorkSheetCatalogList))
            return false;

        WorkSheetCatalogList that = (WorkSheetCatalogList) o;

        return userId.equals(that.userId) && instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode()
    {
        int result = userId.hashCode();
        result = 31 * result + instanceId.hashCode();
        return result;
    }
}
