package com.gzzm.platform.form;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;

import java.util.*;

/**
 * 表单内容管理，可以修改表单中的意见
 *
 * @author camel
 * @date 13-12-10
 */
@Service
public class FormControlPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Long bodyId;

    @NotSerialized
    private SystemFormContext context;

    private String title;

    @NotSerialized
    private List<ParallelTextItem.Bak> baks;

    public FormControlPage()
    {
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public List<ParallelTextItem.Bak> getBaks()
    {
        return baks;
    }

    public SystemFormContext getContext() throws Exception
    {
        if (context == null)
            context = FormApi.getFormContext(bodyId);

        return context;
    }

    @NotSerialized
    public List<PageData> getPages() throws Exception
    {
        return getContext().getFormData().getPages();
    }

    /**
     * 找出某个页下所有的意见栏
     *
     * @return 返回一个列表，包含了所有填了已经的意见栏
     * @throws Exception 从数据库加载表单数据错误
     */
    public List<ParallelTextData> getParallelTexts(PageData pageData) throws Exception
    {
        List<ParallelTextData> texts = new ArrayList<ParallelTextData>();

        for (ComponentData data : pageData)
        {
            if (data instanceof ParallelTextData)
            {
                ParallelTextData text = (ParallelTextData) data;

                if (text.getItems() != null && text.getItems().size() > 0)
                {
                    texts.add(text);
                }
            }
        }

        return texts;
    }

    @Service
    @Forward(page = "/platform/form/parallelbaks.ptl")
    public String showBaks(String name, String id) throws Exception
    {
        SystemFormContext formContext = getContext();

        ComponentData data = formContext.getFormData().getData(name);

        if (data instanceof ParallelTextData)
            baks = ((ParallelTextData) data).getItem(id).getBaks();

        return null;
    }

    @Service(url = "/form/control/{bodyId}")
    public String show()
    {
        return "control";
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void saveOpinion(String name, String id, String text) throws Exception
    {
        FormApi.lockFormBody(getBodyId());

        SystemFormContext context = getContext();

        ComponentData data = context.getFormData().getData(name);

        if (data instanceof ParallelTextData)
        {
            ((ParallelTextData) data).getItem(id).modify(text, userOnlineInfo.getUserId().toString(),
                    userOnlineInfo.getUserName(), userOnlineInfo.getDeptName());
        }

        context.save();
    }
}
