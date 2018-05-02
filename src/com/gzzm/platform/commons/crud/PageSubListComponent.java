package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.SubListViewModel;
import net.cyan.arachne.*;
import net.cyan.arachne.components.AbstractPageComponent;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.ListCrud;

import java.util.Map;

/**
 * @author camel
 * @date 2010-12-7
 */
@SuppressWarnings("UnusedDeclaration")
public class PageSubListComponent extends AbstractPageComponent<SubListView>
{
    public PageSubListComponent()
    {
    }

    public String createHtml(String tagName, Map<String, Object> attributes) throws Exception
    {
        ListCrud crud = (ListCrud) model.getCrud();


        String id = StringUtils.toString(attributes.get("id"));
        //没有定义id，使用tree做id
        if (id == null)
            throw new NullPointerException("id");

        String crudVariable = id + "$crud";

        ForwardContext context = RequestContext.getContext().getForwardContext();

        context.importJs("/platform/commons/sublist.js");

        context.setVariable("sublist$", new SubListViewModel(model, id));
        context.include("/platform/commons/sublist");


        return "<script>\n" + PageClass.getClassInfo(crud.getClass()).getObjectScript(crud, crudVariable) +
                "\n</script>\n";
    }
}
