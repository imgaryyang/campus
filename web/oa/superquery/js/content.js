Cyan.onload(function () {
    Cyan.Arachne.form.pageNo = 1;
/*    createDocument();
    createEmail();
    createMenu();
    createUser();
    countEmail();
    countMenu();
    countUser();*/
    searchCheckBox();
});

function createDocument(){
    Cyan.Arachne.form.searchType = 1;
    search({
        wait:false,
        callback : function (result) {
            Cyan.$("condition").value=result.condition;
            if (result) {
                createDocumentHtml(result);
            }
        }
    });
}

function createEmail(){
    Cyan.Arachne.form.searchType = 2;
    search({
        wait:false,
        callback : function (result) {
            if (result) {
                createEmailHtml(result);
            }
        }
    });
}

function createMenu(){
    Cyan.Arachne.form.searchType = 3;
    search({
        wait:false,
        callback : function (result) {
            if (result) {
                createMenuHtml(result);
            }
        }
    });
}

function createUser(){
    Cyan.Arachne.form.searchType = 4;
    search({
        wait:false,
        callback : function (result) {
            createUserHtml(result);
        }
    });
}

function countEmail(){
    Cyan.Arachne.form.searchType = 2;
        resultCount({
            wait:false,
            callback : function (result) {
                    $$("#emailCount")[0].innerText="邮件（"+result+"）";
            }
        })
}

function countMenu(){
    Cyan.Arachne.form.searchType = 3;
    resultCount({
        wait:false,
        callback : function (result) {
            $$("#menuCount")[0].innerText="功能（"+result+"）";
        }
    })
}

function countUser(){
    Cyan.Arachne.form.searchType = 4;
    resultCount({
        wait:false,
        callback : function (result) {
            $$("#userCount")[0].innerText="用户（"+result+"）";
        }
    })
}

function createDocumentHtml(result){
    var headhtml="<table width='100%' border='0' cellspacing='1' cellpadding='0' class='box1'>"+
            "<tr><td class='td1' width='15%'>标题</td><td class='td1' width='6%'>正文/附件</td><td class='td1' width='7%'>类型</td>"+
            "<td class='td1'>发文字号</td><td class='td1'>来文单位拟稿人</td><td class='td1'>来自</td><td class='td1'>来文时间</td>"+
            "<td class='td1'>环节</td><td class='td1'>状态</td><td class='td1'>已用时间</td><td class='td1'>跟踪</td> </tr>"

    var workSheetItemList=result.workSheetItemList;
    var content=""
    for(var i=0;i<workSheetItemList.length;i++){
        var item=workSheetItemList[i];
        content+="<tr><td style='text-align: left'><span class='a_suspension_points' title='"+ item.titleText+"' >" +
        "<a href='javascript:void(0);' onclick=openStep("+item.stepId+",'"+item.type+"');return false;'>"
        var priority=item.priority;
        if(priority!=null&&priority!=""&&priority!="普通"&&priority!="平件"&&priority!="常规"){
            content+= "<span class='priority'>["+priority+"]</span>"
        }
        if(item.tag!=null){
            content+= "  <span class='tag'>["+item.tag+"]</span>"
        }
        content+=item.titleText +"</a> </span> </td>"

        if(item.textId!=null){
            var attachmentStr;
            if(item.attachment){
                attachmentStr="正文/附件";
            }else{
                attachmentStr="正文";
            }
            content+="<td><a id='textHref"+item.stepId+"'onclick=showText("+item.stepId+","+item.documentId+",'"+item.type+"',"+item.attachment.toString()+",'"+item.otherFileName+"');return false;href='javascript:void(0);'>"+attachmentStr+"</a></td>"
        }else{
            content+="<td> </td>"
        }
        var sendNumber="";
        if(item.sendNumber!=null){
            sendNumber=item.sendNumber;
        }
        content+=" <td><span class='type_send'>"+item.typeName+"</span></td><td><span>"+sendNumber+"</span></td>"+
        " <td><span>"+item.source+"</span></td><td><span>"+item.sourceName+"</span></td>"+
        "<td style='text-align: left'><span>"+item.receiveTime+"</span></td><td><span>"+item.nodeName+"</span></td>"

        if(item.state=="unclosed"){
            content+="<td> <span style='color:red'>"+item.state+"</span> </td>"
        }else{
            content+="<td><span>"+item.state+"</span></td>"
        }
        content+= "<td><span>"+item.workday+"工作日</span></td>"
       if(item.flowTag!='copy'){
           content+="<td>"+" <a onclick='track("+item.stepId+");return false;' href='javascript:void(0);'>跟踪</a></td></tr>"
       }else{
           content+="<td></td></tr>"
        }
    }
    var tableEnd="</table>";
    var foothtml="";
    foothtml+="<div id='foot' class='fy' ><div class='fy_lt'><p>共"+result.pageCount+"页</p><p>当前第"+result.pageNo+"页</p>"
    if(result.pageNo==1){
        foothtml+="<p><span class='no_click_a'>首页</span></p><p ><span class='no_click_a'>上一页</span></p>"
    }else{
        foothtml+="<p><a href='javascript:void(0);' onclick=documentPageSearch('first')>首页</a></p>"
        foothtml+="<p><a href='javascript:void(0);' onclick=documentPageSearch('previous')>上一页</a></p>"
    }
    foothtml+="<p><a href='javascript:void(0);' onclick='documentPageSearch()'>刷新</a></p>"
    if(result.pageNo>=result.pageCount){
        foothtml+="<p ><span class='no_click_a'>下一页</span><p ><span class='no_click_a'>尾页</span></p>"
    }else{
        foothtml+="<p><a href='javascript:void(0);' onclick=documentPageSearch('next')>下一页</a></p>"
        foothtml+="<p><a href='javascript:void(0);' onclick=documentPageSearch('last')>尾页</a></p>"
    }

    foothtml+="</div> <div class='fy_zj'><input onkeyup='keySearch(3)' type='text' name='wsTurnTo' value='"+result.pageNo+"'/><p>页</p></div>"
    foothtml+="<div class='fy_rt'><a href='javascript:void(0);' id='wsgoto' onclick=documentPageSearch('turnToPage')>跳转</a></div></div>"
    $$("#wsCount")[0].innerText="公文（"+result.wsCount+"）";
    Cyan.$("wsPageCount").value=result.pageCount;
    Cyan.$("wsPageNo").value=result.pageNo;
    $$("#t1")[0].innerHTML=headhtml+content+tableEnd+foothtml;
}

function createEmailHtml(result){
    var headhtml="<table width='100%' border='0' cellspacing='1' cellpadding='0' class='box1'><tr><td class='td1' >标题</td><td class='td1'>发件人</td><td class='td1'>来信部门</td><td class='td1'>接受时间</td><td class='td1'>大小</td></tr>";

    var emailList=result.emailList;
    var content=""
    for(var i=0;i<emailList.length;i++){
        var item=emailList[i];
        content+="<tr><td><div  title='"+item.title+"' style='text-align: left' >"
        var classTag="";
        if(item.replyed!=null&&item.replyed){
            classTag="title_replyed"
        }else{
            if(item.readTime==null){
                classTag="title_new"
            }else{
                classTag="title_old"
            }
        }
        content+="<span class='title "+classTag+"'><a href='javascript:void(0);'"
        if(item.color!=null){
            content+= "style='color:"+item.color+"'"
        }
        content+= "onclick='showMail("+item.mailId+");return false;'>"+item.title+"</a></span></div></td>"
        content+="<td><span>"+item.senderName+"</span></td><td><span>"+item.allDeptName+"</span></td><td><span>"
            +item.acceptTime+"</span></td><td><span>"+item.mailSize+"</span></td></tr>"

    }
    var tableEnd="</table>";
    var foothtml="";
    foothtml+="<div id='foot' class='fy' ><div class='fy_lt'><p>共"+result.pageCount+"页</p><p>当前第"+result.pageNo+"页</p>"
    if(result.pageNo==1){
        foothtml+="<p><span class='no_click_a'>首页</span></p><p ><span class='no_click_a'>上一页</span></p>"
    }else{
        foothtml+="<p><a href='javascript:void(0);' onclick=pageSearch('first')>首页</a></p>"
        foothtml+="<p><a href='javascript:void(0);' onclick=pageSearch('previous')>上一页</a></p>"
    }
    foothtml+="<p><a href='javascript:void(0);' onclick='pageSearch()'>刷新</a></p>"
    if(result.pageNo>=result.pageCount){
        foothtml+="<p ><span class='no_click_a'>下一页</span><p ><span class='no_click_a'>尾页</span></p>"
    }else{
        foothtml+="<p><a href='javascript:void(0);' onclick=pageSearch('next')>下一页</a></p>"
        foothtml+="<p><a href='javascript:void(0);' onclick=pageSearch('last')>尾页</a></p>"
    }
    foothtml+="</div> <div class='fy_zj'><input onkeyup='keySearch(2)' type='text' name='emailTurnTo' value='"+result.pageNo+"'/><p>页</p></div>"
    foothtml+="<div class='fy_rt'><a href='javascript:void(0);' id='goto' onclick=pageSearch('turnToPage')>跳转</a></div></div>"
    Cyan.$("emailpageCount").value=result.pageCount;
    Cyan.$("emailPageNo").value=result.pageNo;
    $$("#t2")[0].innerHTML=headhtml+content+tableEnd+foothtml;
}

function createMenuHtml(result){
    var contenthtml="<div class='gn'><ul>";
    var menuList=result.menuList;
    for(var i=0;i<menuList.length;i++){
        var item=menuList[i];
        contenthtml+="<li><a href='javascript:void(0);' onclick=openHref('"+item.menuId+"','"+item.url+"','"+item.menuTitle+"')>"+item.menuTitle+"</a>"
    }
    contenthtml+="</ul></div>"
    $$("#t3")[0].innerHTML=contenthtml;
}

function createUserHtml(result){
    var contenthtml="<div class='yh'>";
    var userList=result.userList;
    var headMap=result.headMap;
    for(var i=0;i<userList.length;i++){
        var item=userList[i];
        contenthtml+="<dl>"
        var headUrl=headMap[item.userId];
        if(headUrl==null){
            headUrl="/oa/superquery/"+item.userId+"/photo";
        }
        contenthtml+="<dt><img src='"+headUrl+"' width='100' height='116'/></dt>"
        contenthtml+="<dd><img class='user_small_image' src='/oa/superquery/image/txl_tb_06.jpg' width='16' height='16'/>"
        if(item.userName==null){
            contenthtml+="<div>暂无</div>"
        }else{
            contenthtml+="<div title='"+item.userName+"'>"+item.userName+"</div>"
        }
        contenthtml+="</dd><dd><img class='user_small_image' src='/oa/superquery/image/txl_tb_09.jpg' width='16' height='16'/>"
        if(item.allDeptName==null){
            contenthtml+="<div >暂无</div>"
        }else{
            contenthtml+="<div title='"+item.allDeptName+"'>"+item.allDeptName+"</div>"
        }
        contenthtml+="</dd><dd><img class='user_small_image' src='/oa/superquery/image/txl_tb_11.jpg' width='16' height='16'/>"
        if(item.duty==null||item.duty==""){
            contenthtml+="<div >暂无</div>"
        }else{
            contenthtml+="<div title='"+item.duty+"'>"+item.duty+"</div>"
        }
        var officePhone = item.officePhone;
        if(officePhone==null){
            officePhone="暂无";
        }
        var phone = item.phone;
        if(phone==null){
            phone="暂无";
        }
        contenthtml+="</dd><dd><img class='user_small_image' src='/oa/superquery/image/txl_tb_13.jpg' width='16' height='16'/>"+officePhone+"</dd>";
        contenthtml+="<dd class='dd1'><img class='user_small_image' src='/oa/superquery/image/txl_tb_15.jpg' width='16' height='16'/>"+phone+"</dd></dl>";
    }

    contenthtml+="</div>"
    $$("#t4")[0].innerHTML=contenthtml;
}


