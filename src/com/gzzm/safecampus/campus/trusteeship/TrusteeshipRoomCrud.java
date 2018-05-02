package com.gzzm.safecampus.campus.trusteeship;


import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.campus.base.CHourMinute;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;

/**
 * 托管室管理CRUD
 */
@Service(url = "/campus/trusteeship/troom")
public class TrusteeshipRoomCrud extends BaseCrud<TrusteeshipRoom, Integer>
{
    /**
     * 托管室名称
     */
    @Like
    private String name;

    /**
     * 生活老师查询条件
     */
    @Like("teacher.teacherName")
    private String teacherName;

    public TrusteeshipRoomCrud()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("托管室名称", "name");
        view.addComponent("生活老师名称", "teacherName");

        view.addColumn("托管室名称", "name").setWidth("200px");
        view.addColumn("生活老师", "teacher.teacherName").setWidth("150px");
        view.addColumn("生活老师工号", "teacher.teacherNo").setWidth("150px");
        view.addColumn("联系电话", "teacher.phone").setWidth("150px");
        view.addColumn("托管时间段", "trusteeTime").setWidth("200px");
        view.addColumn("备注", "remark");
        view.defaultInit(false);
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("托管室名称", "name");
        view.addComponent("生活老师", "teacherId").setProperty("text", "${teacher.teacherName}");
        view.addComponent("托管时间起", new CHourMinute("trusteeStartHour", "trusteeStartMinute").setFormat("00", "00").setWidth("117px", "117px"));
        view.addComponent("托管时间止", new CHourMinute("trusteeEndHour", "trusteeEndMinute").setFormat("00", "00").setWidth("117px", "117px"));
        view.addComponent("备注", new CTextArea("remark"));
        view.addDefaultButtons();
        return view;
    }
}
