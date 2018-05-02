package com.gzzm.portal.cms.information;

import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;

/**
* @author camel
* @date 2014/11/14
*/
@Service(url = "/portal/simple_information")
public class SimpleInformationCrud extends InformationCrudBase<SimpleInformation>
{
    static
    {
        ParameterCheck.addNoCheckURL("/portal/simple_information");
    }

    private boolean editable = true;

    public SimpleInformationCrud()
    {
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    @Override
    @Forward(page = "/portal/cms/information/simple0.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/portal/cms/information/simple0.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }
}
