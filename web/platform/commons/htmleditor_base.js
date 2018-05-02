function createVideoHTML(path, name, width, height)
{
    var ext = Cyan.getExtName(name);
    if (ext)
        ext = ext.toLowerCase();
    if (ext == "avi" || ext == "wmv")
    {
        return "<object classid='clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA' width='" + width + "' height='" + height +
                "'><embed width='" + width + "' height='" + height +
                "' border='0' showdisplay='0' showcontrols='1' autostart='1' autorewind='0'" +
                "playcount='0' src='" + path + "'></embed></object>";
    }
    else
    {
        var src = "/web/player/flowplayer.swf";
        var flashVars = 'config={"playerId":"player","clip":{"url":"' + path + '"},"playlist":[{"url":"' + path +
                '"}]}';
        return '<object width="' + width + '" height="' + height +
                '" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">' +
                '<param name="FlashVars" value=\'' + flashVars + '\'>' +
                '<param name="bgcolor" value="#000000">' +
                '<param name="movie" value="' + src + '">' +
                '<param name="src" value="' + src + '">' +
                '<embed src="' + src + '" type="application/x-shockwave-flash" width="' + width + '" height="' +
                height + '" FlashVars=\'' + flashVars + '\' bgcolor="#000000"></embed></object>';
    }
}