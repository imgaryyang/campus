package com.gzzm.oa.notice;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 内部信息浏览
 *
 * @author czf
 * @date 2010-3-18
 */
@Service(url = "/oa/notice/read")
public class NoticeReadQuery extends DeptOwnedQuery<Notice, Integer>
{
    @Like("title")
    private String title;

    @Lower(column = "publishTime")
    private Date time_start;

    @Upper(column = "publishTime")
    private Date time_end;

    @UserId
    private Integer userId;

    @Inject
    private NoticeDao dao;

    @Inject
    private GlobalConfig globalConfig;

    private Integer typeId;

    @Equals("typeId")
    private Integer typeId0;

    @Equals("noticeType.sortId")
    private Integer sortId;

    private String page;

    public NoticeReadQuery()
    {
        addOrderBy(
                "case when topTag=1 and (topInvalidTime is null or addday(topInvalidTime,1)>=sysdate()) then 1 else 0 end",
                OrderType.desc);
        addOrderBy("publishTime", OrderType.desc);
        addOrderBy("noticeId", OrderType.desc);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getTypeId()
    {
        if (typeId != null && typeId == 0)
            return null;
        else
            return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getTypeId0()
    {
        return typeId0;
    }

    public void setTypeId0(Integer typeId0)
    {
        this.typeId0 = typeId0;
    }

    public Integer getSortId()
    {
        return sortId;
    }

    public void setSortId(Integer sortId)
    {
        this.sortId = sortId;
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

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String condtion = "state = 1 and (invalidTime is null or addday(invalidTime,1)>=sysdate())";

        if (getTypeId() == null)
            return condtion;
        else
            return condtion + " and typeId = ?typeId";
    }

    protected Object createListView() throws Exception
    {
        if ("list".equals(page))
        {
            SimplePageListView view = new SimplePageListView()
            {
                @Override
                public Object display(Object obj) throws Exception
                {
                    Notice notice = (Notice) obj;

                    StringBuilder buffer = new StringBuilder();

                    buffer.append("<span class=\"type\">[<a href=\"#\" onclick=\"more('");
                    buffer.append(notice.getNoticeType().getTypeId()).append("');return false;\">");
                    buffer.append(notice.getNoticeType().getTypeName());
                    buffer.append("</a>]</span>");

                    buffer.append("<span class=\"title_trunc ");

                    if (notice.isTop())
                    {
                        if ((System.currentTimeMillis() - notice.getPublishTime().getTime())
                                / DateUtils.MILLIS_IN_DAY < 2)
                            buffer.append(" title_trunc_top_new");
                        else
                            buffer.append(" title_trunc_top");
                    }

                    if (isNoReaded(notice.getNoticeId()))
                        buffer.append(" title_trunc_new");

                    buffer.append("\"><a href=\"javascript:showNotice(").append(notice.getNoticeId())
                            .append(")\"  title=\"")
                            .append(HtmlUtils.escapeAttribute(notice.getTitle()))
                            .append("\">");

                    buffer.append(notice.getTitle());
                    buffer.append("</a></span>");

                    buffer.append("<span class=\"time\">");
                    buffer.append(DateUtils.toString(notice.getPublishTime(), "yyyy-MM-dd"));
                    buffer.append("</span>");

                    return buffer.toString();
                }
            };

            view.addAction(Action.more());

            view.importCss("/oa/notice/list.css");
            view.importJs("/oa/notice/list.js");

            return view;
        }
        else
        {
            PageTableView view;
            if (typeId0 == null)
            {
                NoticeTypeDisplay noticeTypeDisplay = Tools.getBean(NoticeTypeDisplay.class);
                noticeTypeDisplay.setSortId(sortId);
                view = new ComplexTableView(noticeTypeDisplay, "typeId", false);
            }
            else
            {
                view = new PageTableView();
            }

            view.addComponent("标题", "title");
            view.addComponent("发布时间", "time_start", "time_end");
            view.addButton(Buttons.query());

            view.addColumn("标题", new FieldCell("title")
            {
                @Override
                public String display(Object entity) throws Exception
                {
                    Notice notice = (Notice) entity;

                    StringBuilder buffer = new StringBuilder("<span class=\"title");

                    if (notice.isTop())
                    {
                        if ((System.currentTimeMillis() - notice.getPublishTime().getTime())
                                / DateUtils.MILLIS_IN_DAY < 2)
                            buffer.append(" title_top_new");
                        else
                            buffer.append(" title_top");
                    }

                    if (isNoReaded(notice.getNoticeId()))
                        buffer.append(" title_new");

                    buffer.append("\"><a href=\"javascript:showNotice(").append(notice.getNoticeId())
                            .append(")\" >");
                    buffer.append(notice.getTitle());
                    buffer.append("</a></span>");

                    return buffer.toString();
                }
            });
            view.addColumn("创建人", "user.userName");
            view.addColumn("发布时间", "publishTime");

            if (typeId0 == null)
                view.addColumn("栏目", "noticeType.typeName");
            view.addColumn("阅读次数", "readTimes");

            view.importCss("/oa/notice/list.css");
            view.importJs("/oa/notice/notice.js");

            return view;
        }
    }

    /**
     * 判断当前用户是否未读此信息
     *
     * @param noticeId 要判断的信息的ID
     * @return 未读返回true，已读返回false
     * @throws Exception 数据库异常
     */
    private boolean isNoReaded(Integer noticeId) throws Exception
    {
        return dao.getNoticeTrace(noticeId, userId) == null;
    }


    @Override
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);

        Notice notice = getEntity();

        notice.setReadTimes(notice.getReadTimes() == null ? 1 : notice.getReadTimes() + 1);
        dao.update(notice);
        dao.addNoticeTrace(key, userId);

        setEntity(notice);

        if (notice.getType() == InfoType.url)
        {
            RequestContext.getContext().redirect(notice.getUrl());
            return null;
        }
        else if (notice.getType() == InfoType.file)
        {
            String s;
            String fileType = notice.getFileType();
            if (OfficeUtils.canChangeToHtml(fileType))
            {
                s = OfficeUtils.toHtml(notice.getFileContent(), fileType);
            }
            else
            {
                s = "/oa/notice/" + notice.getNoticeId() + "/down";

                if (fileType != null && "pdf".equalsIgnoreCase(fileType))
                {
                    String s1 = Tools.getPdfViewUrl(s);

                    if (s1 != null)
                        s = s1;
                    else
                        s = s + "?contentType=application/pdf";
                }
            }
            RequestContext.getContext().redirect(s);
            return null;
        }
        else
        {
            return notice.getTemplate().getTemplatePath();
        }
    }
}
