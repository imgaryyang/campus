package com.gzzm.oa.help;

import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.cms.information.Information;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.DateUtils;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 提示信息服务类
 *
 * @author LDP
 * @date 2017/3/23
 */
@Service
public class PromptService {
    @Inject
    private PointDao pointDao;

    public PromptService() {
    }

    /**
     * 根据栏目编码，获得该栏目最新一条有效信息文本
     */
    @Service(url = "/oa/help/prompt/{$0}/pointinfo")
    public Map<String, Object> getValidPointInfo(String type) throws Exception {
        if (type == null) return null;

        Pointion pointion = pointDao.getPointionByType(type,true);
        if (pointion == null) return null;
        if (DateUtils.getMinutesInterval(new Date(System.currentTimeMillis()), pointion.getValidTime()) >= 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", pointion.getTitle());
            map.put("content", pointion.getPointContent());
            return map;
        }

        return null;
    }
}
