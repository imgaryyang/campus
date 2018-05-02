package com.gzzm.platform.wordnumber;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.nest.annotation.Inject;

import java.util.Map;

/**
 * @author camel
 * @date 2015/8/5
 */
public class WordNumberService
{
    @Inject
    private WordNumberDao dao;

    @Inject
    private DeptService deptService;

    public WordNumberService()
    {
    }

    public WordNumberService getInstance() throws Exception
    {
        return Tools.getBean(WordNumberService.class);
    }

    public WordNumberConfig getWordNumberConfig(String type, Integer deptId) throws Exception
    {
        WordNumberConfig wordNumberConfig = dao.getWordNumberConfig(type, deptId);
        if (wordNumberConfig != null || deptId == 1)
        {
            return wordNumberConfig;
        }

        DeptInfo dept = deptService.getDept(deptId);

        return getWordNumberConfig(type, dept.parentDept());
    }

    public WordNumberConfig getWordNumberConfig(String type, DeptInfo dept) throws Exception
    {
        Integer deptId = dept.getDeptId();

        WordNumberConfig wordNumberConfig = dao.getWordNumberConfig(type, deptId);
        if (wordNumberConfig != null || deptId == 1)
        {
            return wordNumberConfig;
        }

        return getWordNumberConfig(type, dept.parentDept());
    }

    public String getWordNumber(String type, Integer deptId, Map<String, Object> properties) throws Exception
    {
        WordNumberConfig wordNumberConfig = getWordNumberConfig(type, deptId);
        if (wordNumberConfig == null)
            return null;

        WordNumber wordNumber = new WordNumber(wordNumberConfig.getWordNumber());

        if (wordNumberConfig.getWordNumberDeptId() != null)
            wordNumber.setDeptId(wordNumberConfig.getDeptId());
        else
            wordNumber.setDeptId(deptId);

        wordNumber.setType(type);

        if (properties != null)
        {
            for (Map.Entry<String, Object> entry : properties.entrySet())
            {
                wordNumber.setProperty(entry.getKey(), entry.getValue());
            }
        }

        return wordNumber.getResult();
    }

    public String getWordNumber(String type, Integer deptId) throws Exception
    {
        return getWordNumber(type, deptId, null);
    }
}
