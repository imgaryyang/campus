package com.gzzm.safecampus.campus.account;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * @author yuanfang
 * @date 18-03-16 13:57
 */
public abstract class SchoolYearDao extends GeneralDao
{
    @OQL("select schoolYearId from SchoolYear where  year=:1 and deptId=:2 and  schoolYearId<>?3 and deleteTag=0")
    public abstract Integer checkYear(Integer year, Integer deptId, Integer schoolYearId) throws Exception;

    @OQL("select count(*) from SchoolYear  where status=1 ")
    public abstract Integer checkYearStatus() throws Exception;

    @OQLUpdate("update SchoolYear set status=1 where schoolYearId=:1 ")
    public abstract void switchSchoolYearStatusOn(Integer schoolYearId) throws Exception;

    @OQLUpdate("update SchoolYear set status=0 where deptId=:1 ")
    public abstract void switchSchoolYearStatusOff(Integer deptId) throws Exception;

    @OQL("select s from SchoolYear s where s.status=1")
    public abstract List<SchoolYear> getCurrentSchoolYear() throws Exception;
}
