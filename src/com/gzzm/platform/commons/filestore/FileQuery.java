package com.gzzm.platform.commons.filestore;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author camel
 * @date 13-12-3
 */
@Service(url = "/filestore/query")
public class FileQuery extends BaseQLQueryCrud<FileItem> implements KeyBaseCrud<FileItem, String>
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private FileStoreService service;

    private FileItemTransformer transformer;

    private QueryType queryType;

    private Class beanType;

    private String queryString;

    @NotSerialized
    private Collection<Integer> authDeptIds;

    private String catalogId;

    @NotSerialized
    private String type;

    @NotSerialized
    private String id;

    private Integer deptId;

    @Like
    private String title;

    private String text;

    @Lower
    private Date time_start;

    @Upper
    private Date time_end;

    public FileQuery()
    {
    }

    @Override
    protected String getQueryString() throws Exception
    {
        return queryString;
    }

    @Override
    protected QueryType getQueryType()
    {
        return queryType;
    }

    protected Class getBeanType()
    {
        return beanType;
    }

    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }

    public void setQueryType(QueryType queryType)
    {
        this.queryType = queryType;
    }

    public void setBeanType(Class beanType)
    {
        this.beanType = beanType;
    }

    public String getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(String catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getType()
    {
        return type;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    public void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
    }

    public void setTransformer(FileItemTransformer transformer)
    {
        this.transformer = transformer;
    }

    @Override
    public void setList(List<FileItem> fileItems)
    {
        super.setList(fileItems);
    }

    @Override
    public void setTotalCount(int totalCount)
    {
        super.setTotalCount(totalCount);
    }

    @Override
    protected void loadList() throws Exception
    {
        if (catalogId != null)
        {
            int index = catalogId.indexOf(':');
            if (index > 0)
            {
                type = catalogId.substring(0, index);
                id = catalogId.substring(index + 1);
            }
            else
            {
                type = catalogId;
                id = "";
            }

            FileStorer storer = service.getStorer(type);
            if (storer == null)
            {
                setList(Collections.<FileItem>emptyList());
            }
            else
            {
                storer.loadFileList(this);
            }
        }
        else
        {
            setList(Collections.<FileItem>emptyList());
        }
    }

    public void loadList0() throws Exception
    {
        super.loadList();
    }

    @Override
    protected FileItem transformBean(Object bean) throws Exception
    {
        if (transformer != null)
            return transformer.transform(bean);
        else
            return (FileItem) bean;
    }

    public Class<String> getKeyType()
    {
        return String.class;
    }

    public String getKey(FileItem entity) throws Exception
    {
        return type + ":" + entity.getId();
    }

    @Override
    protected Object createView(String action) throws Exception
    {
        ComplexTableView view = new ComplexTableView(Tools.getBean(FileCatalogDisplay.class), "catalogId", true);

        view.setTitle("资料中心");

        view.addComponent("文件名称", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("时间", "time_start", "time_end");

        view.addColumn("文件名称", new FieldCell("title").setOrderable(false));
        view.addColumn("时间", new FieldCell("time").setOrderable(false));
        view.addColumn("来源", new FieldCell("source").setOrderable(false));
        view.addColumn("备注", new FieldCell("remark").setOrderable(false));

        view.addButton(Buttons.query());
        view.addButton(Buttons.ok().setIcon(Buttons.getIcon("ok")));

        view.importJs("/platform/commons/filestore/select.js");

        return view;
    }
}
