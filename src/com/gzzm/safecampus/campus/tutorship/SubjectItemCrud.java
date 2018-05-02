package com.gzzm.safecampus.campus.tutorship;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 课程分类Crud
 *
 * @author yiuman
 * @date 2018/3/14
 */

@Service(url = "/campus/tutorship/subjectitemcrud")
public class SubjectItemCrud extends BaseNormalCrud<TutorSubjectTypeItem, Integer> {

    @Like
    private String typeItemName;

    private Integer typeId;

    private SubjectTree subjectTree;

    public String getTypeItemName() {
        return typeItemName;
    }

    public void setTypeItemName(String typeItemName) {
        this.typeItemName = typeItemName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public void setSubjectTree(SubjectTree subjectTree) {
        this.subjectTree = subjectTree;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("分类名称", "typeItemName");
        view.addComponent("所属类目", "typeId");
        view.addColumn("分类名称", "typeItemName");
        view.addColumn("分类编码", "typeItemNo");
        view.addColumn("所属类目", "subjectType.typeName");
        view.defaultInit(false);
        return view;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("分类名称", "typeItemName",true);
        view.addComponent("分类编码", "typeItemNo");
        view.addComponent("所属类目", "typeId",true).setProperty("text","${subjectType.typeName}");
        view.addDefaultButtons();
        return view;
    }

    @Select(field = {"typeId", "entity.typeId"}, value = "typeId", text = "typeName")
    public SubjectTree getSubjectTree() throws Exception {

        if (subjectTree == null) subjectTree = Tools.getBean(SubjectTree.class);

        return subjectTree;
    }
}
