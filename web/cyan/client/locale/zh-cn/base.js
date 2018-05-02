Cyan.Date.getDayString = function (day)
{
    return "星期" + (day == 0 ? "日" : Cyan.Date.getSimpleDayString(day));
};
Cyan.Date.getSimpleDayString = function (day)
{
    return ["日", "一", "二", "三", "四", "五", "六"][day];
};
Cyan.Date.getMonthString = function (month)
{
    return ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"][month - 1] + "月";
};
Cyan.titles =
{message:"信息提示", error:"错误提示", confirm:"信息确认", prompt:"请输入", submiting:"正在提交数据", waiting:"请稍候",
    waitForSubmit:"正在提交数据，请耐心等待", loading:"正在加载数据", search:"搜索", clear:"清空"};

Cyan.Date.second = "秒";
Cyan.Date.minute = "分";
Cyan.Date.hour = "小时";
Cyan.Date.day = "天";
Cyan.Date.month = "月";
Cyan.Date.year = "年";

/**
 * 中文日期格式
 */
Cyan.Date.addDateFormat("yyyy年MM月dd日");
Cyan.Date.addDateFormat("yyyy年M月d日");
Cyan.Date.addDateTimeFormat("yyyy年MM月dd日 HH:mm:ss");
Cyan.Date.addDateTimeFormat("yyyy年MM月dd日 HH:mm");
Cyan.Date.addDateTimeFormat("yyyy年M月d日 H:m:s");
Cyan.Date.addDateTimeFormat("yyyy年M月d日 H:m");