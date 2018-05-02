package com.gzzm.portal.interview.dao;

import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * 文章dao
 *
 * @author lishiwei
 * @date 2016/7/29.
 */
public abstract class ArticleDao extends GeneralDao {

    public ArticleDao() {
    }

    /**
     * 发布
     * @param articleId 文章ID
     * @throws Exception
     */
    @OQLUpdate("update InterviewArticle set publishTime=sysdate(),state=1 where articleId in :2 ")
    public abstract void publish(Integer[] articleId) throws Exception;

    /**
     * 取消发布
     * @param infoIds 文章ID
     * @throws Exception
     */
    @OQLUpdate("update InterviewArticle set publishTime=null,state=0 where infoId in :1")
    public abstract void cancelPublish(Integer[] infoIds) throws Exception;
}
