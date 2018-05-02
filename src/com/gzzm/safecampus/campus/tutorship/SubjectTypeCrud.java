package com.gzzm.safecampus.campus.tutorship;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;

/**
 * 课程类目CRUD
 *
 * @author yiuman
 * @date 2018/3/14
 */

@Service(url = "/campus/tutorship/subjecttypecrud")
public class SubjectTypeCrud extends BaseNormalCrud<TutorSubjectType, Integer> {

    @Like
    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("分类名称", "typeName");
        view.addColumn("分类名称", "typeName");
        view.addColumn("分类编码", "typeNo");
        view.defaultInit(false);
        return view;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("分类名称", "typeName",true);
        view.addComponent("分类编码", "typeNo");
        view.addDefaultButtons();
        return view;
    }
}
