package com.gzzm.safecampus.campus.wx.message;

/**
 * 成绩消息
 *
 * @author Neo
 * @date 2018/4/2 9:49
 */
public class ScoreMsg extends StudentMsg
{
    /**
     * 考试名称
     */
    private String courseName;

    /**
     * 考试时间
     */
    private String time;

    /**
     * 成绩/得分
     */
    private String score;

    public ScoreMsg()
    {
        super(TemplateMessageType.SCORE);
    }

    public String getFirst()
    {
        return title;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getScore()
    {
        return score;
    }

    public void setScore(String score)
    {
        this.score = score;
    }
}
