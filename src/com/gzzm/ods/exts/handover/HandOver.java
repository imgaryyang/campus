package com.gzzm.ods.exts.handover;

import com.gzzm.ods.flow.*;
import net.cyan.valmiki.form.*;

/**
 * @author camel
 * @date 11-12-9
 */
public class HandOver extends AbstractOdFlowComponent
{
    public HandOver()
    {
    }

    @Override
    public void extractData(OdFlowContext context) throws Exception
    {
        super.extractData(context);

        FormData formData = context.getFormContext().getFormData();

        context.getFlowContext().getInstance().setTitle(formData.getString("事项名称"));
    }

    public String getJsFile()
    {
        return "/ods/exts/handover/handover.js";
    }
}
