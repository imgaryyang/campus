package com.gzzm.platform.wordnumber;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 编号配置
 *
 * @author camel
 * @date 2015/8/5
 */
@Entity(table = "PFWORDNUMBERCONFIG", keys = "configId")
@Indexes(@Index(columns = {"TYPE", "DEPTID"}))
public class WordNumberConfig
{
    @Generatable(length = 6)
    private Integer configId;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 编号类型
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private WordNumberType type;

    @ColumnDescription(type = "varchar(250)")
    private String wordNumber;

    private Integer wordNumberDeptId;

    @NotSerialized
    @ToOne("WORDNUMBERDEPTID")
    private Dept wordNumberDept;

    public WordNumberConfig()
    {
    }

    public Integer getConfigId()
    {
        return configId;
    }

    public void setConfigId(Integer configId)
    {
        this.configId = configId;
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

    public WordNumberType getType()
    {
        return type;
    }

    public void setType(WordNumberType type)
    {
        this.type = type;
    }

    public String getWordNumber()
    {
        return wordNumber;
    }

    public void setWordNumber(String wordNumber)
    {
        this.wordNumber = wordNumber;
    }

    public Integer getWordNumberDeptId()
    {
        return wordNumberDeptId;
    }

    public void setWordNumberDeptId(Integer wordNumberDeptId)
    {
        this.wordNumberDeptId = wordNumberDeptId;
    }

    public Dept getWordNumberDept()
    {
        return wordNumberDept;
    }

    public void setWordNumberDept(Dept wordNumberDept)
    {
        this.wordNumberDept = wordNumberDept;
    }

    @NotSerialized
    public String getText() throws Exception
    {
        if (wordNumber != null)
        {
            WordNumber wordNumberObject = new WordNumber(wordNumber);

            return wordNumberObject.toString();
        }

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof WordNumberConfig))
            return false;

        WordNumberConfig that = (WordNumberConfig) o;

        return configId.equals(that.configId);
    }

    @Override
    public int hashCode()
    {
        return configId.hashCode();
    }
}
