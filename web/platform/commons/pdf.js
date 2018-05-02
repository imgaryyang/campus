(function ()
{
    var showPdf = function (path)
    {
        if (Cyan.navigator.isIE() && Cyan.navigator.version > 8)
        {
            window.open("/pdfjs/viewer/viewer.html?file=" + encodeURIComponent(path));
        }
        else
        {
            path += ( path.indexOf("?") >= 0 ? "&" : "?") + "show=true";
            window.open(path);
        }
    };
    if (window.System)
        System.showPdf = showPdf;
    else
        window.showPdf = showPdf;
})();