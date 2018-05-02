function showlog(projectCode) {
    if(!projectCode) return;

    var logdiv = document.getElementById("logdiv");
    if (logdiv) {
        logdiv.style.display = "block";
    } else {
        var ajax = new Cyan.Ajax();
        ajax.method = "GET";
        ajax.setRequestHeader("Accept", "text/json");
        ajax.call(Cyan.formatUrl("/oa/version/versionlog?project.projectCode=" + projectCode));
        ajax.handleObject = function (result) {
            creatview(result);
        }
    }
}

function creatview(result) {
    var topdiv = document.createElement("div");
    topdiv.setAttribute("id", "logdiv");
    topdiv.setAttribute("class", "zj");
    logdiv = document.createElement("div");
    logdiv.setAttribute("class", "tk_xxnr");
    topdiv.appendChild(logdiv);

    var div1 = document.createElement("div");
    div1.setAttribute("class", "tk_lb")
    logdiv.appendChild(div1);
    var a = document.createElement("a");
    a.setAttribute("class", "gban")
    a.setAttribute("href", "javascript:void(0)")
    a.setAttribute('onclick', 'closeView()');
    div1.appendChild(a)

    var div2 = document.createElement("div");
    div2.setAttribute("class", "tk_sb")
    div1.appendChild(div2);

    var dl = document.createElement("dl");
    div2.appendChild(dl);

    var dt = document.createElement("dt");
    dl.appendChild(dt);
    var img0 = document.createElement("img");
    img0.src = "/versionLog/image/tk_14.jpg";
    img0.setAttribute("width", "114");
    img0.setAttribute("height", "115");
    dt.appendChild(img0);

    var dd1 = document.createElement("dd");
    var img1 = document.createElement("img");
    img1.src = "/versionLog/image/tk_11.jpg";
    dd1.appendChild(img1);
    dl.appendChild(dd1);
    dd1.appendChild(document.createTextNode("技术支持：" + result.support));

    var dd2 = document.createElement("dd");
    dl.appendChild(dd2);
    var img2 = document.createElement("img");
    img2.src = "/versionLog/image/tk_17.jpg";
    dd2.appendChild(img2);
    dd2.appendChild(document.createTextNode("服务热线：" + result.phone));

    var dd3 = document.createElement("dd");
    dl.appendChild(dd3)
    var img3 = document.createElement("img");
    img3.src = "/versionLog/image/tk_20.jpg";
    dd3.appendChild(img3);
    dd3.appendChild(document.createTextNode("版本：" + result.currVer));

    var div3 = document.createElement("div");
    div3.setAttribute("class", "tk_xb")
    logdiv.appendChild(div3);

    var div4 = document.createElement("div")
    div4.setAttribute("class", "fbrj")
    var span = document.createElement("span")
    span.appendChild(document.createTextNode("发布日记"));
    div4.appendChild(span)
    div3.appendChild(div4)

    var div5 = document.createElement("div");
    div5.setAttribute("class", "fbrj_hz")
    div3.appendChild(div5)

    var verList = result.verList;
    for (var i = 0; i < verList.length; i++) {
        var listdiv = document.createElement("div");
        listdiv.setAttribute("class", "fbrj_rq");
        var h2 = document.createElement("h2");
        h2.appendChild(document.createTextNode(verList[i].date + "发布内容"));
        listdiv.appendChild(h2);
        var contentdiv = document.createElement("div");
        contentdiv.innerHTML = Cyan.escapeHtml(verList[i].content);
        contentdiv.setAttribute("class", "contentdiv")
        listdiv.appendChild(contentdiv);
        div5.appendChild(listdiv)
    }

    var maskdiv = document.createElement("div");
    maskdiv.setAttribute("class", "tk");
    topdiv.appendChild(maskdiv)

    document.body.appendChild(topdiv)
}

function closeView() {
    var logdiv = document.getElementById("logdiv");
    logdiv.style.display = "none";
}
