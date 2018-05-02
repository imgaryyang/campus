package com.gzzm.ods.documenttemplate;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 选择文件模板的页面
 *
 * @author camel
 * @date 11-9-25
 */
@Service
public class DocumentTemplatePage
{
    @Inject
    private DocumentTemplateService service;

    /**
     * 当前用户信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 当前部门
     */
    private Integer deptId;

    private Integer templateId;

    public DocumentTemplatePage()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    @Select(field = "templateId")
    @NotSerialized
    public List<DocumentTemplate> getDocumentTemplates() throws Exception
    {
        return service.getDocumentTemplates(deptId, userOnlineInfo);
    }

    @Service(url = "/ods/documenttemplate/select")
    public String show()
    {
        return "select";
    }
}
