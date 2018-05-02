package com.gzzm.safecampus.campus.common;

import com.gzzm.platform.annotation.CacheInstance;
import com.gzzm.safecampus.campus.account.SchoolYear;
import com.gzzm.safecampus.campus.account.SchoolYearService;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Neo
 * @date 2018/3/24 20:04
 */
@CacheInstance("SchoolYear")
public class SchoolYearContainer
{
    @Inject
    private static Provider<SchoolYearService> schoolYearServiceProvider;

    private Map<Integer, List<SchoolYear>> schoolYearMap = new HashMap<>();

    private Map<Integer, SchoolYear> currentSchoolYearMap = new HashMap<>();

    public SchoolYearContainer() throws Exception
    {
        load();
    }

    private void load() throws Exception
    {
        for (SchoolYear schoolYear : schoolYearServiceProvider.get().getCurrentSchoolYear())
        {
            List<SchoolYear> schoolYears = schoolYearMap.get(schoolYear.getDeptId());
            if (schoolYears == null)
            {
                schoolYears = new ArrayList<>();
                schoolYearMap.put(schoolYear.getDeptId(), schoolYears);
            }
            schoolYears.add(schoolYear);
            if (schoolYear.getStatus())
            {
                currentSchoolYearMap.put(schoolYear.getDeptId(), schoolYear);
            }
        }
    }

    public SchoolYear getCurrentSchoolYear(Integer deptId)
    {
        return currentSchoolYearMap.get(deptId);
    }

    public List<SchoolYear> getSchoolYear(Integer deptId)
    {
        return schoolYearMap.get(deptId);
    }
}
