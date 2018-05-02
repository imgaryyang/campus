package com.gzzm.oa.notice;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 内部信息维护界面
 *
 * @author czf
 * @date 2010-3-16
 */
@Service(url = "/oa/notice/notice")
public class NoticeCrud extends DeptOwnedEditableCrud<Notice, Integer> implements OwnedCrud<Notice, Integer, Integer>
{
    static
    {
        ParameterCheck.addNoCheckURL("/oa/notice/notice");
    }

    @Inject
    private NoticeDao dao;

    @UserId
    private Integer userId;

    @Inject
    private DeptService deptService;

    @Like
    private String title;

    private Integer typeId;

    @Equals("typeId")
    private Integer typeId0;

    /**
     * 跟踪列表
     */
    @NotSerialized
    private List<Track> traceList;

    //已阅读的人数
    @NotSerialized
    private int readedCount;

    @Equals("noticeType.sortId")
    private Integer sortId;

    private InputFile file;

    public NoticeCrud()
    {
        addOrderBy("createTime", OrderType.desc);
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
        {
            return null;
        }
        else
        {
            return typeId;
        }
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

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    public List<Track> getTraceList()
    {
        return traceList;
    }

    @NotSerialized
    public int getAllCount()
    {
        return traceList.size();
    }

    public int getReadedCount()
    {
        return readedCount;
    }

    @NotSerialized
    public int getNoReadedCount()
    {
        return getAllCount() - readedCount;
    }

    @Override
    public void initEntity(Notice entity) throws Exception
    {
        super.initEntity(entity);

        entity.setType(InfoType.edit);
        entity.setState(NoticeState.noPublished);
        entity.setTopTag(TopTag.noTop);

        if (typeId == null)
        {
            if (typeId0 != null)
            {
                entity.setTypeId(typeId0);
                entity.setNoticeType(dao.getNoticeType(typeId0));
            }
        }
        else
        {
            entity.setTypeId(typeId);
            entity.setNoticeType(dao.getNoticeType(typeId));
        }
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    @Forward(page = "/oa/notice/notice.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forwards({
            @Forward(page = "/oa/notice/notice.ptl"), @Forward(name = "top", page = Pages.EDIT)
    })
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);
        return forward;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;
        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
        {
            if (typeId0 == null)
            {
                NoticeTypeDisplay noticeTypeDisplay = Tools.getBean(NoticeTypeDisplay.class);
                noticeTypeDisplay.setSortId(sortId);
                view = new ComplexTableView(noticeTypeDisplay, "typeId", true).enableDD();
            }
            else
            {
                view = new PageTableView(true);
            }
        }
        else
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);
        }

        view.addComponent("标题", "title");

        view.addColumn("标题", "title");
        view.addColumn("创建人", "user.userName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("发布时间", "publishTime").setHidden(true);
        view.addColumn("有效时间", "invalidTime").setHidden(true);

        if (typeId0 == null)
        {
            view.addColumn("栏目", "noticeType.typeName");
        }

        view.addColumn("状态", "state");
        view.addColumn("置顶", new HrefCell("topTag").setAction(Actions.show("top")));
        view.addColumn("预览", new CHref("预览", "/oa/notice/read/${noticeId}").setTarget("_blank"));
        view.addColumn("跟踪", new CHref("跟踪", "javascript:openTrack(${noticeId})"));

        view.addDefaultButtons();
        view.makeEditable();

        view.importJs("/oa/notice/notice.js");
        view.addButton(new CButton("发布", "doPublish()"));
        view.addButton(new CButton("取消发布", "doCancelPublish()"));

        return view;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        Notice notice = getEntity();
        notice.setTemplateId(dao.getNoticeType(notice.getTypeId()).getTemplateId());

        java.sql.Date invalidTime = notice.getInvalidTime();
        if (!Null.isNull(invalidTime) && DateUtils.addDate(invalidTime, 1).compareTo(new Date()) < 1)
        {
            throw new NoErrorException(Tools.getMessage("com.gzzm.oa.notice.InvalidTimeErrMsg"));
        }

        if (notice.getState() == NoticeState.published)
        {
            notice.setPublishTime(new Date());
        }
        else if (notice.getState() == NoticeState.noPublished)
        {
            notice.setPublishTime(Null.Date);
        }

        if (file != null && notice.getType() == InfoType.file)
        {
            notice.setFileContent(file.getInputStream());
            notice.setFileType(file.getExtName());
        }

        return true;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreateTime(new Date());
        getEntity().setCreator(userId);
        getEntity().setReadTimes(0);

        return true;
    }

    public Integer getOwnerKey(Notice entity) throws Exception
    {
        return entity.getTypeId();
    }

    public void setOwnerKey(Notice entity, Integer ownerKey) throws Exception
    {
        entity.setTypeId(ownerKey);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        if ("top".equals(getForward()))
        {
            SimpleDialogView view = new SimpleDialogView();

            view.addComponent("是否置顶", "topTag");
            view.addComponent("置顶时间", "topInvalidTime");
            view.setTitle("置顶状态维护");

            view.addDefaultButtons();

            return view;
        }
        else
        {
            return null;
        }
    }

    @NotSerialized
    @Select(field = "entity.typeId")
    public List<NoticeType> getTypeList() throws Exception
    {
        return dao.getNoticeTypes(getDeptId(), sortId);
    }

    /**
     * 批量发布信息
     *
     * @throws Exception 数据库操作异常
     */
    @ObjectResult
    @Service(method = HttpMethod.post)
    public void publish() throws Exception
    {
        dao.publish(getKeys());
    }

    /**
     * 批量取消发布信息
     *
     * @throws Exception 数据库操作异常
     */
    @ObjectResult
    @Service(method = HttpMethod.post)
    public void cancelPublish() throws Exception
    {
        dao.cancelPublish(getKeys());
    }

    /**
     * 打开信息阅读跟踪页面
     *
     * @param noticeId，信息ID
     * @return 页面的路径
     * @throws Exception 从数据库读取数据错误
     */
    @Service
    public String track(Integer noticeId) throws Exception
    {
        Notice notice = getCrudService().get(Notice.class, noticeId);

        //有阅读权限的用户列表
        List<User> users = deptService.getUsersByApp("noticeread", notice.getDeptId());

        //已经阅读的信息列表
        List<NoticeTrace> traces = dao.getNoticeTraceList(noticeId);
        traceList = new ArrayList<Track>(users.size());


        for (User user : users)
        {
            Track track = new Track(user);

            for (NoticeTrace noticeTrack : traces)
            {
                if (user.getUserId().intValue() == noticeTrack.getUserId().intValue())
                {
                    track.setReadTime(noticeTrack.getReadTime());
                    readedCount++;
                    break;
                }
            }

            traceList.add(track);
        }

        return "/oa/notice/track.ptl";
    }

    @Service(url = "/oa/notice/{$0}/down")
    public InputFile down(Integer noticeId, String contentType) throws Exception
    {
        Notice notice = dao.getNotice(noticeId);

        String fileName = notice.getTitle();
        if (!StringUtils.isEmpty(notice.getFileType()))
        {
            fileName += "." + notice.getFileType();
        }

        return new InputFile(notice.getFileContent(), fileName, contentType);
    }

    @Override
    public Notice clone(Notice entity) throws Exception
    {
        Notice notice = super.clone(entity);
        notice.setContent(entity.getContent());

        return notice;
    }

    public String getOwnerField()
    {
        return "typeId";
    }

    public void copyAllTo(Integer[] keys, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.copyTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }

    public void copyTo(Integer key, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.copyTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }

    public void moveAllTo(Integer[] keys, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.moveTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }

    public void moveTo(Integer key, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.copyTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }
}
