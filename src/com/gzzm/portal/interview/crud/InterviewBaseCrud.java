package com.gzzm.portal.interview.crud;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.portal.interview.dao.InterviewBaseDao;
import com.gzzm.portal.interview.entity.Interview;
import com.gzzm.portal.interview.entity.InterviewType;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CHref;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 在线访谈-基础crud
 *
 * @author lishiwei
 * @date 2016/7/28.
 */
@Service(url = "/portal/interview/crud/InterviewBaseCrud")
public class InterviewBaseCrud extends BaseNormalCrud<Interview, Integer> {

    public InterviewBaseCrud() {
        setLog(true);
        addOrderBy("startTime", OrderType.desc);
    }

    /**
     * url参数-入口
     */
    private String operation;

    /**
     * 查询条件-在线访谈标题
     */
    @Like
    protected String title;

    /**
     * 查询条件-类别
     */
    protected Integer typeId;

    /**
     * 查询条件-嘉宾
     */
    @Like
    protected String guestName;

    @Inject
    private InterviewBaseDao baseDao;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView(false);

        view.addColumn("标题", "title");
        view.addColumn("开始时间", "startTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("嘉宾", "guestName");
        view.addColumn("主持人", "guestName");
        view.addColumn("状态", "state==0?'未发布':'已发布'");
        view.addColumn("类别", "type.typeName");

        view.addComponent("标题", "title");
        view.addComponent("嘉宾", "guestName");
        view.addComponent("类别", "typeId");
        view.addButton(Buttons.query());

        //添加action特性视图
        addListView(view);

        view.importJs("/portal/interview/interview_show.js");
        return view;
    }

    /**
     * 根据operation 添加特性视图
     *
     * @param view PageTableView 视图
     */
    protected void addListView(PageTableView view) {
        //在线访谈维护界面
        if (operation.equals("interview")) {
            view.defaultInit();
            view.addButton("发布", "publish(true)");
            view.addButton("取消发布", "publish(false)");

            view.importJs("/portal/interview/interview.js");
        }
        //留言维护界面
        if (operation.equals("discuss")) {
            view.addColumn("操作",new CHref("留言维护").setAction("discussPage(${interviewId})"));
        }
        //图片维护
        if (operation.equals("photo")) {
            view.addColumn("操作",new CHref("图片维护").setAction("photoPage(${interviewId})"));
        }
        //文章维护
        if (operation.equals("article")) {
            view.addColumn("操作",new CHref("文章维护").setAction("articlePage(${interviewId})"));
        }
    }

    @Select(field = "typeId")
    public Map<Integer, String> getTypeDesc() throws Exception {
        List<InterviewType> types = baseDao.getInterviewTypes();
        if (types == null || types.size() == 0) {
            return null;
        }
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (InterviewType type : types) {
            map.put(type.getTypeId(), type.getTypeName());
        }
        return map;
    }

    @Override
    public String getOrderField() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(new Integer[]{1, 2}));
        return "orderId";
    }
}
