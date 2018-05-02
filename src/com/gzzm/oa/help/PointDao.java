package com.gzzm.oa.help;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

/**
 *
 * @author ljb
 * @date 2017/3/31 0031
 */
public abstract class PointDao extends GeneralDao {
    public PointDao() {
    }

    /**
     * 查询公开发布的系统消息
     * @param type
     * @return
     * @throws Exception
     */
    @OQL("select  p from Pointion p where p.type=:1  and p.publicTag=:2 order by p.validTime desc limit 1")
    public abstract Pointion getPointionByType(String type,boolean tag)throws Exception;
}
