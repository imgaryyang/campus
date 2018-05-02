package com.gzzm.ods.archive;

import com.gzzm.platform.commons.crud.DeptOwnedEditableCrud;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;

/**
 * @author camel
 * @date 2016/6/12
 */
@Service
public abstract class ArchiveBasePage<T> extends DeptOwnedEditableCrud<T, Long>
{
    @Inject
    protected ArchiveService service;

    @Like("document.title")
    private String title;

    @Like("document.sendNumber")
    private String sendNumber;

    @Contains("document.textContent")
    private String text;

    private Integer year;

    private String author;

    private String remark;

    @Require
    private String catalogName;

    private Integer catalogId;

    public ArchiveBasePage()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getCatalogName()
    {
        return catalogName;
    }

    public void setCatalogName(String catalogName)
    {
        this.catalogName = catalogName;
    }


    @NotSerialized
    public Date getStartTime()
    {
        return DateUtils.toSQLDate(DateUtils.getYearStart(year));
    }

    @NotSerialized
    public Date getEndTime()
    {
        return DateUtils.toSQLDate(DateUtils.addDate(DateUtils.getYearEnd(year), -1));
    }

    @NotSerialized
    @Select(field = "catalogName")
    public List<String> getCatalogNames() throws Exception
    {
        return service.getDao().getCatalogNames(getYear(), getDeptId());
    }

    @Select(field = {"timeLimit", "entity.timeLimit"})
    @NotSerialized
    @NotCondition
    public List<String> getTimeLimits() throws Exception
    {
        return service.getDao().getTimeLimits();
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    protected Integer getCatalogId() throws Exception
    {
        if (catalogId == null)
        {
            if (catalogName != null)
            {
                ArchiveCatalog catalog = service.getDao().getCatalogByName(getYear(), getDeptId(), getCatalogName());
                if (catalog == null)
                {
                    catalog = new ArchiveCatalog();
                    catalog.setCatalogName(catalogName);
                    catalog.setYear(getYear());
                    catalog.setDeptId(getDeptId());
                    catalog.setOrderId(CrudUtils.getOrderValue(6, false));
                    service.getDao().add(catalog);
                }

                catalogId = catalog.getCatalogId();
            }
        }

        return catalogId;
    }
}