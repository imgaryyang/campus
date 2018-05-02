package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.crud.SystemCrudUtils;
import com.gzzm.platform.log.LogAction;
import com.gzzm.platform.organ.Dept;
import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.commons.PageCache;
import com.gzzm.portal.cms.station.Station;
import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.commons.transaction.Transactional;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.Date;
import java.util.List;

/**
 * 信息采编和信息展示相关的数据访问对象
 *
 * @author camel
 * @date 2011-5-21
 */
public abstract class InformationDao extends GeneralDao
{
    public InformationDao()
    {
    }

    public Dept getDept(Integer deptId) throws Exception
    {
        return load(Dept.class, deptId);
    }

    public Information getInformation(Long informationId) throws Exception
    {
        return load(Information.class, informationId);
    }

    public InformationEdit getInformationEdit(Long informationId) throws Exception
    {
        return load(InformationEdit.class, informationId);
    }

    public InformationFile getInformationFile(Long informationId) throws Exception
    {
        return load(InformationFile.class, informationId);
    }

    public InformationFileEdit getInformationFileEdit(Long informationId) throws Exception
    {
        return load(InformationFileEdit.class, informationId);
    }

    public Station getStation(Integer stationId) throws Exception
    {
        return load(Station.class, stationId);
    }

    public Channel getChannel(Integer channelId) throws Exception
    {
        return load(Channel.class, channelId);
    }

    public PageTemplate getPageTemplate(Integer templateId) throws Exception
    {
        return load(PageTemplate.class, templateId);
    }

    /**
     * 通过栏目编码获得栏目
     *
     * @param channelCode 栏目编码
     * @return 栏目对象
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from Channel c where channelCode=:1 and deleteTag=0")
    public abstract Channel getChannelByCode(String channelCode) throws Exception;

    public InformationCatalog getRootCatalog() throws Exception
    {
        InformationCatalog catalog = load(InformationCatalog.class, 0);

        if (catalog == null)
        {
            catalog = new InformationCatalog();
            catalog.setCatalogId(0);
            catalog.setCatalogName("根节点");
            add(catalog);
        }

        return catalog;
    }

    public InformationCatalog getCatalog(Integer catalogId) throws Exception
    {
        return load(InformationCatalog.class, catalogId);
    }

    @OQL("select c from InformationCatalog c where c.catalogName like ?")
    public abstract List<InformationCatalog> searchCatalog(String catalogName) throws Exception;

    @Transactional
    public void deleteInformation(Long informationId) throws Exception
    {
        Information information = load(Information.class, informationId);
        if(information!=null){
            SystemCrudUtils.saveLog(information, LogAction.delete, null, null);
        }
        delete(Information.class, informationId);

        SystemCrudUtils.saveLog(load(InformationEdit.class, informationId), LogAction.delete, null, null);
        delete(InformationEdit.class, informationId);

        PageCache.updateCache();
    }

    /**
     * 更新文章的阅读次数
     *
     * @param informationId 文章id
     * @throws Exception 更新数据库错误
     */
    @Transactional
    public void updateReadTimes(Long informationId) throws Exception
    {
        lock(Information.class, informationId);

        InformationReadTimes readTimes = load(InformationReadTimes.class, informationId);

        if (readTimes == null)
        {
            readTimes = new InformationReadTimes();
            readTimes.setInformationId(informationId);
            readTimes.setReadTimes(1);

            add(readTimes);
        }
        else
        {
            Integer count = readTimes.getReadTimes();
            if (count == null)
                count = 1;
            else
                count++;

            readTimes.setReadTimes(count);
            update(readTimes);
        }
    }

    /**
     * 查询某个栏目中的信息
     *
     * @param channelId 栏目ID
     * @param orderBy   排序字段
     * @param photo     是否只查询有标题图片的文章
     * @param limit     限制条数
     * @return 栏目中的信息列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select i from Information i where i.channelId=:1 and valid=1 and (validTime is null or validTime>sysdate()) " +
            "$this.3{and i.photo is not null} order by @this.2 $this.4{limit :4}")
    public abstract List<Information> queryInformationsInChannel(Integer channelId, String orderBy, boolean photo,
                                                                 Integer limit) throws Exception;

    /**
     * 查询本栏目及子栏目的信息
     *
     * @param channelId 栏目ID
     * @param orderBy   排序字段
     * @param photo     是否只查询有标题图片的文章
     * @param limit     限制条数
     * @return 栏目中的信息列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select i from Information i join Channel c on channel.leftValue>=c.leftValue " +
            "and channel.leftValue<c.rightValue where c.channelId=:1 and c.deleteTag=0 " +
            "and valid=1 and (validTime is null or validTime>sysdate()) " +
            "$this.3{and i.photo is not null} order by @this.2 $this.4{limit :4}")
    public abstract List<Information> queryInformationsInDescendantChannels(Integer channelId, String orderBy,
                                                                            boolean photo, Integer limit)
            throws Exception;

    /**
     * 获得某条信息在同一个栏目下的下一条信息
     *
     * @param informationId 当前信息的ID
     * @return 下一条信息的id
     * @throws Exception 数据库查询错误
     */
    @OQL("select i.informationId from Information i,Information i0 where i0.informationId=:1 " +
            "and i.channelId=i0.channelId and (i.orderId,i.updateTime,i.informationId)>" +
            "(i0.orderId,i0.updateTime,i0.informationId) and i.valid=1 and (i.validTime is null " +
            "or i.validTime>sysdate()) order by i.orderId,i.updateTime,i.informationId limit 1")
    public abstract Long getNextInformationId(Long informationId) throws Exception;

    /**
     * 获得某条信息在同一个栏目下的上一条信息
     *
     * @param informationId 当前信息的ID
     * @return 上一条信息的id
     * @throws Exception 数据库查询错误
     */
    @OQL("select i.informationId from Information i,Information i0 where i0.informationId=:1" +
            " and i.channelId=i0.channelId and (i.orderId,i.updateTime,i.informationId)<" +
            "(i0.orderId,i0.updateTime,i0.informationId) and i.valid=1 and (i.validTime is null " +
            "or i.validTime>sysdate()) order by i.orderId desc,i.updateTime desc,i.informationId desc limit 1")
    public abstract Long getPrevInformationId(Long informationId) throws Exception;

    /**
     * 根据时间获得应该适当的排序号
     *
     * @param channelId   栏目ID
     * @param publishTime 发布时间
     * @return 合适的排序号，为在这个时间之前发布的信息的最小排序号
     * @throws Exception
     */
    @OQL("select max(orderId)+1 from Information where channelId=:1 and publishTime<=:2")
    public abstract Integer getOrderIdByPublishTime(Integer channelId, Date publishTime) throws Exception;

    @LoadByKey
    public abstract SimpleInformation getSimpleInformation(Long informationId) throws Exception;

    @GetByField("mainInformationId")
    public abstract List<InformationEdit> getInformationEditsByMainInformationId(Long mainInformationId)
            throws Exception;

    @GetByField("sourceInformationId")
    public abstract List<InformationEdit> getInformationEditsBySourceInformationId(Long mainInformationId)
            throws Exception;

    @LoadByKey
    public abstract OrgCode getOrgCode(Integer deptId) throws Exception;

    /**
     * 删除信息采编关联
     * @param mainInfoId
     * @param otherInfoIds
     * @return
     * @throws Exception
     */
    @OQLUpdate("delete from com.gzzm.portal.cms.information.InformationEditRelated where informationId=:1 and otherInformationId in :2")
    public abstract void deleteInfoEditRelated(Long mainInfoId, Long[] otherInfoIds) throws Exception;

    @OQLUpdate("delete from com.gzzm.portal.cms.information.InformationRelated where informationId=:1 and otherInformationId in :2")
    public abstract void deleteInfoRelated(Long mainInfoId, Long[] otherInfoIds) throws Exception;

    @OQLUpdate("delete from com.gzzm.portal.cms.information.InformationEditRelated where informationId=:1")
    public abstract void deleteAllInfoRelated(Long mainInfoId) throws Exception;

    @OQLUpdate("update com.gzzm.portal.cms.information.InformationEditRelated set orderId=:3 where informationId=:1 and otherInformationId = :2")
    public abstract void updateInfoRelated(Long mainInfoId, Long otherInfoId,Integer orderId) throws Exception;

    @GetByField("informationId")
    public abstract List<InformationEditRelated> getInformationEditRelated(Long informationId) throws Exception;
}
