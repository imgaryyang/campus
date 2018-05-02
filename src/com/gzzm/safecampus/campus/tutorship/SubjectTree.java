package com.gzzm.safecampus.campus.tutorship;

import com.gzzm.platform.commons.components.EntityPageListModel;

/**
 * 类目树
 *
 * @author yiuman
 * @date 2018/3/14
 */
public class SubjectTree extends EntityPageListModel<TutorSubjectType, Integer> {


    public SubjectTree() {
    }

    @Override
    protected String getTextField() throws Exception {
        return "typeName";
    }


}
