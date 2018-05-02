package com.gzzm.safecampus.campus.base;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.DeptOwnedNormalCrud;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.safecampus.campus.classes.TeacherListModel;
import com.gzzm.safecampus.campus.common.Constants;
import com.gzzm.safecampus.campus.common.Importable;
import com.gzzm.safecampus.campus.common.SchoolYearId;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

/**
 * 校园管理基础crud
 *
 * @author zy
 * @date 2018/3/15 17:51
 */
@Service
public class BaseCrud<E extends BaseBean, K> extends DeptOwnedNormalCrud<E, K> implements Importable
{
    //查询条件 ：学年
    @SchoolYearId
    protected Integer schoolYearId;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    private TeacherListModel teacherListModel;

    /**
     * 是否显示下载导入模板 默认都显示
     */
    protected boolean showTemplate;

    /**
     * 模板路径，子类实现
     */
    protected String templatePath;

    public BaseCrud()
    {
        setLog(true);
    }

    public boolean isShowTemplate()
    {
        //templatePath不为空就显示下载模板
        return StringUtils.isNotEmpty(getTemplatePath());
    }

    public void setShowTemplate(boolean showTemplate)
    {
        this.showTemplate = showTemplate;
    }

    public String getTemplatePath()
    {
        return templatePath;
    }

    public void setTemplatePath(String templatePath)
    {
        this.templatePath = templatePath;
    }

    public Integer getSchoolYearId()
    {
        return schoolYearId;
    }

    public void setSchoolYearId(Integer schoolYearId)
    {
        this.schoolYearId = schoolYearId;
    }

    @Select(field = {"teacherId", "entity.teacherId", "entity.masterId"})
    public TeacherListModel getTeacherListModel() throws Exception
    {
        if (teacherListModel == null)
            teacherListModel = Tools.getBean(TeacherListModel.class);
        return teacherListModel;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setDeptId(userOnlineInfo.getDeptId());
        return super.beforeInsert();
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
        setDefaultDeptId();
    }

    @Override
    @Forward(page = Constants.IMP_PAGE)
    public String showImp() throws Exception
    {
        return super.showImp();
    }
}