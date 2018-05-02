package com.gzzm.oa.mail;

/**
 * 邮件归档目录的扩展信息
 *
 * @author camel
 * @date 2010-4-19
 */
public class MailCatalogInfo
{
    /**
     * 归档目录占用的大小
     */
    private long usedSize;

    /**
     * 邮件数量
     */
    private int mailCount;

    /**
     * 未读邮件数量
     */
    private int notReadCount;

    public MailCatalogInfo()
    {
    }

    public long getUsedSize()
    {
        return usedSize;
    }

    public void setUsedSize(long usedSize)
    {
        this.usedSize = usedSize;
    }

    public int getMailCount()
    {
        return mailCount;
    }

    public void setMailCount(int mailCount)
    {
        this.mailCount = mailCount;
    }

    public int getNotReadCount()
    {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount)
    {
        this.notReadCount = notReadCount;
    }
}
