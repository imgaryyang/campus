package com.gzzm.portal.cms.information.search;

import com.gzzm.portal.annotation.*;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.information.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 全文检索标签
 *
 * @author sjy
 * @date 2018/3/12
 */
@Tag(name = "searchInfo")
public class SearchInfoListTag extends InformationListTag
{

    @Inject
    private SearchRecordDao dao;

    @Inject
    private SearchRecordService service;

    private List<ChannelCatalog> catalogs;

    private Integer stationId;


    private Integer channelCatalogId;

    private List<Integer> catalogChannelIds;

    @Override
    protected String getQueryString() throws Exception
    {
        return super.getQueryString();
    }

    @Override
    protected void loadList() throws Exception
    {
        List resultList = new ArrayList<SearchResult>();
        if (channelCatalogId == null)
        {
            String oql = "select i from Information i where i.valid=1 and (i.validTime is null or i.validTime>sysdate()) and contains(i.text,?text) " + " and( i.channelId in ?channelIds or i.channelId in ?catalogChannelIds ) ";
            oql += " and i.publishTime>?time_start and i.publishTime<?time_end and i.title like ?title and i.fileCode like ?fileCode and i.source like ?source ";
            oql += " order by ";
            Map<ChannelCatalog, List<Integer>> catalogMap = getCatalogMap();
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("text", getText());
            param.put("title", initLike(getTitle()));
            param.put("fileCode", initLike(getFileCode()));
            param.put("source", initLike(getSource()));
            param.put("time_start", getTime_start());
            param.put("time_end", getTime_end());
            List<Integer> paramChannelIds = getChannelIds();
            param.put("channelIds", paramChannelIds);
            if (paramChannelIds != null && paramChannelIds.size() > 0)
            {
                param.put("catalogChannelIds", catalogChannelIds);
            }
            else
            {
                param.put("catalogChannelIds", null);
            }
            if (catalogMap != null && catalogMap.size() > 0)
            {
                for (ChannelCatalog catalog : catalogMap.keySet())
                {
                    List<Integer> channelIds = catalogMap.get(catalog);
                    if (channelIds != null && channelIds.size() > 0)
                    {
                        param.put("order" + catalog.getCatalogId(), channelIds);
                        oql += " case when i.channelId in ?order" + catalog.getCatalogId() + " then 0 else 1 end ,";
                    }
                }
            }
            oql += " i.topmost desc,i.orderId desc,i.publishTime desc,i.informationId desc";
            dao.setPageSize(pageSize);
            dao.setPageNo(pageNo);
            List<Information> queryRes = dao.oqlQuery(oql, Information.class, param);
            totalCount = dao.getTotalCount();
            if (queryRes != null && queryRes.size() > 0)
            {
                List<Information> otherInfo = new ArrayList<Information>();
                List<Information> catalogInfos = new ArrayList<Information>();
                if (catalogMap != null && catalogMap.size() > 0)
                {
                    for (ChannelCatalog catalog : catalogMap.keySet())
                    {
                        List<Integer> channelIds = catalogMap.get(catalog);
                        for (Information info : queryRes)
                        {
                            Integer channelId = info.getChannelId();
                            if (channelIds.contains(channelId))
                            {
                                SearchResult searchResult = new SearchResult();
                                resultList.add(searchResult);
                                searchResult.setChannelCatalog(catalog);
                                searchResult.setInformation(info);
                                catalogInfos.add(info);
                            }
                        }
                    }
                }
                for (Information info : queryRes)
                {
                    if (!catalogInfos.contains(info))
                    {
                        otherInfo.add(info);
                    }
                }
                for (Information info : otherInfo)
                {
                    SearchResult searchResult = new SearchResult();
                    searchResult.setInformation(info);
                    resultList.add(searchResult);
                }
            }
        }
        else
        {
            ChannelCatalog catalog = dao.load(ChannelCatalog.class, channelCatalogId);
            List<Channel> relateChannels = catalog.getRelateChannels();
            if (relateChannels != null && relateChannels.size() > 0)
            {
                List<Integer> channelIds = new ArrayList<Integer>();
                for (Channel channel : relateChannels)
                {
                    channelIds.add(channel.getChannelId());
                }
                dao.setPageSize(pageSize);
                dao.setPageNo(pageNo);
                List<Information> queryRes = dao.queryInformationList(getText(), channelIds);
                totalCount = dao.getTotalCount();
                if (queryRes != null && queryRes.size() > 0)
                {
                    for (Information info : queryRes)
                    {
                        SearchResult searchResult = new SearchResult();
                        searchResult.setInformation(info);
                        resultList.add(searchResult);
                    }
                }
            }
        }
        if (resultList.size() > 0 && StringUtils.isNotBlank(getText()))
        {
            service.addSearchRecord(getText(), getStationId());
        }
        setList(resultList);
    }

    @Override
    protected Object transform(Information information) throws Exception
    {
        SearchResult result = (SearchResult) information;
        Information info = result.getInformation();
        SearchInformationInfo informationInfo = new SearchInformationInfo(info, null);
        informationInfo.setChannelCatalog(result.getChannelCatalog());
        informationInfo.setWords(getWords());
        return informationInfo;
    }

    private List<Integer> getCatalogChannelIds(ChannelCatalog catalog)
    {
        List<Channel> relateChannels = catalog.getRelateChannels();
        if (relateChannels != null && relateChannels.size() > 0)
        {
            List<Integer> channelIds = new ArrayList<Integer>();
            for (Channel channel : relateChannels)
            {
                channelIds.add(channel.getChannelId());
            }
            return channelIds;
        }
        return null;
    }

    private Map<ChannelCatalog, List<Integer>> getCatalogMap() throws Exception
    {
        List<ChannelCatalog> channelCatalogs = getCatalogs();
        if (channelCatalogs != null && channelCatalogs.size() > 0)
        {
            catalogChannelIds = new ArrayList<Integer>();
            Map<ChannelCatalog, List<Integer>> result = new HashMap<ChannelCatalog, List<Integer>>();
            for (ChannelCatalog catalog : channelCatalogs)
            {
                List<Integer> channelIds = new ArrayList<Integer>();
                result.put(catalog, channelIds);
                List<Channel> relateChannels = catalog.getRelateChannels();
                if (relateChannels != null && relateChannels.size() > 0)
                {
                    for (Channel channel : relateChannels)
                    {
                        Integer channelId = channel.getChannelId();
                        channelIds.add(channelId);
                        if (!catalogChannelIds.contains(channelId))
                        {
                            catalogChannelIds.add(channelId);
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    public List<ChannelCatalog> getCatalogs() throws Exception
    {
        if (catalogs == null)
        {
            catalogs = dao.queryChannelCatalogs(getStationId());
        }
        return catalogs;
    }

    private static String initLike(String param)
    {
        if (StringUtils.isBlank(param))
        {
            return null;
        }
        return "%" + param + "%";
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public Integer getChannelCatalogId()
    {
        return channelCatalogId;
    }

    public void setChannelCatalogId(Integer channelCatalogId)
    {
        this.channelCatalogId = channelCatalogId;
    }

    public List<Integer> getCatalogChannelIds()
    {
        return catalogChannelIds;
    }

}
