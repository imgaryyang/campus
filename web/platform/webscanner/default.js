System.Scanner = {
    index: 1,
    init: function ()
    {
        if (System.Scanner.inited)
            return;

        var s = "<OBJECT style='left:0;top:0;height:100px;width:100px' classid='clsid:" + System.Scanner.classId +
                "' id='" + System.Scanner.id + "'";

        if (System.Scanner.codebase)
            s += " codebase='" + System.Scanner.codebase + "'";
        s += "></OBJECT>";

        var div = document.createElement("DIV");
        div.style.display = "none";
        document.body.appendChild(div);

        div.innerHTML = s;
        div.style.width = "100px";
        div.style.height = "100px";

        if (System.Scanner.initActiveX)
            System.Scanner.initActiveX();

        if (System.Scanner.setUnload)
            System.Scanner.setUnload();

        System.Scanner.inited = true;
    },
    getActiveX: function ()
    {
        return Cyan.$(System.Scanner.id);
    },
    scanAndStore: function (format, callback)
    {
        System.Scanner.init();
        System.Scanner.setFormat(format);
        System.Scanner.scan();
        var images = System.Scanner.getImages();
        if (images && images.length)
        {
            var index = 0;
            var n = images.length;
            var paths = [];
            var f = function ()
            {
                Cyan.displayProgress(index / n, "正在上传图片", "正在上传第" + (index + 1) + "张图片");

                var image = images[index++];
                var base64 = image.getBase64();
                Cyan.Arachne.post("/richupload/base64", [base64, (System.Scanner.index++ ) + ".jpg"],
                        function (path)
                        {
                            if (path)
                            {
                                paths.push({id: path.toString(), path: "/richupload/thumb?path=" + path});
                                if (index < n)
                                {
                                    f();
                                }
                                else
                                {
                                    Cyan.displayProgress(-1, "", "");
                                    if (callback)
                                    {
                                        callback(paths);
                                    }
                                }
                            }
                            else
                            {
                                alert("扫描错误");
                                Cyan.displayProgress(-1, "", "");
                            }
                        }
                );
            };

            f();
        }
    }
};

Cyan.importJs("/platform/webscanner/imp.js");