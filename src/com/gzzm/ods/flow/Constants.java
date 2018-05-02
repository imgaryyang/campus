package com.gzzm.ods.flow;

import java.util.*;

/**
 * 表单控件名常量
 *
 * @author camel
 * @date 11-10-11
 */
public interface Constants
{
    /**
     * 公文相关的常量
     */
    interface Document
    {
        String TITLE = "文件标题";

        String TITLE1 = "文件名称";

        String SOURCEDEPT = "来文单位";

        String SENDNUMBER = "发文字号";

        String SENDNUMBER1 = "原文号";

        String SUBJECT = "主题词";

        String SECRET = "密级";

        String SECRET1 = "原文密级";

        String SECRET2 = "秘密密级";

        String PRIORITY = "紧级";

        String PRIORITY1 = "紧急";

        String PRIORITY2 = "原文紧级";

        String PRIORITY3 = "原文紧急";

        String PRIORITY4 = "紧急程度";

        String SENDCOUNT = "份数";

        String ATTACHMENT = "附件";

        String ATTACHMENT1 = "收文附件";

        String MAINSEND = "主送";

        String SIGNER = "签发人";

        String SIGNTIME = "签发时间";
    }

    /**
     * 流程相关的常量
     */
    interface Flow
    {
        String SERIAL = "编号";

        String TAG = "标志";

        String DEADLINE = "完成时限";

        String MESSAGE = "短信通知内容";
    }

    /**
     * 发文相关的常量
     */
    interface Send
    {
        String UNIONDEPTS = "联合发文单位";

        String SELECTREDHEAD = "选择红头模板";

        String REDHEAD = "红头模板";

        String SEAL = "用章";

        String CREATOR = "拟稿人";

        String CREATEDEPT = "拟稿科室";
    }

    /**
     * 收文相关的常量
     */
    interface Receive
    {
        String SENDTIME = "来文日期";

        String ACCEPTTIME = "收文日期";

        String RECEIVER = "登记人";

        String TURN = "转发";

        String RECEIVEDEPT = "收文机关";
    }

    /**
     * 联合办文相关常量
     */
    interface Union
    {
        String UNION = "联合办文";
    }

    /**
     * 联合办文相关常量
     */
    interface UnionDeal
    {
        String UNIONDEAL = "联合办文";
    }

    /**
     * 会签相关常量
     */
    interface Collect
    {
        String COLLECT = "会签";

        List<String> COLLECTCOMPONENTNAMES =
                Arrays.asList("会签意见", "会签", "相关部门意见", "ParellelWord1", "回复意见", "单位意见", "会签单位意见");
    }

    /**
     * 红头相关的常量
     */
    interface RedHead
    {
        String BUSINESSDEPTNAME = "部门名称";

        String RECEIVERS = "收文单位";

        String SIGNER = "签发人";

        String SIGNTIME = "签发时间";

        String MAINSEND = "主送";

        String SENDTIME = "发文日期";

        String PRIORITY = "紧级";

        String PRIORITY1 = "紧急";
    }

    /**
     * 回执相关的常量
     */
    interface Receipt
    {
        String RECEIPT = "回执";

        String RECEIPT1 = "是否需要回执";

        String ACCEPTDEPT = "回执接收部门";
    }
}
