package com.gzzm.safecampus.campus.tutorship;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.Provider;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.BaseSimpleCell;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 课程Crud
 *
 * @author yiuman
 * @date 2018/3/14
 */
@Service(url = "/campus/tutorship/tutorcoursecrud")
public class TutorCourseCrud extends BaseNormalCrud<TutorCourse, Integer> {

    @Inject
    private Provider<TutorDao> daoProvider;

    @Like
    private String courseName;

    private Integer institutionId;

    private List<String> courseTypes;

    private List<Integer> items;

    private List<Integer> courseIds;

    private InstitutionTree institutionTree;

    private SubjectItemModel subjectItemModel;

    public TutorCourseCrud() {

    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        if(institutionId==0) institutionId =null;
        this.institutionId = institutionId;
    }

    public List<String> getCourseTypes() {
        return courseTypes;
    }

    public void setCourseTypes(List<String> courseTypes) {
        this.courseTypes = courseTypes;
    }

    public List<Integer> getItems() {
        return items;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }

    public List<Integer> getCourseIds() {
        if (courseIds != null) return courseIds;
        if (CollectionUtils.isNotEmpty(courseTypes)) {
            courseIds = (daoProvider.get().getCourseByItemIds(courseTypes));
        }
        return courseIds;
    }

    public void setCourseIds(List<Integer> courseIds) {
        this.courseIds = courseIds;
    }

    @Select(field = {"institutionId", "entity.institutionId"}, value = "institutionId", text = "entity.institution.institutionName")
    public InstitutionTree getInstitutionTree() throws Exception {
        if (institutionTree == null) institutionTree = Tools.getBean(InstitutionTree.class);
        return institutionTree;
    }

    @Select(field = {"courseTypes", "items"})
    public SubjectItemModel getSubjectItemModel() throws Exception {
        if (subjectItemModel == null) subjectItemModel = Tools.getBean(SubjectItemModel.class);
        return subjectItemModel;
    }

    public void setSubjectItemModel(SubjectItemModel subjectItemModel) {
        this.subjectItemModel = subjectItemModel;
    }

    @Override
    protected Object createListView() throws Exception {
        ComplexTableView view = new ComplexTableView(Tools.getBean(InstitutionDisplay.class),"institutionId");
        view.addComponent("课程名称", "courseName");
        view.addComponent("课程分类", "courseTypes");
        view.addColumn("课程名称", "courseName");
        view.addColumn("所属机构", "institution.institutionName");
        view.addColumn("课程分类", new BaseSimpleCell() {
            @Override
            public Object getValue(Object o) throws Exception {
                TutorCourse tutorCourse = (TutorCourse) o;
                return getTypeItemString(tutorCourse.getCourseId());
            }
        });
        view.defaultInit(false);
        return view;
    }

    @Override
    @Forward(page = "/safecampus/campus/turtor/course.ptl")
    public String add(String forward) throws Exception {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/safecampus/campus/turtor/course.ptl")
    public String show(Integer key, String forward) throws Exception {
        items = daoProvider.get().getItemsByCourseId(key);
        return super.show(key, forward);
    }

    @Override
    public void afterSave() throws Exception {
        if (CollectionUtils.isNotEmpty(items)) {
            List<CourseTypes> markses = daoProvider.get().getCourseTypessByCId(getEntity().getCourseId());
            if (CollectionUtils.isNotEmpty(markses)) daoProvider.get().deleteItemsByCourseId(getEntity().getCourseId());
            for (Integer ct : items) {
                CourseTypes item = new CourseTypes();
                item.setCourseId(getEntity().getCourseId());
                item.setTypeItemId(Integer.valueOf(ct));
                daoProvider.get().save(item);
            }
        }
        super.afterSave();
    }

    @Service
    public String getTypeItemString(Integer courseId) {
        StringBuilder stringBuilder = null;
        if (courseId != null) {
            List<CourseTypes> courseTypes = daoProvider.get().getCourseTypessByCId(courseId);
            if (CollectionUtils.isNotEmpty(courseTypes)) {
                stringBuilder = new StringBuilder();
                for (CourseTypes types : courseTypes) {
                    stringBuilder.append(types.getSubjectTypeItem().getTypeItemName()).append(",");
                }
            }
        }
        return stringBuilder == null ? "" : stringBuilder.toString().substring(0, stringBuilder.length() - 1);
    }

    @Override
    public void afterDelete(Integer key, boolean exists) throws Exception {
        daoProvider.get().deleteCourseTypes(key);
        super.afterDelete(key, exists);
    }
}
