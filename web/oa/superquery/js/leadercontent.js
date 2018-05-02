Cyan.onload(function () {
    Cyan.Arachne.form.pageNo = 1;
    searchLeaderCheckBox();
});

function createLeaderDocument(){
    Cyan.Arachne.form.searchType = 1;
    leaderSearch({
        wait:false,
        callback : function (result) {
            Cyan.$("condition").value=result.condition;
            if (result) {
                createLeaderDocumentHtml(result);
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

function createMenuHtml(result){
    var contenthtml="<div class='gn'><ul>";
    var menuList=result.menuList;
    for(var i=0;i<menuList.length;i++){
        var item=menuList[i];
        contenthtml+="<li><a href='javascript:void(0);' onclick=openHref('"+item.menuId+"','"+item.url+"','"+item.menuTitle+"')>"+item.menuTitle+"</a>"
    }
    contenthtml+="</ul></div>"
    $$("#t2")[0].innerHTML=contenthtml;
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
    $$("#t3")[0].innerHTML=contenthtml;
}

function createLeaderDocumentHtml(result){
    var headhtml="<table width='100%' border='0' cellspacing='1' cellpadding='0' class='box1'><tr>"+
            "<td class='td1' width='15%'>标题</td> <td class='td1'>发文字号</td> <td class='td1'>来文单位</td>" +
        "<td class='td1'>编号</td> <td class='td1'>类型</td> <td class='td1'>拟稿时间/收文时间</td> <td class='td1'>拟稿人/接收人</td></tr>"

    var odFlowInstance=result.odFlowInstance;
    var content=""
    for(var i=0;i<odFlowInstance.length;i++){
        var item=odFlowInstance[i];
        var titleText=item.titleText!=null?item.titleText:"无标题";
        var sendNumber=item.sendNumber!=null?item.sendNumber:"";
        var serial=item.serial!=null?item.serial:"";
        var sourceDept=item.sourceDept!=null?item.sourceDept:"";
        var userName=item.userName!=null?item.userName:"";
        var typeName=item.typeName!=null?item.typeName:"";
        var startTime=item.startTime!=null?item.startTime:"";

        content+="<tr><td style='text-align: left'><span class='a_suspension_points' title='"+ titleText+"' >" +
            "<a href='javascript:void(0);' onclick='openDocument("+item.instanceId+",true);return false;'>"+titleText +"</a></span></td>"+
            "<td><span>"+sendNumber+"</span></td>" +
            "<td><span>"+sourceDept+"</span></td>"+
            "<td><span>"+serial+"</span></td>" +
            "<td><span>"+typeName+ "</span></td>" +
            "<td style='text-align: left'><span>"+startTime+"</span></td>" +
            "<td><span>"+userName+"</span></td></tr>"
    }
    var tableEnd="</table>";
    var foothtml="";
    foothtml+="<div id='foot' class='fy' ><div class='fy_lt'><p>共"+result.pageCount+"页</p><p>当前第"+result.pageNo+"页</p>"
    if(result.pageNo==1){
        foothtml+="<p><span class='no_click_a'>首页</span></p><p ><span class='no_click_a'>上一页</span></p>"
    }else{
        foothtml+="<p><a href='javascript:void(0);' onclick=leaderDocumentPageSearch('first')>首页</a></p>"
        foothtml+="<p><a href='javascript:void(0);' onclick=leaderDocumentPageSearch('previous')>上一页</a></p>"
    }
    foothtml+="<p><a href='javascript:void(0);' onclick='leaderDocumentPageSearch()'>刷新</a></p>"
    if(result.pageNo>=result.pageCount){
        foothtml+="<p ><span class='no_click_a'>下一页</span><p ><span class='no_click_a'>尾页</span></p>"
    }else{
        foothtml+="<p><a href='javascript:void(0);' onclick=leaderDocumentPageSearch('next')>下一页</a></p>"
        foothtml+="<p><a href='javascript:void(0);' onclick=leaderDocumentPageSearch('last')>尾页</a></p>"
    }

    foothtml+="</div> <div class='fy_zj'><input onkeyup='keySearch(5)' type='text' name='wsTurnTo' value='"+result.pageNo+"'/><p>页</p></div>"
    foothtml+="<div class='fy_rt'><a href='javascript:void(0);' id='leadergoto' onclick=leaderDocumentPageSearch('turnToPage')>跳转</a></div></div>"
    $$("#wsCount")[0].innerText="公文（"+result.odCount+"）";
    Cyan.$("wsPageCount").value=result.pageCount;
    Cyan.$("wsPageNo").value=result.pageNo;
    $$("#t1")[0].innerHTML=headhtml+content+tableEnd+foothtml;
}


