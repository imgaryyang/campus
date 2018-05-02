if (!window.System)
    window.System = {};

System.HttpUploader = function ()
{
};

System.HttpUploader.prototype.create = function ()
{
};

System.HttpUploader.createXMLDOM = function ()
{
    var arr = ["MSXML2.DOMDocument.5.0", "MSXML2.DOMDocument.4.0", "MSXML2.DOMDocument.3.0", "MSXML2.DOMDocument",
        "Microsoft.XmlDom"];
    for (var i = 0; i < arr.length; i++)
    {
        try
        {
            return new ActiveXObject(arr[i]);
        }
        catch (oError)
        {
        }
    }
    throw new Error("MSXML is not install on your system.");
};

System.HttpUploader.prototype.post = function (path, url, args, calback)
{
    var xml_dom = System.HttpUploader.createXMLDOM();
    xml_dom.loadXML('<?xml   version="1.0" ?><root/>');
    xml_dom.documentElement.setAttribute("xmlns:dt", "urn:schemas-microsoft-com:datatypes");

    var l_node1 = xml_dom.createElement("file");
    l_node1.dataType = "bin.base64";

    var ado_stream = new ActiveXObject("ADODB.Stream");
    ado_stream.Type = 1;
    ado_stream.Open();
    ado_stream.LoadFromFile(path);

    l_node1.nodeTypedValue = ado_stream.Read(-1);
    ado_stream.Close();

    xml_dom.documentElement.appendChild(l_node1);

    var base64 = l_node1.text;

    var obj = {base64: base64};

    if (args)
    {
        for (name in args)
            obj[name] = args[name];
    }

    Cyan.Arachne.post(url, obj, calback);
};
