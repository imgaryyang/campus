package com.gzzm.safecampus.campus.common;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.base.ClassesOwnedCrud;
import com.gzzm.safecampus.campus.classes.ClassesTreeDisplay;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.StudentDao;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.Align;
import net.cyan.nest.annotation.Inject;

/**
 * 学生选择窗口
 *
 * @author hmc
 * @date 18-02-07 10:46
 */
@Service(url = "/campus/common/studentselect")
public class StudentModelCrud extends ClassesOwnedCrud<Student, Integer>
{

    @Like
    private String studentNo;

    @Like
    private String studentName;

    public StudentModelCrud()
    {
        addOrderBy("orderId");
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new ClassesTreeDisplay(), "classesId");
        view.addComponent("学号", "studentNo");
        view.addComponent("姓名", "studentName");
        view.addColumn("学号", "studentNo");
        view.addColumn("姓名", "studentName");
        view.addColumn("性别", "sex");
        view.addColumn("班级", "classes.allName").setWidth("90").setAlign(Align.center);
        view.addButton(Buttons.query());
        view.addButton(Buttons.ok());
        view.importJs("/safecampus/campus/common/studentmodel.js");
        return view;
    }
}



