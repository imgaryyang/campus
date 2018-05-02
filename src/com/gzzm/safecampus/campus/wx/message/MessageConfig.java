package com.gzzm.safecampus.campus.wx.message;

/**
 * 消息相关配置
 *
 * @author Neo
 * @date 2018/4/2 9:51
 */
public class MessageConfig
{
    /**
     * 成绩单消息模板Id
     */
    private TemplateItem scoreTemplate;

    /**
     * 待缴账单消息模板Id
     */
    private TemplateItem unpaidBillTemplate;

    /**
     * 已缴账单消息模板Id
     */
    private TemplateItem paidBillTemplate;

    /**
     * 考勤消息模板Id
     */
    private TemplateItem attendanceTemplate;

    public MessageConfig()
    {
    }

    public TemplateItem getScoreTemplate()
    {
        return scoreTemplate;
    }

    public void setScoreTemplate(TemplateItem scoreTemplate)
    {
        this.scoreTemplate = scoreTemplate;
    }

    public TemplateItem getUnpaidBillTemplate()
    {
        return unpaidBillTemplate;
    }

    public void setUnpaidBillTemplate(TemplateItem unpaidBillTemplate)
    {
        this.unpaidBillTemplate = unpaidBillTemplate;
    }

    public TemplateItem getPaidBillTemplate()
    {
        return paidBillTemplate;
    }

    public void setPaidBillTemplate(TemplateItem paidBillTemplate)
    {
        this.paidBillTemplate = paidBillTemplate;
    }

    public TemplateItem getAttendanceTemplate()
    {
        return attendanceTemplate;
    }

    public void setAttendanceTemplate(TemplateItem attendanceTemplate)
    {
        this.attendanceTemplate = attendanceTemplate;
    }

    public static class TemplateItem
    {
        /**
         * 模板Id
         */
        private String templateId;

        /**
         * 模板内容
         */
        private String template;

        public TemplateItem()
        {
        }

        public String getTemplateId()
        {
            return templateId;
        }

        public void setTemplateId(String templateId)
        {
            this.templateId = templateId;
        }

        public String getTemplate()
        {
            return template;
        }

        public void setTemplate(String template)
        {
            this.template = template;
        }
    }
}
