package com.gzzm.in;

/**
 * @author camel
 * @date 13-12-23
 */
public class FileUploadResult extends InterfaceResult
{
    private String id;

    public FileUploadResult()
    {
    }

    public FileUploadResult(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
