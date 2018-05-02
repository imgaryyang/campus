package com.gzzm.safecampus.campus.tutorship;

import com.gzzm.platform.commons.components.EntityPageListModel;

/**
 * 机构树
 *
 * @author yiuman
 * @date 2018/3/14
 */
public class InstitutionTree extends EntityPageListModel<TutorInstitution, Integer> {

    public InstitutionTree() {
    }

    @Override
    protected String getTextField() throws Exception {
        return "institutionName";
    }
}
