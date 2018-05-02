package com.gzzm.ods.archive;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.view.components.*;

import java.util.Date;

/**
 * @author camel
 * @date 2016/8/27
 */
@Service
public abstract class ToArchivePage<T> extends ArchiveBasePage<T>
{
    private int serial = -1;

    private Integer pagesCount;

    @Require
    private String timeLimit;

    public ToArchivePage()
    {
    }

    public String getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit)
    {
        this.timeLimit = timeLimit;
    }

    public Integer getPagesCount()
    {
        return pagesCount;
    }

    public void setPagesCount(Integer pagesCount)
    {
        this.pagesCount = pagesCount;
    }

    protected abstract OfficeDocument getDocument(T entity) throws Exception;

    protected abstract Long getInstanceId(T entity) throws Exception;

    protected abstract Date getDocTime(T entity) throws Exception;

    @Service
    @Forward(page = Pages.QUERY)
    public String showCatalog() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("案卷目录", new CCombox("catalogName").setEditable(true).setNullable(true));
        view.addComponent("保管期限", "timeLimit");
        view.addComponent("页数", "pagesCount");
        view.addComponent("备注", new CTextArea("remark"));
        view.setTitle("文件归档");

        setView(view);

        view.addButton(Buttons.ok());
        view.addButton(Buttons.cancel());

        return null;
    }

    protected void catalog(OfficeDocument document, Long instanceId, Date docTime) throws Exception
    {
        if (serial == -1)
            serial = service.getDao().getMaxSerial(getCatalogId()) + 1;
        else
            serial++;

        Archive archive = new Archive();

        archive.setYear(getYear());
        archive.setDeptId(getDeptId());
        archive.setCatalogId(getCatalogId());
        archive.setDocumentId(document.getDocumentId());
        archive.setInstanceId(instanceId);
        archive.setDocTime(docTime);
        archive.setSerial(serial);
        archive.setTitle(document.getTitle());
        archive.setSendNumber(document.getSendNumber());
        archive.setAuthor(document.getSourceDept());
        archive.setRemark(getRemark());
        archive.setUserId(userOnlineInfo.getUserId());
        archive.setTimeLimit(getTimeLimit());
        archive.setPagesCount(pagesCount);
        archive.setArchiveTime(new Date());

        service.getDao().add(archive);
    }

    protected void lock() throws Exception
    {
        service.lockCatalog(getCatalogId());
    }

    protected void catalog(T entity) throws Exception
    {
        catalog(getDocument(entity), getInstanceId(entity), getDocTime(entity));
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void catalogSelected() throws Exception
    {
        lock();

        for (Long key : getKeys())
        {
            T entity = getEntity(key);
            catalog(entity);
        }
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void catalogQueryResult() throws Exception
    {
        lock();

        setPageSize(-1);
        loadList();

        for (T entity : getList())
        {
            catalog(entity);
        }
    }
}
