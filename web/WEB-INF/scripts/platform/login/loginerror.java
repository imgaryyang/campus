public String check(String loginName,com.gzzm.platform.login.LoginErrorService service) throws Exception
{
        java.util.Date date = net.cyan.commons.util.DateUtils.truncate();
        if(service.getErrorCount(date)>=5)
           return "您今天已输入5次密码错误，今天不允许再登录!";
        else
           return null;
}