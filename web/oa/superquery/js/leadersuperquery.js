Cyan.onload(function () {
    Cyan.Class.overwrite(window, "openDocument", function (instanceId, readOnly)
    {
        var url = "/ods/flow/instance/" + instanceId + "/open";
        if (readOnly)
            url += "?readOnly=" + readOnly;

        System.openPage(url);
    });
});

function searchLeaderConditionSearch(){
    var condition = Cyan.$("condition").value;
    condition = Cyan.trim(condition)
    if (!condition) {
        $.error("请输入查询关键字");
        return false;
    }
    Cyan.Arachne.form.condition = condition;
    Cyan.Arachne.form.pageNo = 1;
    searchLeaderCheckBox();
}

function changeTab(searchType,obj){
    var tabLi=$$(".gw_sb a")
    for(var i=0;i<tabLi.length;i++){
        tabLi[i].setAttribute("class", "")
        var tableobj="#t"+(i+1);
        $$(tableobj)[0].style.display="none";
    }
    obj.setAttribute("class", "hover")
    var selectobj="#t"+searchType;
    $$(selectobj)[0].style.display="block";
}

function pageSearch(turnPageType){
    var pageNo=Cyan.$("emailPageNo").value;
    var pageCount=Cyan.$("emailpageCount").value;
    if(turnPageType=="first"){
        pageNo=1;
    }else if(turnPageType=="previous"){
        pageNo--;
    }else if(turnPageType=="next"){
        pageNo++;
    }else if(turnPageType=="last"){
        pageNo=Cyan.$("emailpageCount").value;
    } else if(turnPageType=="turnToPage"){
        pageNo = parseInt(Cyan.$("emailTurnTo").value);
        if (pageNo < 1) {
            pageNo = 1;
        } else if (pageNo > pageCount) {
            pageNo = pageCount;
        }
    }
    Cyan.Arachne.form.pageNo = pageNo;
    createEmail()
}

function leaderDocumentPageSearch(turnPageType){
    var pageNo=Cyan.$("wsPageNo").value;
    var pageCount = Cyan.$("wsPageCount").value;
    if(turnPageType=="first"){
        pageNo=1;
    }else if(turnPageType=="previous"){
        pageNo--;
    }else if(turnPageType=="next"){
        pageNo++;
    }else if(turnPageType=="last"){
        pageNo=pageCount;
    }else if(turnPageType=="turnToPage"){
        pageNo = parseInt(Cyan.$("wsTurnTo").value);
        if (pageNo < 1) {
            pageNo = 1;
        } else if (pageNo > pageCount) {
            pageNo = pageCount;
        }
    }
    Cyan.Arachne.form.pageNo = pageNo;
    createLeaderDocument()
}

function openHref(appId, url, title, target) {
    if (appId) {
        System.goMenu(appId);
    } else {
        System.openPage(url, null, title);
    }
}

function keySearch(type) {
    if (event.keyCode == '13') {
        if (type == 1) {//搜索文本框的按钮跳转
            $$("#search")[0].click();
        }
        if (type == 2) {//输入到第几页的按钮跳转
            $$("#goto")[0].click();
        }
        if (type == 3) {//输入到第几页的按钮跳转
            $$("#wsgoto")[0].click();
        }
        if (type == 4) {//领导搜索文本框的按钮跳转
            $$("#leaderSearch")[0].click();
        }
        if (type == 5) {//领导输入到第几页的按钮跳转
            $$("#leadergoto")[0].click();
        }
    }
}

function hideAll(showTab){
    var tabLi=$$(".gw_sb li")
    var taba=$$(".gw_sb a")
    for(var i=0;i<tabLi.length;i++){
        tabLi[i].style.display="none";
        $$("#t"+(i+1))[0].style.display="none";
    }
    for(var i=0;i<taba.length;i++){
        taba[i].className="";
    }
    var firstLi=$$("#li"+showTab)[0];
    var firsta=$$("#li"+showTab +" a")[0];
    firsta.className='hover';
    firstLi.style.display="block";
    var firstTab=$$("#t"+showTab);
    firstTab[0].style.display="block";
}

function searchLeaderCheckBox(){
    var wsCheck=$$("#wsCheckBox")[0].checked;
    var menuCheck=$$("#menuCheckBox")[0].checked;
    var userCheck=$$("#userCheckBox")[0].checked;
    var showTab=wsCheck?1:menuCheck?2:userCheck?3:0;
    if (showTab==0) {
        $.error("请选择查询内容");
        return false;
    }
    hideAll(showTab);
    if(wsCheck){
        createLeaderDocument();
        $$("#li1")[0].style.display="block";
    }
    if(menuCheck){
        createMenu();
        countMenu();
        $$("#li2")[0].style.display="block";
    }
    if(userCheck){
        createUser();
        countUser();
        $$("#li3")[0].style.display="block";
    }
}
