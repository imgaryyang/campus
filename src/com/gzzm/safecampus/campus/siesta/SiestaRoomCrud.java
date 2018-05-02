package com.gzzm.safecampus.campus.siesta;

import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.base.BaseCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;

/**
 * 午休室管理
 * Created by Huangmincong on 2018/3/12.
 */
@Service(url = "/campus/siesta/sroom")
public class SiestaRoomCrud extends BaseCrud<SiestaRoom, Integer>
{
    @Like
    private String name;

    @Like("teacher.teacherName")
    private String teacherName;

    public SiestaRoomCrud()
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
        view.addComponent("午休室名称", "name");
        view.addComponent("生活老师", "teacherName");
        view.addColumn("午休室名称", "name");
        view.addColumn("生活老师", "teacher");
        view.addColumn("联系电话", "teacher.phone");
        view.addColumn("床位数", "bedNum");
        view.addColumn("备注", "remark");
        view.defaultInit(false);
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("午休室名称", "name", true);
        view.addComponent("生活老师", "teacherId", true).setProperty("text", "${teacher.teacherName+[teacher.teacherNo]}");
        view.addComponent("床位数", "bedNum", true);
        view.addComponent("备注", new CTextArea("remark"), false);
        view.addDefaultButtons();
        return view;
    }
}