package com.gzzm.portal.interview.crud;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.interview.entity.InterviewPhoto;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CFile;
import net.cyan.crud.view.components.CImage;
import net.cyan.nest.annotation.Inject;

/**
 * 在线访谈-图片维护
 *
 * @author lishiwei
 * @date 2016/7/28.
 */
@Service(url = "/portal/interview/crud/PhotoCrud")
public class PhotoCrud extends BaseNormalCrud<InterviewPhoto, Integer> {

    public PhotoCrud() {
        setLog(true);
        addOrderBy("orderId", OrderType.desc);
    }

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * url参数-在线访谈Id
     */
    private Integer interviewId;

    /**
     * 查询条件-图片标题
     */
    @Like
    private String title;

    public Integer getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Integer interviewId) {
        this.interviewId = interviewId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("图片标题", "title");

        view.addColumn("图片标题", "title");
        view.addColumn("上传人", "creator.userName");
        view.addColumn("上传时间", "createTime");
        view.addColumn("预览", new CImage("/portal/interview/crud/PhotoCrud/photo/${photoId}").setHeight("120px")).setWidth("360px");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();
        view.setPage("double");

        view.addComponent("图片标题", "title").setWidth("200");
        view.addComponent("图片", new CFile("photo")).setFileType("$image");
        view.addComponent("预览",
                new CImage(getEntity().getPhoto() == null ? "" : "/portal/interview/crud/PhotoCrud/photo/" + getEntity().getPhotoId())
                        .setProperty("class", "photo"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    protected String getComplexCondition() throws Exception {
        return " interviewId=?interviewId ";
    }

    @Override
    public boolean beforeInsert() throws Exception {
        getEntity().setInterviewId(interviewId);
        getEntity().setCreatorId(userOnlineInfo.getUserId());
        getEntity().setCreateTime(new java.util.Date());
        return super.beforeInsert();
    }

    @Service(url = "/portal/interview/crud/PhotoCrud/photo/{$0}")
    public byte[] getPhoto(int photoId) throws Exception {
        return getEntity(photoId).getPhoto();
    }
}
