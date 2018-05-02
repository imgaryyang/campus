package com.gzzm.portal.interview.crud;

import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.portal.commons.StationOwnedCrud;
import com.gzzm.portal.interview.dao.InterviewDao;
import com.gzzm.portal.interview.entity.Interview;
import com.gzzm.portal.interview.entity.InterviewType;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lk
 * @date 13-10-8
 */
@Service(url = "/portal/interview/InterviewCrud")
public class InterviewCrud extends StationOwnedCrud<Interview, Integer> {
    @Override
    public String getOrderField() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(new Integer[]{1, 2}));
        return "orderId";
    }

    @Inject
    private InterviewDao dao;

    private List<InterviewType> types;

    private boolean deletePhoto;

    @Like
    private String title;

    private Interview interview;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDeletePhoto() {
        return deletePhoto;
    }

    public void setDeletePhoto(boolean deletePhoto) {
        this.deletePhoto = deletePhoto;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public InterviewCrud(){
        setLog(true);
    }
    @NotSerialized
    @Select(field = "entity.typeId")
    public List<InterviewType> getTypes() {
        if (types == null) {
            types = dao.getAllTypes();
        }
        return types;
    }

    @ObjectResult
    @Service()
    public String getStationName(int typeId) {
        return dao.getStationName(typeId);
    }

    @Service(method = HttpMethod.post)
    public void publish(boolean p) throws Exception {
        int state = p ? 1 : 0;
        for (int key : getKeys()) {
            Interview entity = getEntity(key);
            entity.setState(state);
            saveEntity(entity, false);
        }
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();

        view.addColumn("标题", "title");
        view.addColumn("开始时间", "startTime");
        view.addColumn("结束时间", "endTime");
        view.addColumn("嘉宾", "guestName");
        view.addColumn("主持人", "hostName");
        view.addColumn("状态", "state==0?'未发布':'已发布'");
       /* view.addColumn("主持人直播", new CHref("主持人直播").setAction( "hostLiveJS(${interviewId})"));
        view.addColumn("嘉宾直播", new CHref("嘉宾直播").setAction("guestLiveJS(${interviewId})"));*/

        view.addComponent("标题", "title");
        view.defaultInit();
        view.addButton("发布", "publish(true)");
        view.addButton("取消发布", "publish(false)");

        view.importJs("/portal/interview/interview.js");
        view.importJs("/portal/interview/interview_show.js");
        return view;
    }

    @Forward(page = "/portal/interview/interview.ptl")
    @Override
    public String add(String forward) throws Exception {
        return super.add(forward);
    }

    @Forward(page = "/portal/interview/interview.ptl")
    @Override
    public String show(Integer key, String forward) throws Exception {
        return super.show(key, forward);
    }

    @Service(url = "/portal/interview/InterviewCrud/photo/{$0}")
    public byte[] getPhoto(int interviewId) throws Exception {
        return getEntity(interviewId).getPhoto();
    }

    /**
     * 新增时 保存stationid
     * @throws Exception 数据库异常
     */
    @Override
    public boolean beforeInsert() throws Exception {
        getEntity().setStationId(dao.getStation(getEntity().getTypeId()).getStationId());
        return super.beforeSave();
    }

    @Override
    public boolean beforeUpdate() throws Exception {
        //删除图片
        Interview entity = getEntity();
        if (entity.getPhoto() == null && deletePhoto) {
            entity.setPhoto(Null.ByteArray);
        }

        return true;
    }
}
