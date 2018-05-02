package com.gzzm.safecampus.campus.common;

import net.cyan.arachne.ForwardContext;
import net.cyan.arachne.PageClass;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.components.AbstractPageComponent;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.ListCrud;

import java.util.*;

/**
 * @author camel
 * @date 2010-12-7
 */
@SuppressWarnings("UnusedDeclaration")
public class ScPageSubListComponent extends AbstractPageComponent<ScSubListView>
{
    public ScPageSubListComponent()
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

        context.importJs("/safecampus/campus/base/crud/sublist.js");

        context.setVariable("sublist$", new ScSubListViewModel(model, id));
        context.include("/safecampus/campus/base/crud/sublist");


        return "<script>\n" + PageClass.getClassInfo(crud.getClass()).getObjectScript(crud, crudVariable) +
                "\n</script>\n";
    }
}