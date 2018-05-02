package com.gzzm.ods.exchange.back;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 退文回执定义
 *
 * @author camel
 * @date 2018/1/29
 */
@Entity(table = "ODBACKPAPER", keys = "paperId")
public class BackPaper
{
    /**
     * 退文回执Id
     */
    @Generatable(length = 9)
    private Integer paperId;

    /**
     * 退文回执名称
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String paperName;

    /**
     * 退文回执页面路径
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private String page;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 退文回执打印模板
     */
    private InputStream template;

    /**
     * 更新时间
     */
    private Date lastModified;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public BackPaper()
    {
    }

    public Integer getPaperId()
    {
        return paperId;
    }

    public void setPaperId(Integer paperId)
    {
        this.paperId = paperId;
    }

    public String getPaperName()
    {
        return paperName;
    }

    public void setPaperName(String paperName)
    {
        this.paperName = paperName;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public InputStream getTemplate()
    {
        return template;
    }

    public void setTemplate(InputStream template)
    {
        this.template = template;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModified()
    {
        setLastModified(new Date());
    }

    @Override
    public String toString()
    {
        return paperName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof BackPaper))
            return false;

        BackPaper backPaper = (BackPaper) o;

        return paperId.equals(backPaper.paperId);
    }

    @Override
    public int hashCode()
    {
        return paperId.hashCode();
    }
}
