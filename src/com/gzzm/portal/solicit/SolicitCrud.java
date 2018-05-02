package com.gzzm.portal.solicit;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptTreeModel;
import com.gzzm.portal.commons.StationOwnedCrud;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.sql.*;
import java.util.*;

/**
 * @author lk
 * @date 13-10-17
 */
@Service(url = "/portal/solicit/SolicitCrud")
public class SolicitCrud extends StationOwnedCrud<Solicit, Integer>
{
    static
    {
        ParameterCheck.addNoCheckURL("/portal/solicit/SolicitCrud");
    }

    @Inject
    private AttachmentService attachmentService;

    private PageAttachmentList attachments;

    @Inject
    private SolicitDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    //根据标题查询
    @Like
    private String title;

    @NotSerialized
    private AuthDeptTreeModel deptTree;

    //根据创建时间的范围查询
//	@Lower(column = "createTime")
    private Date createTime_start;
    //	@Upper(column = "createTime")
    private Date createTime_end;


    public SolicitCrud()
    {
        addOrderBy("createTime desc");
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getCreateTime_start()
    {
        return createTime_start;
    }

    public void setCreateTime_start(Date createTime_start)
    {
        this.createTime_start = createTime_start;
    }

    public Date getCreateTime_end()
    {
        return createTime_end;
    }

    public void setCreateTime_end(Date createTime_end)
    {
        this.createTime_end = createTime_end;
    }

    public PageAttachmentList getAttachments()
    {
        return attachments;
    }

    public void setAttachments(PageAttachmentList attachments)
    {
        this.attachments = attachments;
    }

    @NotSerialized
    @Select(field = "entity.typeId")
    public List<SolicitType> getTypes()
    {
        Integer stationId = getEntity().getStationId();
        if (stationId == null)
        {
            return Collections.emptyList();
        }
        return dao.getSolicitTypes(stationId);
    }

    @Select(field = {"entity.publishDeptId"})
    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
        {
            deptTree = new AuthDeptTreeModel();
        }
        return deptTree;
    }

    @NotSerialized
    @Select(field = "entity.state")
    public List<SolicitState> getSolicitStates()
    {
        List<SolicitState> states = new ArrayList<SolicitState>(3);
        if (isNew$())
        {
            states.add(SolicitState.notPublished);
        }
        else if (getEntity().getState().equals(SolicitState.notPublished))
        {
            states.add(SolicitState.notPublished);
            states.add(SolicitState.published.setName("发布"));
        }
        else if (getEntity().getState().equals(SolicitState.published))
        {
            states.add(SolicitState.published.setName("发布"));
            states.add(SolicitState.notPublished);
            states.add(SolicitState.stop.setName("停止征集"));
        }
        else if (getEntity().getState().equals(SolicitState.stop))
        {
            states.add(SolicitState.stop.setName("停止征集"));
            states.add(SolicitState.published.setName("继续征集"));
        }

        return states;
    }

    @Service
    public void publish(boolean doPublish) throws Exception
    {
        SolicitState state = doPublish ? SolicitState.published.setName("发布") : SolicitState.notPublished;
        Integer[] keys = getKeys();
        for (int key : keys)
        {
            Solicit entity = getEntity(key);
            entity.setState(state);
            entity.setPublishTime(doPublish ? new java.util.Date() : Null.Date);
            update(entity);
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        SolicitState.published.setName("发布");
        SolicitState.stop.setName("已停止");

        PageTableView view = new PageTableView();

        view.addColumn("征求标题", "title");
        view.addColumn("分类", "type");
        view.addColumn("创建人", "creator").setWidth("80");
        view.addColumn("创建时间", "createTime").setWidth("150");
        view.addColumn("所属网站", "station.stationName");
        view.addColumn("状态", "state");
        view.addColumn("发布时间", "publishTime").setWidth("150");
        view.addColumn("发布部门", "publishDept.deptName");
        view.addColumn("回复列表", new CButton("查回复列表", "showReply(${solicitId},'${title}')"));
        view.addComponent("标题", "title");
        view.addComponent("创建时间", "createTime_start", "createTime_end");
        view.addComponent("站点", "stationId");
        view.addDefaultButtons();
        view.makeEditable();
        view.addButton("发布", "publish(true)");
        view.addButton("取消发布", "publish(false)");

        view.importJs("/portal/solicit/solicit.js");
        return view;
    }

    @Override
    @Forward(page = "/portal/solicit/solicit.ptl")
    public String add(String forward) throws Exception
    {
        attachments = new PageAttachmentList();
        return super.add(forward);
    }

    @Override
    @Forward(page = "/portal/solicit/solicit.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        attachments = new PageAttachmentList();
        String str = super.show(key, forward);
        if (getEntity().getAttachmentId() != null && getEntity().getAttachmentId() > 0)
        {
            attachments.setAttachmentId(getEntity().getAttachmentId());
        }
        return str;
    }

    @Override
    public void initEntity(Solicit entity) throws Exception
    {
        entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        entity.setCreatorId(userOnlineInfo.getUserId());
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setDeptId(userOnlineInfo.getDeptId());
        if (Null.isNull(getEntity().getPublishTime()))
        {
            getEntity().setState(SolicitState.notPublished);
        }
        else
        {
            getEntity().setState(SolicitState.published);
        }
        if (attachments != null)
        {

            getEntity().setAttachmentId(
                    attachments.save(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(), "solicit"));


        }
        return super.beforeInsert();
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        if (Null.isNull(getEntity().getPublishTime()))
        {
            getEntity().setState(SolicitState.notPublished);
        }
        else
        {
            getEntity().setState(SolicitState.published);
        }
        if (attachments != null)
        {
            getEntity().setAttachmentId(
                    attachments.save(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(), "solicit"));
        }
        else
        {

        }
        return super.beforeUpdate();
    }

    @Service(url = "/portal/solicit/annex/{$0}/down")
    public InputFile downByAnnexId(Integer solicitId) throws Exception
    {


        Solicit solicit = getCrudService().get(Solicit.class, solicitId);

        if (userOnlineInfo == null && solicit.getState() == SolicitState.notPublished)
        {
            return null;
        }

        SortedSet<Attachment> attachments = solicit.getAttachments();

        if (attachments == null)
        {
            return null;
        }
        else if (attachments.size() == 1)
        {
            Attachment attachment = attachments.iterator().next();
            return attachment.getInputFile();
        }
        else
        {
            return attachmentService.zip(solicit.getAttachmentId(), solicit.getTitle());
        }
    }

}
