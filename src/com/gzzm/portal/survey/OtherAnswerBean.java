package com.gzzm.portal.survey;

import java.util.*;

/**
 * 其他回答列表显示的列表字段bean
 * @author sjy
 * @date 2016/12/9
 */
public class OtherAnswerBean {

    //回答内容
    private Object answerContent;

    //回答时间
    private Date answerTime;

    //回答用户名称
    private String userName;

    public Object getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(Object answerContent) {
        this.answerContent = answerContent;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
