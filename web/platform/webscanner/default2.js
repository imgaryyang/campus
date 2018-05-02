Cyan.importCss("/platform/webscanner/scanner.css");

System.Scanner = {
    images: [],
    index: 1,
    resize: function ()
    {
        var bodyHeight = document.documentElement.clientHeight;
        var buttomHeight = Cyan.Elements.getComponentSize(Cyan.$("scanner_buttom")).height;

        var scanner = Cyan.$("scanner");
        var scannerHeight = Cyan.Elements.getComponentSize(scanner).height;

        var maxHeight = bodyHeight - buttomHeight - 10;

        if (scannerHeight > maxHeight)
        {
            scanner.style.height = maxHeight + "px";
            scanner.style.width = (maxHeight * 1.8) + "px";
        }
    },
    init: function ()
    {
        if (System.Scanner.inited)
            return;

        var overlayDiv = document.createElement("DIV");
        overlayDiv.id = "scanner_overlay";
        overlayDiv.style.display = "none";
        document.body.appendChild(overlayDiv);

        var s = "<div style='display: none'><OBJECT style='left:0;top:0;height:100px;width:100px' classid='clsid:" +
                System.Scanner.classId +
                "' id='" + System.Scanner.id + "'";

        if (System.Scanner.codebase)
            s += " codebase='" + System.Scanner.codebase + "'";

        s += "></OBJECT></div><div style='width100%;height: 100%;'><img id='scanner_image'></div>";

        var scannerDiv = document.createElement("DIV");
        scannerDiv.id = "scanner";
        overlayDiv.appendChild(scannerDiv);

        scannerDiv.innerHTML = s;

        var scannerButtomDiv = document.createElement("DIV");
        scannerButtomDiv.id = "scanner_buttom";
        overlayDiv.appendChild(scannerButtomDiv);

        var imagesDiv = document.createElement("DIV");
        imagesDiv.id = "scanner_images";
        scannerButtomDiv.appendChild(imagesDiv);

        var imageLeft = document.createElement("DIV");
        imageLeft.id = "scanner_imageLeft";
        imageLeft.onclick = function ()
        {
            var left = imageListDiv.scrollLeft - 118;
            if (left < 0)
                left = 0;

            imageListDiv.scrollLeft = left;
        };
        imagesDiv.appendChild(imageLeft);

        var imageListDiv = document.createElement("DIV");
        imageListDiv.id = "scanner_imageList";
        imagesDiv.appendChild(imageListDiv);

        var imageListInnerDiv = document.createElement("DIV");
        imageListInnerDiv.id = "scanner_imageListInner";
        imageListDiv.appendChild(imageListInnerDiv);

        var imageRight = document.createElement("DIV");
        imageRight.id = "scanner_imageRight";
        imageRight.onclick = function ()
        {
            imageListDiv.scrollLeft = imageListDiv.scrollLeft + 118;
        };
        imagesDiv.appendChild(imageRight);

        var buttonsDiv = document.createElement("DIV");
        buttonsDiv.id = "scanner_buttons";
        scannerButtomDiv.appendChild(buttonsDiv);

        function addButton(id, text, action)
        {
            var span = document.createElement("SPAN");
            span.className = "scanner_button";

            var button = document.createElement("BUTTON");
            button.id = id;
            button.innerHTML = text;
            button.onclick = action;

            span.appendChild(button);

            buttonsDiv.appendChild(span);
        }

        addButton("bt_scanner", "扫描", System.Scanner.scan0);
        addButton("bt_scanner_ok", "保存", System.Scanner.ok);
        addButton("bt_scanner_delete", "删除选中的图片", function ()
        {
            System.Scanner.deleteImg();
        });
        addButton("bt_scanner_clear", "删除所有图片", System.Scanner.clear);
        addButton("bt_scanner_cancel", "关闭", System.Scanner.close);

        Cyan.$("scanner_image").onclick = function ()
        {
            if (this.src)
                window.open(this.src);
        };

        if (System.Scanner.initActiveX)
            System.Scanner.initActiveX();

        if (System.Scanner.setUnload)
            System.Scanner.setUnload();

        System.Scanner.inited = true;
    },
    scanAndStore: function (format, callback, attachmentId)
    {
        System.Scanner.init();
        System.Scanner.setFormat(format);
        Cyan.$("scanner_overlay").style.display = "";
        Cyan.$("scanner_image").style.display = "none";

        if (System.Scanner.start)
            System.Scanner.start();

        System.Scanner.callback = callback;

        setTimeout(System.Scanner.resize, 100);
    },
    ok: function ()
    {
        var images = System.Scanner.images;
        if (System.Scanner.stop)
            System.Scanner.stop();

        System.Scanner.clear();

        var overlay = Cyan.$("scanner_overlay");
        if (overlay)
            overlay.style.display = "none";

        if (images && images.length)
        {
            var index = 0;
            var n = images.length;
            var paths = [];
            var f = function ()
            {
                Cyan.displayProgress(index / n, "正在上传图片", "正在上传第" + (index + 1) + "张图片");

                var image = images[index++];
                if (image.id)
                {
                    paths.push({id: image.id, path: image.getPath()});
                }
                else
                {
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
                                        if (System.Scanner.callback)
                                            System.Scanner.callback(paths);

                                        System.Scanner.callback = null;
                                    }
                                }
                                else
                                {
                                    alert("扫描错误");
                                    Cyan.displayProgress(-1, "", "");
                                    System.Scanner.callback = null;
                                }
                            }
                    );
                }
            };

            f();
        }
    },
    close: function ()
    {
        if (System.Scanner.stop)
        {
            try
            {
                System.Scanner.stop();
            }
            catch (e)
            {
            }
        }

        System.Scanner.clear();

        var overlay = Cyan.$("scanner_overlay");
        if (overlay)
            overlay.style.display = "none";

        System.Scanner.callback = null;
    },
    addImg: function (image)
    {
        System.Scanner.images.push(image);

        var f = function ()
        {
            var img = document.createElement("img");
            img.src = image.getPath();
            img.onclick = System.Scanner.imgClick;

            Cyan.$("scanner_imageListInner").appendChild(img);
        };
        if (image.getPath)
        {
            f();
        }
        else
        {
            Cyan.Arachne.post("/richupload/base64", [image.getBase64(), (System.Scanner.index++ ) + ".jpg"],
                    function (path)
                    {
                        if (path)
                        {
                            image.id = path;
                            image.getPath = function ()
                            {
                                return "/richupload/thumb?path=" + path;
                            };
                            f();
                        }
                        else
                        {
                            alert("扫描错误");
                        }
                    }
            );
        }
    },
    imgClick: function ()
    {
        var childElements = Cyan.$$("#scanner_imageListInner").childElements();
        var selectedImg = childElements.searchFirst(function ()
        {
            return this.className == "selected";
        });

        if (selectedImg)
            selectedImg.className = "";

        this.className = "selected";

        var index = Cyan.Array.indexOf(childElements, this);

        var image = Cyan.$("scanner_image");
        image.style.width = "";
        image.style.height = "";
        image.src = System.Scanner.images[index].getPath();
        image.style.display = "";

        var div = image.parentNode;
        var size = Cyan.Elements.getComponentSize(div);

        var width = image.width;
        var height = image.height;

        if (width > size.width)
        {
            height = height * size.width / width;
            width = size.width;
        }

        if (height > size.height)
        {
            width = width * size.height / height;
            height = size.height;
        }

        image.style.width = width + "px";
        image.style.height = height + "px";
    },
    getSelectedIndex: function ()
    {
        var index = Cyan.$$("#scanner_imageListInner").childElements().each(function (index)
        {
            if (this.className == "selected")
                return index;
        });

        if (index == null)
            index = -1;

        return index;
    },
    clear: function ()
    {
        System.Scanner.images = [];
        System.Scanner.index = 1;
        Cyan.$("scanner_imageListInner").innerHTML = "";

        Cyan.$("scanner_image").style.display = "none";
    },
    deleteImg: function (index)
    {
        if (index == null)
            index = System.Scanner.getSelectedIndex();

        if (index != null && index >= 0)
        {
            Cyan.Array.remove(System.Scanner.images, index);
            var imagesDiv = Cyan.$("scanner_imageListInner");
            imagesDiv.removeChild(imagesDiv.childNodes[index]);

            Cyan.$("scanner_image").style.display = "none";
        }
        else
        {
            alert("请选择要删除的图片");
        }
    },
    scan0: function ()
    {
        System.Scanner.scan();
        var images = System.Scanner.getImages();
        if (images && images.length)
        {
            for (var i = 0; i < images.length; i++)
                System.Scanner.addImg(images[i]);
        }
    },
    getActiveX: function ()
    {
        return Cyan.$(System.Scanner.id);
    }
};

Cyan.importJs("/platform/webscanner/imp.js");