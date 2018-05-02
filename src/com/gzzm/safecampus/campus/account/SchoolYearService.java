package com.gzzm.safecampus.campus.account;

import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author Neo
 * @date 2018/3/26 9:59
 */
public class SchoolYearService
{
    @Inject
    private SchoolYearDao dao;

    public SchoolYearService()
    {
    }

    public List<SchoolYear> getCurrentSchoolYear() throws Exception
    {
        return dao.getCurrentSchoolYear();
    }
}
