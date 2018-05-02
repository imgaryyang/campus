/**
 * 表示证书到期前多少天进行提醒
 */
var key_login_days_ahead_for_remind = 30;

/**
 * 到期提醒提示信息
 */
var getKeyLoginRemindMessage = function (day)
{
    return "您的证书在" + day + "天后过期，请尽快到云浮市经济和信息化局办理证书续期更新手续。联系电话：8988925。";
};