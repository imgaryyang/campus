package com.gzzm.oa.mail;

import com.gzzm.platform.attachment.AttachmentService;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.recent.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 邮件列表，包括收件箱，草稿箱、发件箱,已删除，查询，个人文件夹
 *
 * @author lfx
 * @date 2010-3-24
 */
@Service(url = "/oa/mail/list")
public class MailList extends UserOwnedNormalCrud<Mail, Long> implements OwnedCrud<Mail, Long, Long>
{
    @Inject
    private static Provider<MailService> serviceProvider;

    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;


    /**
     * 邮件标题
     */
    @Like("title")
    private String title;

    @Lower
    private Date startTime;

    @Upper
    private Date endTime;

    /**
     * 邮件操作类
     */
    @Inject
    private MailDao dao;

    /**
     * 草稿，收件箱，或者发件箱
     */
    private MailType type;

    /**
     * 是否已经被删除
     */
    private boolean deleted;

    /**
     * 归档文件夹Id
     */
    private Long catalogId;

    /**
     * 标记Id
     */
    private Long markId;

    private List<MailMark> marks;

    private List<MailCatalog> catalogs;

    /**
     * 邮件ID，用于接收参数，打开一份邮件，或者回复一份邮件的时候，指明是哪份邮件
     */
    private Long mailId;

    /**
     * 查看邮件时，存放的邮件信息
     */
    private Mail mail;

    /**
     * 回复的内容
     */
    private String reply;

    /**
     * 往来邮件查询时关联的邮件地址
     */
    private String ref;

    /**
     * 关联的邮件体的ID，跟踪回复的邮件时使用
     */
    @Equals("body.refBodyId")
    private Long refBodyId;

    @Contains("body.text")
    private String content;

    private String page;

    private boolean showReply = true;

    private PageUserSelector userSelector;

    private Integer sender;

    private Integer receiver;

    private Boolean readed;

    @NotCondition
    private String mailFrom;

    public MailList()
    {
        setLog(true);
        addOrderBy("acceptTime", OrderType.desc);
    }

    public Long getMarkId()
    {
        return markId;
    }

    public void setMarkId(Long markId)
    {
        this.markId = markId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public MailType getType()
    {
        return type;
    }

    public void setType(MailType type)
    {
        this.type = type;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }

    public Long getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Long catalogId)
    {
        this.catalogId = catalogId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Long getMailId()
    {
        return mailId;
    }

    public void setMailId(Long mailId)
    {
        this.mailId = mailId;
    }

    public Boolean getReaded()
    {
        return readed;
    }

    public void setReaded(Boolean readed)
    {
        this.readed = readed;
    }

    public String getMailFrom()
    {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom)
    {
        this.mailFrom = mailFrom;
    }

    public Mail getMail()
    {
        return mail;
    }

    public boolean isHtml()
    {
        if (mail != null)
        {
            String contentType = mail.getBody().getContentType();
            return contentType != null && contentType.toLowerCase().startsWith("text/html");
        }

        return false;
    }

    public String getReply()
    {
        return reply;
    }

    public void setReply(String reply)
    {
        this.reply = reply;
    }

    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }

    public Long getRefBodyId()
    {
        return refBodyId;
    }

    public void setRefBodyId(Long refBodyId)
    {
        this.refBodyId = refBodyId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public boolean isShowReply()
    {
        return showReply;
    }

    public void setShowReply(boolean showReply)
    {
        this.showReply = showReply;
    }

    public Integer getSender()
    {
        return sender;
    }

    public void setSender(Integer sender)
    {
        this.sender = sender;
    }

    public Integer getReceiver()
    {
        return receiver;
    }

    public void setReceiver(Integer receiver)
    {
        this.receiver = receiver;
    }

    public String getDomain()
    {
        return MailContext.getContext().getDomain();
    }

    @Select(field = {"sender", "receiver"})
    public PageUserSelector getUserSelector()
    {
        if (userSelector == null)
            userSelector = new PageUserSelector();

        return userSelector;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        //type为3以上为一些不显示的辅助邮件，如邮件阅读回执，邮件阅读通知等
        String s = "((acceptTime>=?startTime and acceptTime<=?endTime) or " +
                "(sendTime>=?startTime and sendTime<=?endTime)) and type<3";

        if (getType() != null)
        {
            s += " and catalogId is null";

            if ((getType() == MailType.received || getType() == MailType.sended) && !showReply)
                s += " and body.refBodyId is null";
        }

        if (receiver != null && !Null.isNull(receiver))
            ref = receiver + "@local";

        if (!StringUtils.isEmpty(ref))
        {
            //往来邮件，
            s +=
                    " and mailId in (select s.mailId from MailRelationShip s where s.userId=:userId and s.refAddress=:ref)";
        }

        if (!deleted && catalogId != null && !Null.isNull(catalogId))
        {
            //全部归档邮件
            s += " and catalogId is not null";
        }

        if (!Null.isNull(readed))
        {
            if (readed)
                s += " and readTime is not null";
            else
                s += " and readTime is null";
        }

        if (!StringUtils.isEmpty(mailFrom))
        {
            s += " and(mailFrom like '%'||?mailFrom||'%' or senderUser.userName like '%'||?mailFrom||'%')";
        }

        return s;
    }

    @Select(field = "markId")
    @NotSerialized
    public List<MailMark> getMarks() throws Exception
    {
        if (marks == null)
        {
            marks = dao.getUserMarks(getUserId());
            if (marks.size() == 0)
                marks = dao.getSysMarks();
        }

        return marks;
    }

    @NotSerialized
    public List<CMenuItem> getMarkMenus() throws Exception
    {
        List<MailMark> marks = getMarks();
        if (marks != null)
        {
            List<CMenuItem> items = new ArrayList<CMenuItem>(marks.size() + 1);
            for (MailMark mark : marks)
            {
                String s = "mark(" + mark.getMarkId();
                if (mailId != null)
                    s += "," + mailId;
                s += ")";

                CMenuItem item = new CMenuItem(mark.getMarkName(), s);
                if (mark.getColor() != null)
                    item.setColor(mark.getColor());

                if (mark.getIcon() != null)
                    item.setIcon("/oa/mail/mark/icon/" + mark.getMarkId());

                items.add(item);
            }

            String s = "mark(null";
            if (mailId != null)
                s += "," + mailId;
            s += ")";
            items.add(new CMenuItem("取消标记", s));

            return items;
        }

        return null;
    }

    @Select(field = "catalogId")
    @NotSerialized
    public List<MailCatalog> getCatalogs() throws Exception
    {
        if (catalogs == null)
            catalogs = dao.getCatalogs(getUserId());

        return catalogs;
    }

    @NotSerialized
    public List<CMenuItem> getCatalogMenus() throws Exception
    {
        List<MailCatalog> catalogs = getCatalogs();
        if (catalogs != null)
        {
            List<CMenuItem> items = new ArrayList<CMenuItem>();
            for (MailCatalog catalog : catalogs)
            {
                String s = "catalog(" + catalog.getCatalogId();
                if (mailId != null)
                    s += "," + mailId;
                s += ")";

                items.add(new CMenuItem(catalog.getCatalogName(), s));
            }

            return items;
        }

        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if ("list".equals(page))
        {
            setPageSize(6);
            type = MailType.received;
        }

        if (type == null && !deleted && StringUtils.isEmpty(ref) && refBodyId == null && catalogId == null)
        {
            List<MailCatalog> catalogs = getCatalogs();
            if (catalogs.size() > 0)
                catalogId = catalogs.get(0).getCatalogId();
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        if ("list".equals(getPage()))
        {
            SimplePageListView view = new SimplePageListView()
            {
                @Override
                public Object display(Object obj) throws Exception
                {
                    Mail mail = (Mail) obj;

                    StringBuilder buffer = new StringBuilder();

                    buffer.append("<span class=\"title_trunc ");

                    if (mail.isReplyed() != null && mail.isReplyed())
                        buffer.append("title_trunc_replyed");
                    else if (mail.getReadTime() == null)
                        buffer.append("title_trunc_new");
                    else
                        buffer.append("title_trunc_old");

                    buffer.append("\"><a href=\"#\" onclick=\"showMail(").append(mail.getMailId())
                            .append(");return false\"  title=\"").append(HtmlUtils.escapeAttribute(mail.getTitle()))
                            .append("\">");

                    buffer.append(HtmlUtils.escapeHtml(mail.getTitle()));
                    buffer.append("</a></span>");

                    buffer.append("<span class=\"time\">");
                    buffer.append(DateUtils.toString(mail.getSendTime(), "yyyy-MM-dd"));
                    buffer.append("</span>");

                    return buffer.toString();
                }
            };

            view.addAction(Action.more());

            view.importCss("/oa/mail/list.css");
            view.importJs("/oa/mail/list.js");

            return view;
        }
        else
        {
            PageTableView view;

            if (type == null && !deleted && StringUtils.isEmpty(ref) && refBodyId == null)
            {
                ComplexTableView complexView =
                        new ComplexTableView(Tools.getBean(MailCatalogDisplay.class), "catalogId");
                complexView.enableDD(false);
                view = complexView;
            }
            else
            {
                view = new PageTableView();

                if (!StringUtils.isEmpty(ref))
                {
                    view.setTitle("往来邮件");
                }
                else if (refBodyId != null)
                {
                    view.setTitle("回复邮件");
                }
            }


            MailService service = serviceProvider.get();
            long capacity = service.getCapacity(getUserId());
            long wastage = service.getWastage(getUserId());
            view.setRemark(Tools.getMessage("oa.mail.remark", IOUtils.getSizeString(capacity),
                    IOUtils.getSizeString(wastage), DataConvert.format(wastage / capacity, "##%")));

            view.addComponent("标题", "title");

            if (type != MailType.draft)
                view.addComponent("时间", "startTime", "endTime");

            if (type == MailType.sended)
            {
                view.addComponent("收件人", "receiver");
            }
            else if (type == MailType.received)
            {
                view.addComponent("发件人", "mailFrom");
                view.addComponent("状态", new CCombox("readed",
                        new Object[]{new KeyValue<String>("false", "未读"),
                                new KeyValue<String>("true", "已读")}));
            }

            List<CMenuItem> markMenus = getMarkMenus();
//            if (markMenus != null)
//                view.addComponent("标记", "markId");

            view.addMoreComponent("内容", "content");
            if (!deleted && type == MailType.received)
            {
                view.addMoreComponent("显示回复的邮件", new CCombox("showReply").setNullable(false));
            }

            view.addButton(Buttons.query());

            if (!deleted && (type == MailType.received || type == MailType.sended))
            {
                List<CMenuItem> catalogMenus = getCatalogMenus();
                if (catalogMenus != null)
                    view.addButton(new CMenuButton(null, "归档到", catalogMenus).setIcon(Buttons.getIcon("catalog")));
            }

            if (markMenus != null)
                view.addButton(new CMenuButton(null, "标记为", getMarkMenus()).setIcon("/oa/mail/icons/mark.gif"));

            if (!deleted)
                view.addButton("删除", "markDeleted()").setIcon(Buttons.getIcon("delete"));

            view.addButton("彻底删除", Actions.delete0()).setIcon(Buttons.getIcon("remove"));

            if (deleted)
                view.addButton("还原", "restore()").setIcon(Buttons.getIcon("restore"));

            if (!deleted && type == MailType.received)
            {
                view.addButton("已读", "setReaded()");
            }

            view.setClass("${readTime==null?'new_bold':''}");

            //紧急
            view.addColumn("优先", new CImage("/oa/mail/icons/urgent.gif")).setDisplay("${urgent}")
                    .setOrderFiled("urgent").
                    setLocked(true).setWidth("27").setHeader(new CImage("/oa/mail/icons/urgent.gif").setPrompt("优先"));

            //附件
            view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                    .setHref("/oa/mail/attachment/${mailId}").setTarget("_blank")
                    .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                    .setDisplay("${attachment}").setLocked(true).setWidth("27")
                    .setHeader(new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

            //标记
            view.addColumn("标记", new CImage("/oa/mail/mark/icon/${markId}").setProperty("class", "mark").setPrompt(
                    "${mark.markName}")).setDisplay("${mark!=null&&mark.icon!=null}").setOrderFiled("mark.orderId")
                    .setLocked(true).setWidth("27").setHeader(new CImage("/oa/mail/icons/mark.gif").setPrompt("标记"));

            view.addColumn("标题", new FieldCell("title")
            {
                @Override
                public String display(Object entity) throws Exception
                {
                    Mail mail = (Mail) entity;

                    //改用StringBuilder拼字符串，ccs
                    StringBuilder html = new StringBuilder();

                    //标题前面加文件夹图标
                    html.append("<span class=\"title ");

                    if (mail.isReplyed() != null && mail.isReplyed())
                        html.append("title_replyed");
                    else if (mail.getReadTime() == null)
                        html.append("title_new");
                    else
                        html.append("title_old");

                    html.append("\">");

                    html.append("<a href=\"#\" onclick=\"");

                    if (mail.getType() == MailType.draft)
                        html.append("writeMail");
                    else
                        html.append("showMail");

                    html.append("(");
                    html.append(mail.getMailId());
                    html.append(");return false;\"");

                    if (mail.getMark() != null && mail.getMark().getColor() != null)
                        html.append(" style=\"color:").append(mail.getMark().getColor()).append("\"");

                    html.append(">");
                    html.append(mail.getTitle());
                    html.append("</a>");

                    html.append("</span>");

                    return html.toString();
                }
            });

            if (type != MailType.draft && type != MailType.sended)
            {
                view.addColumn("发件人", new FieldCell("senderName").setOrderField("mailFrom"));
                view.addColumn("来信部门", new FieldCell("senderUser==null?null:senderUser.allDeptName()")).setWidth("250");
            }

            if (type == null)
            {
                view.addColumn("接收时间", new FieldCell("acceptTime", "yyyy-MM-dd HH:mm"));
                view.addColumn("发送时间", new FieldCell("sendTime", "yyyy-MM-dd HH:mm")).setHidden(true);
            }
            else if (type == MailType.sended)
            {
                view.addColumn("发送时间", new FieldCell("sendTime", "yyyy-MM-dd HH:mm"));
                view.addColumn("跟踪", new CHref("跟踪").setAction("trace(${bodyId})"));
                view.addColumn("查看回复", new CHref("查看回复").setAction("showReplies(${bodyId})"));
            }
            else if (type != MailType.draft)
            {
                view.addColumn("接收时间", new FieldCell("acceptTime", "yyyy-MM-dd HH:mm"));
            }

            view.addColumn("大小", new FieldCell("mailSize").setFormat("bytesize"));

            view.importJs("/oa/mail/mail.js");
            view.importCss("/oa/mail/mail.css");
            return view;
        }
    }

    /**
     * 显示邮件
     *
     * @return 转向mail.ptl页面
     * @throws Exception 显示
     */
    @Forward(page = "/oa/mail/mail.ptl")
    @Service(url = "/oa/mail/show/{mailId}")
    public String open() throws Exception
    {
        mail = dao.getMail(mailId);

        if (mail.getReadTime() == null)
        {
            mail.setReadTime(new java.util.Date());
            dao.update(mail);

//            if (!StringUtils.isEmpty(mail.getBody().getNotificationTo()))
            serviceProvider.get().sendNotice(mail);
        }

        saveRecent();

        return null;
    }

    protected void saveRecent() throws Exception
    {
        Recent recent = new Recent();

        recent.setUserId(userOnlineInfo.getUserId());
        recent.setRecentName(mail.getTitle());
        recent.setTarget(RecentTarget.TAB);
        recent.setUrl("/oa/mail/show/" + mailId);
        recent.setShowTime(mail.getAcceptTime());
        recent.setType("mail");

        RecentService service = Tools.getBean(RecentService.class);
        service.saveRecent(recent);
    }

    /**
     * 把选择的邮件移动到选定的个人文件夹
     *
     * @param mailId    邮件的ID
     * @param catalogId 目录id
     * @throws Exception 数据库更新异常
     */
    @ObjectResult
    @Service
    public void catalog(Long catalogId, Long mailId) throws Exception
    {
        dao.catalog(catalogId, mailId);
    }

    /**
     * 把选择的邮件批量移动到选定的个人文件夹
     *
     * @param catalogId 目录id
     * @throws Exception 数据库更新异常
     */
    @ObjectResult
    @Service
    public void catalogAll(Long catalogId) throws Exception
    {
        dao.catalog(catalogId, getKeys());
    }

    /**
     * 标记邮件
     *
     * @param mailId 被标记的邮件
     * @param markId 标记Id
     * @throws Exception 数据库更新操作异常
     */
    @ObjectResult
    @Service
    public void mark(Long markId, Long mailId) throws Exception
    {
        if (markId == null)
            dao.cancelMark(mailId);
        else
            dao.mark(markId, mailId);
    }

    /**
     * 标记多个邮件
     *
     * @param markId 标记Id
     * @throws Exception 数据库更新操作异常
     */
    @ObjectResult
    @Service
    public void markAll(Long markId) throws Exception
    {
        if (markId == null)
            dao.cancelMark(getKeys());
        else
            dao.mark(markId, getKeys());
    }

    /**
     * 标记邮件被删除，即移到垃圾箱
     *
     * @param mailId 邮件ID
     * @throws Exception 数据库更新操作异常
     */
    @ObjectResult
    @Service
    public void markDeleted(Long mailId) throws Exception
    {
        dao.markDeleted(mailId);
    }

    /**
     * 标记邮件被删除，即移到垃圾箱
     *
     * @throws Exception 数据库更新操作异常
     */
    @ObjectResult
    @Service
    public void markDeletedAll() throws Exception
    {
        dao.markDeleted(getKeys());
    }

    /**
     * 恢复邮件，即将邮件从垃圾箱移到原来的位置
     *
     * @param mailId 邮件ID
     * @throws Exception 数据库更新操作异常
     */
    @ObjectResult
    @Service
    public void restore(Long mailId) throws Exception
    {
        dao.restore(mailId);
    }

    /**
     * 恢复邮件，即将邮件从垃圾箱移到原来的位置
     *
     * @throws Exception 数据库更新操作异常
     */
    @ObjectResult
    @Service
    public void restoreAll() throws Exception
    {
        dao.restore(getKeys());
    }

    /**
     * 回复邮件
     *
     * @throws Exception 回复邮件错误
     */
    @ObjectResult
    @Service(method = HttpMethod.post)
    public void replyQuickly() throws Exception
    {
        MailService service = serviceProvider.get();

        if (service.isCapacityFull(getUserId()))
            throw new NoErrorException("oa.mail.capacityfull");

        //获得原来的邮件
        Mail mail = service.getMail(mailId);


        //新邮件的邮件体
        MailBody newBody = MailUtils.reply(mail);

        newBody.setContent((reply + new String(newBody.getContent())).toCharArray());

        //保存并发送邮件
        if (service.createAndSend(newBody, userOnlineInfo.getUserId(), userOnlineInfo))
            SmtpSendJob.send();
    }

    /**
     * 将邮件附件打包下载
     *
     * @param mailId 邮件对应的mailId
     * @return zip文件
     * @throws Exception 数据库获取数据错误或者文件压缩错误
     */
    @Service(url = "/oa/mail/attachment/{$0}")
    public InputFile zipAttachments(Long mailId) throws Exception
    {
        Mail mail = getEntity(mailId);

        if (mail == null)
            return null;

        checkEntity(mail);

        MailBody body = mail.getBody();
        if (body.getAttachmentId() == null)
            return null;

        return attachmentServiceProvider.get().zip(body.getAttachmentId(), body.getTitle());
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void setReaded() throws Exception
    {
        dao.setReaded(getKeys(), new java.util.Date());
    }

    @Override
    public boolean delete(Long mailId) throws Exception
    {
        MailService service = serviceProvider.get();

        Mail mail = service.getMail(mailId);
        if (mail != null)
        {
            MailBody body = mail.getBody();

            boolean result = super.delete(mailId);

            if (body.getMails().size() == 0)
            {
                //没有其他邮件，删除邮件体
                service.deleteBody(body.getBodyId());
            }

            return result;
        }

        return false;
    }

    @Override
    public int deleteAll() throws Exception
    {
        int result = 0;
        for (Long mailId : getKeys())
        {
            if (delete(mailId))
                result++;
        }

        return result;
    }

    /**
     * 获得某个邮件帐号的部门信息
     *
     * @param address 邮件地址
     * @return 用户所属的部门的全名，如果是多个部门用逗号连接
     * @throws Exception 数据库查询异常
     */
    @Service
    @ObjectResult
    public String getDeptNameForUser(String address) throws Exception
    {
        User user = dao.getUserBySourceMail(address);

        if (user == null)
        {
            int index = address.indexOf('@');
            String account = address.substring(0, index);

            Integer userId = null;
            try
            {
                userId = Integer.valueOf(account);
            }
            catch (NumberFormatException ex)
            {
                //用户ID不是整数
            }

            if (userId != null)
                user = dao.getUser(userId);

            if (user == null)
                user = dao.getUserByLoginName(account);
        }

        if (user != null)
            return user.allDeptName();

        return "";
    }

    public String getOwnerField()
    {
        return "catalogId";
    }

    public Long getOwnerKey(Mail entity) throws Exception
    {
        return entity.getCatalogId();
    }

    public void setOwnerKey(Mail entity, Long ownerKey) throws Exception
    {
        entity.setCatalogId(ownerKey);
    }

    public void moveTo(Long key, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.moveTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }

    public void moveAllTo(Long[] keys, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.moveTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }

    public void copyTo(Long key, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.copyTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }

    public void copyAllTo(Long[] keys, Long newOwnerKey, Long oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.copyTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }
}
