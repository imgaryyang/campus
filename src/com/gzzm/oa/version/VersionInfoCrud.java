package com.gzzm.oa.version;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 项目版本更新发布信息展示
 *
 * @author zy
 * @date 2017/1/10 10:05
 */
@Service(url = "/oa/version/crud")
public class VersionInfoCrud extends BaseNormalCrud<VersionInfo, Integer>
{
    /**
     * 系统编号
     */
    private ProjectDir project;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 版本名称
     */
    @Like
    private String verName;

    /**
     * 创建开始时间
     */
    @Lower(column = "createTime")
    private Date createTimeStart;

    /**
     * 创建结束时间
     */
    @Upper(column = "createTime")
    private Date createTimeEnd;

    /**
     * 是否桌面布局
     */
    private boolean deskTop;

    @Inject
    private VersionDao dao;

    public VersionInfoCrud()
    {
        addOrderBy("createTime", OrderType.desc);
        addOrderBy("versionId", OrderType.desc);
    }

    public ProjectDir getProject()
    {
        return project;
    }

    public void setProject(ProjectDir project)
    {
        this.project = project;
    }

    public String getVerName()
    {
        return verName;
    }

    public void setVerName(String verName)
    {
        this.verName = verName;
    }

    public Date getCreateTimeStart()
    {
        return createTimeStart;
    }

    public void setCreateTimeStart(Date createTimeStart)
    {
        this.createTimeStart = createTimeStart;
    }

    public Date getCreateTimeEnd()
    {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd)
    {
        this.createTimeEnd = createTimeEnd;
    }

    public boolean isDeskTop()
    {
        return deskTop;
    }

    public void setDeskTop(boolean deskTop)
    {
        this.deskTop = deskTop;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected Object createListView() throws Exception
    {
        if (isDeskTop())
        {
            setPageSize(1);
            SimplePageListView view = new SimplePageListView()
            {
                @Override
                public Object display(Object obj) throws Exception
                {
                    VersionInfo entity = (VersionInfo) obj;
                    String content = entity.getPublishContent().replace("\n", "<br/>");
                    return "<div class=\"components\"><div class=\"component_item\"><div class=\"label\">版本名称</div><div class=\"value\">" + entity.getVerName() + "</div></div><div class=\"component_item\"><div class=\"label\">版本号</div><div class=\"value\">" + entity.getVerNo() + "</div></div><div class=\"component_item\"><div class=\"label\">更新时间</div><div class=\"value\">" + DateUtils.toString(entity.getCreateTime(), "yyyy-MM-dd") + "</div></div><div class=\"component_item\"><div class=\"label\">更新内容</div><div class=\"value longValue\">" + content + "</div></div></div>";
                }
            };
            view.addAction(Action.more());
            view.importJs("/oa/version/version.js");
            view.importCss("/oa/version/version.css");
            return view;
        }
        else
        {
            PageTableView view = new PageTableView();
            view.addComponent("系统名称", "project");
            view.addComponent("版本名称", "verName");
            view.addComponent("发布时间", "createTimeStart", "createTimeEnd");
            view.addColumn("系统名称", "project").setWidth("160");
            view.addColumn("版本名称", "verName").setWidth("160");
            view.addColumn("版本号", "verNo").setWidth("60");
            view.addColumn("发布时间", "createTime").setWidth("140").setAlign(Align.center);
            view.addColumn("主动弹出", "push");
            view.addColumn("发布内容", "publishContent");
            view.addButton(Buttons.query());
            if (userOnlineInfo.isAdmin())
            {
                view.addColumn("发布方式", "releaseMode").setWidth("60");
                view.addButton(Buttons.add());
                view.addButton(Buttons.delete());
                view.addColumn("修改", new ConditionComponent().add("releaseMode==releaseMode.MANUAL", Buttons.edit())).setWidth("60");
            }
            return view;
        }
    }

    @Override
    public void initEntity(VersionInfo entity) throws Exception
    {
        entity.setCreateTime(new java.util.Date());
        super.initEntity(entity);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("系统名称", "project");
        view.addComponent("版本名称", "verName");
        view.addComponent("版本号", "verNo");
        view.addComponent("发布时间", "createTime");
        view.addComponent("主动弹出", "push");
        view.addComponent("发布内容", new CTextArea("publishContent"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setPublisher(userOnlineInfo.getUserId());
        getEntity().setReleaseMode(ReleaseMode.MANUAL);
        getEntity().setUpdateTime(new java.util.Date());
        if (project != null)
        {
            getEntity().setProject(project);
        }
        if (getEntity().getPush() != null && getEntity().getPush())
        {
            List<Integer> userIdList = dao.getPushUser();
            List<VersionPushUser> pushUsers = new ArrayList<VersionPushUser>();
            for (Integer userId : userIdList)
            {
                pushUsers.add(new VersionPushUser(userId,getEntity().getProject()));
            }
            dao.saveEntities(pushUsers);
        }
        return super.beforeInsert();
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        getEntity().setUpdateTime(new java.util.Date());
        return super.beforeUpdate();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        Integer versionId = null;
        if (!isNew$())
        {
            versionId = getEntity().getVersionId();
        }
        if (dao.checkVersionInfo(getEntity().getProject(), getEntity().getVerNo(), versionId) != null)
        {
            throw new MessageException("重复的版本号!");
        }
        return super.beforeSave();
    }

    /**
     * 获取oa的各个版本信息和最新版本信息
     */
    @Service(url = "/oa/version/versionlog", method = HttpMethod.all)
    @Json
    public Map versionLog() throws Exception
    {
        if(project == null) return null;

        List<VersionInfo> versionInfoList = dao.getVersionInfoList(project);
        if (CollectionUtils.isNotEmpty(versionInfoList))
        {
            VersionInfo currVer = versionInfoList.get(0);
            Map<String, Object> data = new HashMap<String, Object>();
            //技术支持
            data.put("support", Tools.getMessage("version.versionlog.support"));
            //服务热线
            data.put("phone", Tools.getMessage("version.versionlog.phone"));
            //版本
            data.put("currVer", currVer.getVerNo());
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            List<Map<String, Object>> verMapList = new ArrayList<Map<String, Object>>();
            for (VersionInfo versionInfo : versionInfoList)
            {
                Map verMap = new HashMap();
                verMap.put("date", format.format(versionInfo.getCreateTime()));
                verMap.put("content", versionInfo.getPublishContent());
                verMap.put("verNo", versionInfo.getVerNo());
                verMapList.add(verMap);
            }
            data.put("verList", verMapList);
            return data;
        }
        return null;
    }

    /**
     * 查询当前用户是否需要推送版本信息
     */
    @Service(url = "/oa/version/checkpush", method = HttpMethod.all)
    public boolean checkPush() throws Exception
    {
        VersionPushUser userPush = dao.getPushUserByUserIdAndCode(userOnlineInfo.getUserId(),project);
        if (Null.isNull(userPush))
        {
            return false;
        }
        dao.delete(userPush);
        return true;
    }
}
