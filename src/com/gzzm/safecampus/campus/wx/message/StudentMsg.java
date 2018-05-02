package com.gzzm.safecampus.campus.wx.message;

/**
 * 发送给学生的模板消息
 *
 * @author Neo
 * @date 2018/4/2 18:57
 */
public class StudentMsg
{
    protected Integer studentId;

    /**
     * 学生姓名/孩子名字
     */
    protected String studentName;

    /**
     * 称谓
     */
    protected final String title = "尊敬的家长：";

    /**
     * 该消息需要打开的url
     */
    protected String url;

    /**
     * 消息类型
     */
    protected TemplateMessageType messageType;

    public StudentMsg(TemplateMessageType messageType)
    {
        this.messageType = messageType;
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public TemplateMessageType getMessageType()
    {
        return messageType;
    }
}
