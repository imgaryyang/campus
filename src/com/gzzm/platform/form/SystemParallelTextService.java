package com.gzzm.platform.form;

import com.gzzm.platform.sign.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.FormContext;
import net.cyan.valmiki.form.components.*;

/**
 * @author camel
 * @date 2015/6/3
 */
public class SystemParallelTextService implements ParallelTextService
{
    @Inject
    private static Provider<SignDao> daoProvider;

    public SystemParallelTextService()
    {
    }

    @Override
    public String getSignUrl(String operator, FParallelText parallelText, FormContext context) throws Exception
    {
        String operatorType = ((SystemFormContext) context).getOperatorType(parallelText);
        if (SystemFormContext.USER.equals(operatorType))
        {
            SignDao signDao = daoProvider.get();
            UserSign userSign = signDao.getUserSign(Integer.valueOf(operator));

            if (userSign != null && userSign.getSign() != null)
                return "/sign/user/" + operator + "/sign";
        }

        return null;
    }
}
