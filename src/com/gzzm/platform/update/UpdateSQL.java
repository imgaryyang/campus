package com.gzzm.platform.update;

import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 启动时执行的sql，系统启动时扫描WEB-INF/initsql目录下的sql文件
 * 如果此文件没有被执行过，则执行之，执行后将文件记录在表pfupdatesql中，下次不再执行。
 *
 * @author camel
 * @date 13-5-22
 */
@Entity(table = "PFUPDATESQL", keys = "fileName")
public class UpdateSQL
{
    /**
     * 文件名
     */
    @ColumnDescription(type = "varchar(250)")
    private String fileName;

    /**
     * 执行时间
     */
    private Date exeucteTime;

    /**
     * 错误信息，如果执行成功则为空
     */
    private char[] error;

    public UpdateSQL()
    {
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Date getExeucteTime()
    {
        return exeucteTime;
    }

    public void setExeucteTime(Date exeucteTime)
    {
        this.exeucteTime = exeucteTime;
    }

    public char[] getError()
    {
        return error;
    }

    public void setError(char[] error)
    {
        this.error = error;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UpdateSQL))
            return false;

        UpdateSQL updateSQL = (UpdateSQL) o;

        return fileName.equals(updateSQL.fileName);
    }

    @Override
    public int hashCode()
    {
        return fileName.hashCode();
    }
}
