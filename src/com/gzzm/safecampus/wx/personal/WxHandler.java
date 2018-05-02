package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.score.SubjectScore;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.StringUtils;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author yiuman
 * @date 2018/4/11
 */
public class WxHandler {


    /**
     * 格式化String集合
     *
     * @param list     集合
     * @param splitStr 分割符
     * @return
     */
    public static String formatStringCollections(List<String> list, String splitStr)
    {
        if (CollectionUtils.isNotEmpty(list))
        {
            StringBuilder sb = new StringBuilder();
            for (String str : list)
            {
                sb.append(str).append(splitStr);
            }
            return sb.substring(0, sb.length() - 1);
        }
        return null;
    }


    /**
     * 手机号 加星处理
     *
     * @param content 需要加星的内容
     * @param begin   开始位置
     * @param end     结束位置
     * @return
     */
    public static String getStarString(String content, int begin, int end)
    {
        if (begin >= content.length() || begin < 0)
        {
            return content;
        }
        if (end >= content.length() || end < 0)
        {
            return content;
        }
        if (begin >= end)
        {
            return content;
        }
        String starStr = "";
        for (int i = begin; i < end; i++)
        {
            starStr = starStr + "*";
        }
        return content.substring(0, begin) + starStr + content.substring(end, content.length());
    }


    /**
     * 学生科目成绩排名
     *
     * @param list
     * @param scoreId
     * @return
     */
    public static int getSort(List<SubjectScore> list, Integer scoreId)
    {
        //因为是排名,所以降序排序
        Collections.sort(list, new Comparator<SubjectScore>()
        {
            @Override
            public int compare(SubjectScore o1, SubjectScore o2)
            {
                if (StringUtils.isBlank(o1.getScore()))
                {
                    return 1;
                }
                if (StringUtils.isBlank(o2.getScore()))
                {
                    return -1;
                }
                if (Float.valueOf(o1.getScore()) > Float.valueOf(o2.getScore()))
                {
                    return -1;
                } else if (Float.valueOf(o1.getScore()).equals(Float.valueOf(o2.getScore())))
                {
                    return 0;
                } else
                {
                    return 1;
                }
            }
        });
        int sort = 0;
        //因为list从0开始,所以要+1
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getScoreId().equals(scoreId))
            {
                sort = i + 1;
                break;
            }
        }
        return sort;
    }

    /**
     * 平均分
     *
     * @param list
     * @return
     */
    public static String getAvg(List<SubjectScore> list)
    {
        int count = list.size();
        float totalScore = 0f;
        for (SubjectScore subjectScore : list)
        {
            totalScore += Float.valueOf(subjectScore.getScore());
        }
        float avg = totalScore / count;
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(avg);
    }
}
