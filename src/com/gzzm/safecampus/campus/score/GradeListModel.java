package com.gzzm.safecampus.campus.score;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.safecampus.campus.classes.Grade;
import com.gzzm.safecampus.campus.classes.GradeDao;
import net.cyan.arachne.components.CheckBoxListModel;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * @author yiuman
 * @date 2018/3/15
 */
public class GradeListModel implements CheckBoxListModel<Grade>
{

    @Inject
    protected static Provider<UserOnlineInfo> userOnlineInfoProvider;

    @Inject
    private GradeDao gradeDao;

    private Boolean checkBox;

    public GradeListModel()
    {
    }

    public Boolean getCheckBox()
    {
        return checkBox;
    }

    public void setCheckBox(Boolean checkBox)
    {
        this.checkBox = checkBox;
    }

    @Override
    public Collection<Grade> getItems() throws Exception
    {
        return gradeDao.getChildren(userOnlineInfoProvider.get().getDeptId());
    }

    @Override
    public String getId(Grade grade) throws Exception
    {
        return grade.getGradeId().toString();
    }

    @Override
    public String toString(Grade grade) throws Exception
    {
        return grade.toString();
    }

    @Override
    public boolean hasCheckBox(Grade grade) throws Exception
    {
        return checkBox == null ? true : checkBox;
    }

    @Override
    public Boolean isChecked(Grade grade) throws Exception
    {
        return true;
    }
}
