package com.gzzm.safecampus.campus.tutorship;

import com.gzzm.platform.commons.FileUploadService;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.ComplexTableView;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.Provider;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.BaseSimpleCell;
import net.cyan.nest.annotation.Inject;

import java.io.File;
import java.util.List;

/**
 * 辅导名师CRUD
 *
 * @author yiuman
 * @date 2018/3/14
 */

@Service(url = "/campus/tutorship/tutorteachercrud")
public class TutorTeacherCrud extends BaseNormalCrud<TutorTeacher, Integer> {

    @Inject
    private Provider<TutorDao> daoProvider;

    @Like
    private String teacherName;

    private Integer institutionId;

    private List<String> teacherCourses;

    private List<Integer> coures;

    private InstitutionTree institutionTree;

    private CourseModel courseModel;

    private List<Integer> teacherIds;

    /**
     * 上传的文件
     */
    protected InputFile file;

    @Inject
    protected static Provider<FileUploadService> uploadServiceProvider;

    public TutorTeacherCrud() {
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }


    public List<String> getTeacherCourses() {
        return teacherCourses;
    }

    public void setTeacherCourses(List<String> teacherCourses) {
        this.teacherCourses = teacherCourses;
    }

    public List<Integer> getCoures() {
        return coures;
    }

    public void setCoures(List<Integer> coures) {
        this.coures = coures;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        if (institutionId == 0) institutionId = null;
        this.institutionId = institutionId;
    }

    public InputFile getFile() {
        return file;
    }

    public void setFile(InputFile file) {
        this.file = file;
    }

    public List<Integer> getTeacherIds() {
        if (teacherIds != null) return teacherIds;
        if (CollectionUtils.isNotEmpty(teacherCourses)) {
            teacherIds = (daoProvider.get().getTeachersByItemIds(teacherCourses));
        }
        return teacherIds;
    }

    public void setTeacherIds(List<Integer> teacherIds) {
        this.teacherIds = teacherIds;
    }

    @Select(field = {"institutionId", "entity.institutionId"}, value = "institutionId", text = "entity.institution.institutionName")
    public InstitutionTree getInstitutionTree() throws Exception {
        if (institutionTree == null) institutionTree = Tools.getBean(InstitutionTree.class);
        return institutionTree;
    }

    @Select(field = {"teacherCourses", "coures"})
    public CourseModel getCourseModel() throws Exception {
        if (courseModel == null) courseModel = Tools.getBean(CourseModel.class);
        return courseModel;
    }

    public void setCourseModel(CourseModel courseModel) {
        this.courseModel = courseModel;
    }

    @Override
    protected Object createListView() throws Exception {
        ComplexTableView view = new ComplexTableView(Tools.getBean(InstitutionDisplay.class), "institutionId");
        view.addComponent("名师姓名", "teacherName");
        view.addComponent("辅导课程", "teacherCourses");
        view.addColumn("名师姓名", "teacherName");
        view.addColumn("所属机构", "institution.institutionName");
        view.addColumn("课程", new BaseSimpleCell() {
            @Override
            public Object getValue(Object o) throws Exception {
                TutorTeacher teacher = (TutorTeacher) o;
                return getTCourseById(teacher.getTeacherId());
            }
        });
        view.addColumn("微信", "weChat");
        view.addColumn("客服QQ", "qq");
        view.defaultInit(false);
        return view;
    }

    @Override
    @Forward(page = "/safecampus/campus/turtor/teacher.ptl")
    public String add(String forward) throws Exception {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/safecampus/campus/turtor/teacher.ptl")
    public String show(Integer key, String forward) throws Exception {
        coures = daoProvider.get().getCoursesIdByTId(key);
        return super.show(key, forward);
    }

    @Override
    public void afterSave() throws Exception {
        if (CollectionUtils.isNotEmpty(coures)) {
            List<Integer> markses = daoProvider.get().getCoursesIdByTId(getEntity().getTeacherId());
            if (CollectionUtils.isNotEmpty(markses)) daoProvider.get().deleteCourseTypesByTeacherId(getEntity().getTeacherId());
            for (Integer ct : coures) {
                TeacherCourse item = new TeacherCourse();
                item.setTeacherId(getEntity().getTeacherId());
                item.setCourseId(ct);
                daoProvider.get().save(item);
            }
        }
        super.afterSave();
    }

    @Override
    public void afterDelete(Integer key, boolean exists) throws Exception {
        daoProvider.get().deleteTeacherCourse(key);
        super.afterDelete(key, exists);
    }

    @Service
    public String getTCourseById(Integer teacherId) {
        StringBuilder stringBuilder = null;
        if (teacherId != null) {
            List<TeacherCourse> courseTypes = daoProvider.get().getTCoursesByTId(teacherId);
            if (CollectionUtils.isNotEmpty(courseTypes)) {
                stringBuilder = new StringBuilder();
                for (TeacherCourse types : courseTypes) {
                    stringBuilder.append(types.getCourse().getCourseName()).append(",");
                }
            }
        }
        return stringBuilder == null ? "" : stringBuilder.toString().substring(0, stringBuilder.length() - 1);
    }

    @Service(url = "/campus/tutorship/teacherimage/{$0}")
    public byte[] getTeacherImage(Integer teacherId) throws Exception {
        if (teacherId == null)
            return new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/teacher.jpg"))).getBytes();
        TutorTeacher teacher = getEntity(teacherId);
        return teacher.getPicture() == null ? new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/teacher.jpg"))).getBytes() : teacher.getPicture().getBytes();
    }


}
