package com.gzzm.ods.exchange.back;

import com.gzzm.platform.organ.*;
import com.gzzm.platform.wordnumber.WordNumber;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2018/1/29
 */
public class BackService
{
    @Inject
    private BackDao dao;

    @Inject
    private DeptService deptService;

    public BackService()
    {
    }

    public BackDao getDao()
    {
        return dao;
    }

    public List<BackPaper> getBackPapers(Integer deptId) throws Exception
    {
        List<BackPaper> backPagers = dao.getBackPagers(deptId);
        if (backPagers.size() > 0)
            return backPagers;

        if (deptId == 1)
            return null;

        DeptInfo dept = deptService.getDept(deptId);
        do
        {
            backPagers = dao.getBackPagers(dept.getParentDeptId());
            if (backPagers.size() > 0)
                return backPagers;
        }
        while ((dept = dept.parentDept()).getDeptId() != 1);

        return null;
    }

    public String getBackNumberString(Integer backNumberId) throws Exception
    {
        return getBackNumberString(dao.getBackNumber(backNumberId));
    }

    public String getBackNumberString(BackNumber backNumber) throws Exception
    {
        WordNumber wordNumber = new WordNumber(backNumber.getBackNumber());
        wordNumber.setDeptId(backNumber.getDeptId());
        wordNumber.setType("od");
        return wordNumber.getResult();
    }

    public List<BackNumber> getBackNumbers(Integer deptId) throws Exception
    {
        List<BackNumber> backNumbers = dao.getBackNumbers(deptId);

        if (backNumbers.size() > 0)
            return backNumbers;

        if (deptId == 1)
            return null;

        DeptInfo dept = deptService.getDept(deptId);
        do
        {
            backNumbers = dao.getBackNumbers(dept.getParentDeptId());
            if (backNumbers.size() > 0)
                return backNumbers;
        }
        while ((dept = dept.parentDept()).getDeptId() != 1);

        return null;
    }
}
