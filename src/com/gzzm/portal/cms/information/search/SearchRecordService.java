package com.gzzm.portal.cms.information.search;

import com.gzzm.portal.commons.PortalUtils;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/3/6
 */
@Service
public class SearchRecordService
{
    @Inject
    private SearchRecordDao dao;

    public void addSearchRecord(String keyword, Integer stationId) throws Exception
    {
        SearchRecord record = new SearchRecord();
        record.setStationId(stationId);
        record.setKeyword(keyword);
        record.setIp(PortalUtils.getIp(RequestContext.getContext().getRequest()));
        record.setSearchTime(new Date());
        dao.save(record);
    }

    @Transactional
    public void createHotWord() throws Exception
    {
        List<Integer> stationIds = dao.queryStationIds();
        if (stationIds != null && stationIds.size() > 0)
        {
            dao.setPageSize(20);
            for (Integer stationId : stationIds)
            {
                List<QueryHotWordBean> queryResults = dao.queryHotWord(stationId, DateUtils.addDate(new Date(), -30));
                if (queryResults != null && queryResults.size() > 0)
                {
                    dao.deleteStationHotWord(stationId);
                    Integer maxOrderId = dao.queryMaxOrderId(stationId);
                    if (maxOrderId == null)
                    {
                        maxOrderId = 0;
                    }
                    List<String> words = new ArrayList<String>();
                    for (QueryHotWordBean result : queryResults)
                    {
                        String keyword = result.getKeyword();
                        for (String w : words)
                        {
                            // 如w是aaa keyword是aa，则移除aaa
                            if (w.contains(keyword))
                            {
                                words.remove(w);
                                break;
                            }
                            //如w是aa keyword是aaa，则忽略 keyword
                            else if (keyword.contains(w))
                            {
                                keyword = null;
                                break;
                            }
                        }
                        if (keyword != null)
                        {
                            words.add(keyword);
                        }
                    }
                    for (String word : words)
                    {
                        InfoHotWord hotWord = new InfoHotWord();
                        hotWord.setSource(HotWordSource.AUTO);
                        hotWord.setStationId(stationId);
                        hotWord.setKeyword(word);
                        hotWord.setOrderId(++maxOrderId);
                        dao.save(hotWord);
                    }
                }
            }
        }
    }
}
