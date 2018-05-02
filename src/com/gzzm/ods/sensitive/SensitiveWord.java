package com.gzzm.ods.sensitive;

import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.Entity;

/**
 * @author camel
 * @date 13-1-14
 */
@Entity(table = "ODSENSITIVEWORD", keys = "wordId")
public class SensitiveWord
{
    private Integer wordId;

    private String word;

    private Integer deptId;

    private Dept dept;

    public SensitiveWord()
    {
    }

    public Integer getWordId()
    {
        return wordId;
    }

    public void setWordId(Integer wordId)
    {
        this.wordId = wordId;
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SensitiveWord))
            return false;

        SensitiveWord that = (SensitiveWord) o;

        return wordId.equals(that.wordId);
    }

    @Override
    public int hashCode()
    {
        return wordId.hashCode();
    }
}
