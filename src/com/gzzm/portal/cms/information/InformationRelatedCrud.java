package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.channel.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/1/29
 */
@Service(url = "/portal/informationRelated")
public class InformationRelatedCrud extends BaseNormalCrud<InformationEdit, Long>
{
    @Inject
    private InformationDao dao;

    private Long mainInfoId;

    private InformationEdit mainInfo;

    @Like
    private String title;

    private Boolean related;

    private List<InformationEdit> relatedInfoList;

    @NotCondition
    private Integer channelId;

    public InformationRelatedCrud()
    {
        addOrderBy("updateTime desc");
    }

    private InformationEdit getMainInfo() throws Exception
    {
        if (mainInfo == null)
        {
            mainInfo = dao.load(InformationEdit.class, mainInfoId);
        }
        return mainInfo;
    }

    /**
     * 关联或取消关联
     * @param relate true是关联，false是取消关联
     * @throws Exception
     */
    @Transactional
    @Service
    @ObjectResult
    public void triggerRelate(boolean relate) throws Exception
    {
        Long[] keys = getKeys();
        if (keys != null && keys.length > 0)
        {
            if (!relate)
            {
                dao.deleteInfoEditRelated(mainInfoId, keys);
                dao.deleteInfoRelated(mainInfoId, keys);
            }
            else
            {
                boolean existInformation = dao.load(Information.class, mainInfoId) != null;
                for (Long key : keys)
                {
                    InformationEditRelated r = new InformationEditRelated();
                    r.setInformationId(mainInfoId);
                    r.setOtherInformationId(key);
                    dao.save(r);
                    if (existInformation)
                    {
                        InformationRelated info = new InformationRelated();
                        info.setInformationId(mainInfoId);
                        info.setOtherInformationId(key);
                        dao.save(info);
                    }
                }
            }
        }
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String res = "state=1 and informationId <> :mainInfoId ";
        if (channelId != null && !Null.isNull(channelId))
        {
            //查询此栏目的所有子栏目
            res += " and channelId in (select c.channelId from com.gzzm.portal.cms.channel.Channel c,com.gzzm.portal.cms.channel.Channel cc" + " where c.leftValue>=cc.leftValue and c.leftValue < cc.rightValue and cc.channelId=:channelId)";
        }

        if (related != null && Null.Boolean != related)
        {

            if (related)
            {
                res += " and informationId in (select r.otherInformationId from com.gzzm.portal.cms.information.InformationEditRelated r where r.informationId=:mainInfoId)";
            }
            else
            {
                res += " and informationId not in (select r.otherInformationId from com.gzzm.portal.cms.information.InformationEditRelated r where r.informationId=:mainInfoId)";
            }
        }
        return res;
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();
        if (relatedInfoList == null)
        {
            mainInfo = getMainInfo();
            relatedInfoList = mainInfo.getRelatedInfos();
        }
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(Tools.getBean(ChannelDisplay.class), "channelId", true);
        view.setTitle(getMainInfo().getTitle() + "-信息关联");
        view.addComponent("标题", "title");
        view.addComponent("关联状态", new CCombox("related", new String[][]{new String[]{"false", "未关联"}, new String[]{"true", "已关联"}}));
        view.addColumn("所属栏目", "channel.channelName").setWidth("100px");
        view.addColumn("标题", "title");
        view.addColumn("采编人", "createUser.userName");
        view.addColumn("预览", new CHref("预览").setAction("preview(${informationId})")).setWidth("40");
        view.addColumn("关联状态", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                InformationEdit edit = (InformationEdit) entity;
                if (relatedInfoList != null && relatedInfoList.size() > 0)
                {
                    for (InformationEdit info : relatedInfoList)
                    {
                        if (info.getInformationId().equals(edit.getInformationId()))
                        {
                            return "<button onclick='deRelate(" + edit.getInformationId() + ")'>已关联</button>";
                        }
                    }
                }
                return "<button onclick='relate(" + edit.getInformationId() + ")'>未关联</button>";
            }
        }).setWidth("65");
        view.addButton(Buttons.query());
        view.addButton(new CButton("关联", "relateInfos()"));
        view.addButton(new CButton("取消关联", "deRelateInfos()"));
        view.importJs("/portal/cms/information/relate.js");
        return view;
    }

    public Long getMainInfoId()
    {
        return mainInfoId;
    }

    public void setMainInfoId(Long mainInfoId)
    {
        this.mainInfoId = mainInfoId;
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Boolean getRelated()
    {
        return related;
    }

    public void setRelated(Boolean related)
    {
        this.related = related;
    }
}
