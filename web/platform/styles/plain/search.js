System.Desktop.topHeight = 50;

Cyan.importJs("/platform/group/member.js");

var tipStr="请输入公文、邮件、功能、用户等关键字";
Cyan.onload(function () {
    setTimeout(function () {

        var desktop_top = Cyan.$("desktop_top");
        if (System.desktopGroupId == "desktop") {

            desktop_top.innerHTML =
                "<div id='desksearchdiv'><input id='deskcondition'  name='deskcondition' />" +
                "<button  id='desksearchbut' onclick='search()' >搜索</button>" +
                "<span class='config'><a href='#' onclick='System.Desktop.showConfig();return false;'>设置桌面</a></span></div>";
            Cyan.$$("#deskcondition").hotKey(
                {
                    "#13": function () {
                        search();
                    }
                });
            Cyan.$$("#deskcondition").attr('style','color:gray;');
            Cyan.$$("#deskcondition").val(tipStr);
            Cyan.$$("#deskcondition").onfocus(function(){
               var v=this.value;
               if(v == tipStr){
                   Cyan.$$("#deskcondition").attr('style','color:;');
                   Cyan.$$("#deskcondition").val("");
                }
            }).onblur(function(){
                var v=this.value;
                if(v == ""){
                    Cyan.$$("#deskcondition").attr('style','color:gray;');
                    Cyan.$$("#deskcondition").val(tipStr);
                }
            });
        }
        else {
            desktop_top.innerHTML =
                "<span class='config'><a href='#' onclick='System.Desktop.showConfig();return false;'>设置桌面</a></span>";
        }
    }, 100);
});

function search() {
    var condition = Cyan.trim(Cyan.$("deskcondition").value);
    if(tipStr==condition)
    {
        condition=null;
    }
    if (!condition) {
        Cyan.message("请输入查询关键字");
        return;
    }
    var menuUrl = "/oa/superquery/homepage";
     var url = "/oa/superquery/turnToSearch?pageNo=1&&searchType=1&&pageType=search&&condition=" + encodeURIComponent(condition);
    var menu = System.getMenuByUrl(menuUrl);
    if (menu)
        menu.go(url);
    else
        System.openPage(url, menuUrl, "超级搜索");
}