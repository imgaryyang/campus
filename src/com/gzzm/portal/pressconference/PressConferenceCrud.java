package com.gzzm.portal.pressconference;

import com.gzzm.platform.attachment.PageAttachmentList;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Null;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.Align;
import net.cyan.nest.annotation.Inject;

import java.sql.*;

/**
 * @author lk
 * @date 13-10-20
 */
@Service(url = "/portal/pressconference/PressConferenceCrud")
public class PressConferenceCrud extends BaseNormalCrud<PressConference, Integer>
{
    PressConferenceCrud()
    {
        setLog(true);
        addOrderBy("releaseDate", OrderType.desc);
    }

    @Inject
    private UserOnlineInfo userOnlineInfo;

    //标题
    @Like
    private String title;

    //根据新闻发言人查询
    @Like
    private String spokesman;

    //根据发布时间查询
    @Lower(column = "releaseDate")
    private Date releaseDate_start;

    @Upper(column = "releaseDate")
    private Date releaseDate_end;

    //根据公布状态查询
    private PressConferenceState state;

    //删除图片标记
    private boolean deletePhoto;

    /**
     * 附件
     */
    @NotSerialized
    protected PageAttachmentList attachments;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSpokesman()
    {
        return spokesman;
    }

    public void setSpokesman(String spokesman)
    {
        this.spokesman = spokesman;
    }

    public Date getReleaseDate_start()
    {
        return releaseDate_start;
    }

    public void setReleaseDate_start(Date releaseDate_start)
    {
        this.releaseDate_start = releaseDate_start;
    }

    public Date getReleaseDate_end()
    {
        return releaseDate_end;
    }

    public void setReleaseDate_end(Date releaseDate_end)
    {
        this.releaseDate_end = releaseDate_end;
    }

    public PressConferenceState getState()
    {
        return state;
    }

    public void setState(PressConferenceState state)
    {
        this.state = state;
    }

    public boolean isDeletePhoto()
    {
        return deletePhoto;
    }

    public void setDeletePhoto(boolean deletePhoto)
    {
        this.deletePhoto = deletePhoto;
    }

    public PageAttachmentList getAttachments()
    {
        return attachments;
    }

    public void setAttachments(PageAttachmentList attachments)
    {
        this.attachments = attachments;
    }

    @Service(url = "/portal/pressconference/photo/{$0}")
    public byte[] getPhoto(int pressConferenceId) throws Exception
    {
        return getEntity(pressConferenceId).getPhoto();
    }

    /**
     * 提交
     */
    @Transactional
    @Service(method = HttpMethod.post)
    public void commitOk() throws Exception
    {
        if (getKeys() == null || getKeys().length == 0) throw new NoErrorException("请选择需要提交的记录");

        for (Integer key : getKeys())
        {
            PressConference conference = getEntity(key);
            if (conference.getState() != PressConferenceState.notCommitted)
                throw new NoErrorException("只有处于待提交状态的信息才可以提交");
        }

        //将状态设为已提交
        for (Integer key : getKeys())
        {
            PressConference entity = getEntity(key);
            entity.setState(PressConferenceState.committed);
            update(entity);
        }
    }

    /**
     * 发布
     */
    @Transactional
    @Service(method = HttpMethod.post)
    public void publishConference() throws Exception
    {
        if (getKeys() == null || getKeys().length == 0) throw new NoErrorException("请选择需要发布的记录");

        for (Integer key : getKeys())
        {
            PressConference conference = getEntity(key);
            if (conference.getState() != PressConferenceState.committed)
                throw new NoErrorException("只有处于已提交状态的信息才可以发布");
        }

        //将状态设为已发布
        for (Integer key : getKeys())
        {
            PressConference entity = getEntity(key);
            entity.setState(PressConferenceState.published);
            update(entity);
        }
    }

    /**
     * 取消发布
     */
    @Transactional
    @Service(method = HttpMethod.post)
    public void cancelPublish() throws Exception
    {
        if (getKeys() == null || getKeys().length == 0) throw new NoErrorException("请选择需要取消发布的记录");

        for (Integer key : getKeys())
        {
            PressConference conference = getEntity(key);
            if (conference.getState() != PressConferenceState.published)
                throw new NoErrorException("只有处于发布状态的信息才可以取消发布");
        }

        //将已发布状态设为已提交
        for (Integer key : getKeys())
        {
            PressConference entity = getEntity(key);
            entity.setState(PressConferenceState.committed);
            update(entity);
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("标题", "title");
        view.addComponent("新闻发言人", "spokesman");
        view.addComponent("公布状态", "state");
        view.addMoreComponent("发布时间", "releaseDate_start", "releaseDate_end");

        view.addColumn("标题", "title");
        view.addColumn("状态", "state").setWidth("70");
        view.addColumn("新闻发言人", "spokesman").setWidth("150");
        view.addColumn("发布时间", "releaseDate").setWidth("120").setAlign(Align.center);
        view.addColumn("发布地点", "address").setWidth("200");

        view.defaultInit();

        view.addButton(Buttons.getButton("提交", "commitOk()", "ok"));
        view.addButton(Buttons.getButton("发布", "publishConference()", "ok"));
        view.addButton(Buttons.getButton("取消发布", "cancelPublish()", "delete"));
        view.importJs("/portal/pressconference/pressConference.js");

        return view;
    }

    @Override
    @Forward(page = "/portal/pressconference/pressConference.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        CrudUtils.loadEntity(this, key);
        attachments = new PageAttachmentList();
        attachments.setAttachmentId(getEntity().getAttachmentId());
        return super.show(key, forward);
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        boolean rel = super.beforeSave();
        if (attachments != null)
        {
            attachments.setAttachmentId(getEntity().getAttachmentId());
            getEntity()
                    .setAttachmentId(
                            attachments.save(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(), "conference"));
        }

        return rel;
    }

    @Override
    @Forward(page = "/portal/pressconference/pressConference.ptl")
    public String add(String forward) throws Exception
    {
        attachments = new PageAttachmentList();
        return super.add(forward);
    }

    @Override
    public void initEntity(PressConference entity) throws Exception
    {
        entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        entity.setCreatorId(userOnlineInfo.getUserId());
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        PressConference entity = getEntity();

        //删除图片
        if (entity.getPhoto() == null && deletePhoto)
        {
            entity.setPhoto(Null.ByteArray);
        }

        return true;
    }
}
