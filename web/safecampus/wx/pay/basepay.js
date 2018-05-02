var charsetStr = "utf-8";

function getXmlHttp() {
    var xmlhttp = false;
    // branch for native XMLHttpRequest object
    if (window.XMLHttpRequest) {
        try {
            xmlhttp = new XMLHttpRequest();
        }
        catch (e) {
            xmlhttp = false;
        }
    }
    // branch for IE/Windows ActiveX version
    else if (window.ActiveXObject) {
        try {
            xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {
                xmlhttp = false;
            }
        }
    }
    return xmlhttp;
}

function getNowFormatDate() {
    var date = new Date();

    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }

    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var hours = date.getHours();
    if (hours >= 0 && hours <= 9) {
        hours = "0" + hours;
    }
    var minutes = date.getMinutes();
    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    var seconds = date.getSeconds();
    if (seconds >= 0 && seconds <= 9) {
        seconds = "0" + seconds;
    }
    var currentdate = date.getFullYear() + month + strDate + hours + minutes + seconds;
    return currentdate;
}

function sendXmlRequest(jsonRequest, url) {
    var xmlhttpObj = getXmlHttp();
    if (xmlhttpObj) {
        var params = "jsonRequestData=" + jsonRequest;
        xmlhttpObj.open("POST", url, false);
        xmlhttpObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xmlhttpObj.setRequestHeader("charset", charsetStr);
        xmlhttpObj.send(params);
        return xmlhttpObj.responseText;
    }
    else {
        alert("您的浏览器版本太低，请升级到IE5.0以上版本");
    }
}
function sendXmlRequestByParams(params, url) {
    var xmlhttpObj = getXmlHttp();
    if (xmlhttpObj) {
        xmlhttpObj.open("POST", url, false);
        xmlhttpObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xmlhttpObj.setRequestHeader("charset", charsetStr);
        xmlhttpObj.send(params);
        return xmlhttpObj.responseText;
    }
    else {
        alert("您的浏览器版本太低，请升级到IE5.0以上版本");
    }
}

//发送表单数据
function sendFormRequest(xmlRequest, url) {
    var form = document.createElement("form");
    document.body.appendChild(form);
    var param = document.createElement("input");
    param.type = "hidden";
    param.value = xmlRequest;
    param.name = "jsonRequestData";
    form.appendChild(param);
    form.action = url;
    form.method = "POST";
    form.accept_charset = charsetStr;
    form.submit();
}