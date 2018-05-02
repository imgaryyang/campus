package com.gzzm.safecampus.campus.tutorship;


import com.gzzm.platform.commons.crud.BaseQueryCrud;
import com.gzzm.platform.commons.crud.SelectableListView;

/**
 * 机构左树
 *
 * @author yiuman
 * @date 2018/3/19
 */
public class InstitutionDisplay extends BaseQueryCrud<TutorInstitution, Integer> {


    @Override
    protected Object createListView() throws Exception {
        return new SelectableListView();
    }

    @Override
    protected void afterQuery() throws Exception {
        TutorInstitution tutorInstitution = new TutorInstitution();
        tutorInstitution.setInstitutionName("全部机构");
        tutorInstitution.setInstitutionId(0);
        getList().add(0, tutorInstitution);
    }

}
