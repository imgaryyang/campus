package com.gzzm.platform.commons;

import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2014/12/10
 */
@Entity(table = "PFERROR", keys = "errorId")
public class SystemError
{
    @Generatable(length = 14)
    private Long errorId;

    @ColumnDescription(type = "varchar(4000)")
    private String message;

    private char[] content;

    public SystemError()
    {
    }

    public Long getErrorId()
    {
        return errorId;
    }

    public void setErrorId(Long errorId)
    {
        this.errorId = errorId;
    }

    public char[] getContent()
    {
        return content;
    }

    public void setContent(char[] content)
    {
        this.content = content;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SystemError))
            return false;

        SystemError that = (SystemError) o;

        return errorId.equals(that.errorId);
    }

    @Override
    public int hashCode()
    {
        return errorId.hashCode();
    }
}
