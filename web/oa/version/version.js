/**
 * Created by zengyu on 2017/1/19.
 */
function more()
{
    var url = "/oa/version/crud";
    var code=Cyan.Arachne.form.projectCode;
    if(code!=null&&code!="")
    {
        url+="?projectCode="+code
    }
    var menu = System.getMenuByUrl(url);
    if (menu)
        menu.go();
    else
    {
        System.openPage(url);
    }
}