package com.gzzm.portal.interview.crud;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.interview.dao.ArticleDao;
import com.gzzm.portal.interview.entity.InterviewArticle;
import com.gzzm.portal.interview.enumtype.PublishState;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

/**
 * 在线访谈-文章信息维护
 *
 * @author lishiwei
 * @date 2016/7/28.
 */
@Service(url = "/portal/interview/crud/ArticleCrud")
public class ArticleCrud extends BaseNormalCrud<InterviewArticle, Integer>
{

    static
    {
        ParameterCheck.addNoCheckURL("/portal/interview/crud/ArticleCrud");
    }


    public ArticleCrud()
    {
        setLog(true);
        addOrderBy("orderId", OrderType.desc);
    }

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private ArticleDao dao;

    /**
     * url参数-在线访谈Id
     */
    private Integer interviewId;

    /**
     * 查询条件-文章标题
     */
    @Like
    private String title;

    /**
     * 查询条件-发布状态
     */
    private PublishState state;

    public Integer getInterviewId()
    {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId)
    {
        this.interviewId = interviewId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public PublishState getState()
    {
        return state;
    }

    public void setState(PublishState state)
    {
        this.state = state;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("文章标题", "title");
        view.addComponent("发布状态", "state");

        view.addColumn("文章标题", "title");
        view.addColumn("创建人", "creator.userName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("修改人", "modifyUser.userName");
        view.addColumn("修改时间", "modifyTime");
        view.addColumn("发布状态", "state");
        view.addColumn("发布人", "publishUser.userName");
        view.addColumn("发布时间", "publishTime");

        view.defaultInit();
        view.addButton("发布", "publishJS(1)");
        view.addButton("取消发布", "publishJS(0)");

        view.importJs("/portal/interview/article.js");
        return view;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return " interviewId=" + interviewId;
    }

    @Forward(page = "/portal/interview/article.ptl")
    @Override
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Forward(page = "/portal/interview/article.ptl")
    @Override
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setInterviewId(interviewId);
        getEntity().setState(PublishState.UNPUBLISH);
        getEntity().setCreatorId(userOnlineInfo.getUserId());
        getEntity().setModifyUserId(userOnlineInfo.getUserId());
        java.util.Date time = new java.util.Date();
        getEntity().setCreateTime(time);
        getEntity().setModifyTime(time);
        return super.beforeInsert();
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        getEntity().setModifyUserId(userOnlineInfo.getUserId());
        getEntity().setModifyTime(new java.util.Date());
        return super.beforeUpdate();
    }

    /**
     * 发布/取消发布
     *
     * @param state 0-未发布,1-已发布
     * @throws Exception
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public void publish(Integer state) throws Exception
    {
        if (state == 1)
        {
            dao.publish(getKeys());
        }
        if (state == 0)
        {
            dao.cancelPublish(getKeys());
        }
    }

}
