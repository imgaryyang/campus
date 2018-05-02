function addElement(type, typeName, content)
{
    var tbody = Cyan.$$("#elements tbody")[0];
    var trs = Cyan.$$("#elements tr");
    var tr = trs[trs.length - 1];

    var newTr = document.createElement("tr");

    for (var i = 0; i < tr.childNodes.length; i++)
    {
        var td = tr.childNodes[i];
        if (td.nodeName == "TD")
        {
            var newTd = document.createElement("td");
            newTd.className = td.className;
            newTr.appendChild(newTd);

            if (td.className == "element_content")
            {
                var typeElement = document.createElement("INPUT");
                typeElement.type = "hidden";
                typeElement.name = "types";
                typeElement.value = type;
                newTd.appendChild(typeElement);

                var div = document.createElement("DIV");
                div.innerHTML = content;
                newTd.appendChild(div);
            }
            else if (td.className == "element_type")
            {
                newTd.innerHTML = typeName;
            }
            else
            {
                newTd.innerHTML = td.innerHTML;
                var span = td.childNodes[0];
                if (span && span.nodeName == "SPAN" && span.onclick)
                {
                    if (!newTd.childNodes[0].onclick)
                        newTd.childNodes[0].onclick = span.onclick;
                }
            }
        }
    }

    tbody.appendChild(newTr);
}

function addText()
{
    addElement("string", "字符", "<input name=\"texts\" value=\"\" class=\"text\">");
}

function addYear()
{
    addElement("year", "年号", "年号");
}

function addSerial()
{
    addElement("serial", "流水号", "名称:<input name=\"serialNames\" value=\"\" class=\"serialName\">" +
    "\r\n长度:<input name=\"serialLens\" value=\"\" class=\"serialLen\">" +
    "\r\n年份:<input name=\"serialYears\" value=\"\" class=\"serialYear\">");
}

function addDate()
{
    addElement("date", "日期", "日期格式:<input name=\"dateFormats\" value=\"\" class=\"dateFormat\">");
}

function addVar()
{
    addElement("var", "变量", "变量名称:<input name=\"varNames\" value=\"\" class=\"varName\">");
}

function upElement(obj)
{
    var tr = obj.parentNode.parentNode;
    var previous = tr.previousSibling;
    while (previous && previous.nodeName != "TR")
        previous = previous.previousSibling;
    if (previous)
        tr.parentNode.insertBefore(tr, previous);
}

function downElement(obj)
{
    var tr = obj.parentNode.parentNode;
    var next = tr.nextSibling;
    while (next && next.nodeName != "TR")
        next = next.nextSibling;
    if (next)
        tr.parentNode.insertBefore(next, tr);
}

function deleteElement(obj)
{
    var tr = obj.parentNode.parentNode;
    if (tr.rowIndex == 1)
    {
        var next = tr.nextSibling;
        while (next && next.nodeName != "TR")
            next = next.nextSibling;
        if (!next)
        {
            $.message("至少需要一个元素");
            return;
        }
    }
    tr.parentNode.removeChild(tr);
}

var textIndex;
var serialIndex;
var dateIndex;
var varIndex;

function ok()
{
    textIndex = 0;
    serialIndex = 0;
    dateIndex = 0;
    varIndex = 0;

    var value = "";
    var text = "";
    var types = Cyan.$$("#types");
    for (var i = 0; i < types.length; i++)
    {
        var type = types[i].value;

        var element = null;
        if (type == "string")
        {
            element = getText();
        }
        else if (type == "year")
        {
            element = getYear();
        }
        else if (type == "serial")
        {
            element = getSerial();
        }
        else if (type == "date")
        {
            element = getDate();
        }
        else if (type == "var")
        {
            element = getVar();
        }

        if (element == null)
            return;

        value += element.value;
        text += element.text;
    }

    Cyan.Window.closeWindow({value: value, text: text});
}

function getText()
{
    var s = Cyan.$$("#texts")[textIndex++].value;
    return {value: s, text: s};
}

function getYear()
{
    return {value: "$year()", text: "年号"};
}

function getSerial()
{
    var serialElement = Cyan.$$("#serialNames")[serialIndex];
    var lenElement = Cyan.$$("#serialLens")[serialIndex];
    var yearElement = Cyan.$$("#serialYears")[serialIndex];
    serialIndex++;

    var serialName = serialElement.value;
    var len = lenElement.value;
    var year = yearElement.value;

//    if (!serialName)
//    {
//        $.message("请输入流水号名称");
//        try
//        {
//            serialElement.focus();
//        }
//        catch (e)
//        {
//        }
//        return null;
//    }

    if (len && !Cyan.Validator.isInteger(len))
    {
        Cyan.message("流水号长度必须是整数");
        try
        {
            lenElement.focus();
        }
        catch (e)
        {
        }
        return null;
    }

    if (year && !Cyan.Validator.isPositiveInteger(year))
    {
        Cyan.message("年份必须是正整数");
        try
        {
            yearElement.focus();
        }
        catch (e)
        {
        }
        return null;
    }


    var value = "$serial(" + serialName + "," + (len ? len : 0);

    if (year != "")
        value += "," + year;

    value += ")";
    return {value: value, text: "流水号"};
}

function getDate()
{
    var dateElement = Cyan.$$("#dateFormats")[dateIndex++];
    var dateFormat = dateElement.value;

    if (!dateFormat)
    {
        Cyan.message("请输入日期格式");
        try
        {
            dateElement.focus();
        }
        catch (e)
        {
        }
        return null;
    }

    return {value: "$date(" + dateFormat + ")", text: "日期"};
}

function getVar()
{
    var varElement = Cyan.$$("#varNames")[varIndex++];
    var varName = varElement.value;

    if (!varName)
    {
        Cyan.message("请输入变量名称");
        try
        {
            varElement.focus();
        }
        catch (e)
        {
        }
        return null;
    }

    return {value: "$var(" + varName + ")", text: "{" + varName + "}"};
}