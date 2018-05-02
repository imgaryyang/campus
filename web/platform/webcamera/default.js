Cyan.importCss("/platform/webcamera/camera.css");
System.Camera = {
    images: [],
    index: 1,
    resize: function ()
    {
        var bodyHeight = document.documentElement.clientHeight;
        var buttomHeight = Cyan.Elements.getComponentSize(Cyan.$("camera_buttom")).height;

        var camera = Cyan.$("camera");
        var cameraHeight = Cyan.Elements.getComponentSize(camera).height;

        var maxHeight = bodyHeight - buttomHeight - 10;

        if (cameraHeight > maxHeight)
        {
            camera.style.height = maxHeight + "px";
            camera.style.width = (maxHeight * 1.8) + "px";
        }
    },
    init: function (callback)
    {
        if (System.Camera.inited)
        {
            if (callback)
                callback();

            return;
        }

        var overlayDiv = document.createElement("DIV");
        overlayDiv.id = "camera_overlay";
        overlayDiv.style.display = "none";
        document.body.appendChild(overlayDiv);

        var cameraDiv = document.createElement("DIV");
        cameraDiv.id = "camera";
        cameraDiv.className = "camera";
        overlayDiv.appendChild(cameraDiv);

        var obj = Cyan.$(System.Camera.id);
        if (obj)
        {
            obj.style.display = "";
            if (!Cyan.navigator.isFF())
            {
                cameraDiv.appendChild(obj);
            }
        }
        else
        {
            if (Cyan.navigator.isFF())
            {
                var s = "<img id=\"" + System.Camera.id + "\" style='left:0;top:0;height:100%;width:100%' src=\"" +
                        System.Camera.cameraDefaultUrl + "\" />";
                cameraDiv.innerHTML = s;
            }
            else
            {
                var s = "<OBJECT style='left:0;top:0;height:100%;width:100%' classid='clsid:" + System.Camera.classId +
                        "' id='" + System.Camera.id + "'";

                if (System.Camera.codebase)
                    s += " codebase='" + System.Camera.codebase + "'";

                s += "></OBJECT>";
                cameraDiv.innerHTML = s;
            }
        }

        var cameraButtomDiv = document.createElement("DIV");
        cameraButtomDiv.id = "camera_buttom";
        overlayDiv.appendChild(cameraButtomDiv);

        var imagesDiv = document.createElement("DIV");
        imagesDiv.id = "camera_images";
        cameraButtomDiv.appendChild(imagesDiv);

        var imageLeft = document.createElement("DIV");
        imageLeft.id = "camera_imageLeft";
        imageLeft.onclick = function ()
        {
            var left = imageListDiv.scrollLeft - 118;
            if (left < 0)
                left = 0;

            imageListDiv.scrollLeft = left;
        };
        imagesDiv.appendChild(imageLeft);

        var imageListDiv = document.createElement("DIV");
        imageListDiv.id = "camera_imageList";
        imagesDiv.appendChild(imageListDiv);

        var imageListInnerDiv = document.createElement("DIV");
        imageListInnerDiv.id = "camera_imageListInner";
        imageListDiv.appendChild(imageListInnerDiv);

        var imageRight = document.createElement("DIV");
        imageRight.id = "camera_imageRight";
        imageRight.onclick = function ()
        {
            imageListDiv.scrollLeft = imageListDiv.scrollLeft + 118;
        };
        imagesDiv.appendChild(imageRight);

        var buttonsDiv = document.createElement("DIV");
        buttonsDiv.id = "camera_buttons";
        cameraButtomDiv.appendChild(buttonsDiv);

        function addButton(id, text, action)
        {
            var span = document.createElement("SPAN");
            span.className = "camera_button";

            var button = document.createElement("BUTTON");
            button.id = id;
            button.innerHTML = text;
            button.onclick = action;

            span.appendChild(button);

            buttonsDiv.appendChild(span);
        }

        addButton("bt_camera", "拍照", System.Camera.camera);
        if (System.Camera.switchCamera)
            addButton("bt_switch", "切换镜头", System.Camera.switchCamera);
        addButton("bt_camera_ok", "保存", System.Camera.ok);

        var rotate = function (type)
        {
            if (System.Camera.setRotate)
            {
                System.Camera.setRotate(type);
                return;
            }

            var index = System.Camera.getSelectedIndex();

            if (index == null || index < 0)
            {
                alert("请选择要旋转的图片");
                return;
            }

            var image = System.Camera.images[index];
            Cyan.Arachne.get("/richupload/rotate/" + type + "?path=" + image.id, null,
                    function ()
                    {
                        Cyan.reloadImg(Cyan.$$("#camera_images img")[index]);
                    }
            );
        };

        addButton("bt_camera_rotate1", "顺时针旋转90度", function ()
        {
            rotate(1);
        });
        addButton("bt_camera_rotate2", "逆时针旋转90度", function ()
        {
            rotate(2);
        });
        addButton("bt_camera_rotate3", "旋转180度", function ()
        {
            rotate(0);
        });
        addButton("bt_camera_delete", "删除选中的图片", function ()
        {
            System.Camera.deleteImg();
        });
        addButton("bt_camera_clear", "删除所有图片", System.Camera.clear);
        addButton("bt_camera_cancel", "关闭", System.Camera.close);

        Cyan.$("camera_overlay").style.top =
                (document.body.scrollTop || document.documentElement.scrollTop) + "px";
        Cyan.$("camera_overlay").style.display = "";
        setTimeout(System.Camera.resize, 100);

        var f = function ()
        {
            if (System.Camera.setUnload)
                System.Camera.setUnload();

            System.Camera.inited = true;

            if (callback)
                callback();
        };

        if (System.Camera.initActiveX)
            System.Camera.initActiveX(f);
        else
            f();
    },
    show: function (callback)
    {
        System.Camera.init(function ()
        {
            Cyan.$("camera_overlay").style.top =
                    (document.body.scrollTop || document.documentElement.scrollTop) + "px";
            Cyan.$("camera_overlay").style.display = "";
            setTimeout(System.Camera.resize, 100);

            if (System.Camera.start)
                System.Camera.start();

            System.Camera.callback = callback;
        });
    },
    ok: function ()
    {
        var images = System.Camera.images;
        if (System.Camera.stop)
            System.Camera.stop();

        System.Camera.clear();

        var overlay = Cyan.$("camera_overlay");
        if (overlay)
            overlay.style.display = "none";

        if (System.Camera.callback)
            System.Camera.callback(images);

        System.Camera.callback = null;
    },
    close: function ()
    {
        if (System.Camera.stop)
        {
            try
            {
                System.Camera.stop();
            }
            catch (e)
            {
            }
        }

        System.Camera.clear();

        var overlay = Cyan.$("camera_overlay");
        if (overlay)
            overlay.style.display = "none";

        System.Camera.callback = null;
    },
    addImg: function (id, path)
    {
        System.Camera.images.push({id: id, path: path});

        var img = document.createElement("img");
        img.src = path;
        img.onclick = System.Camera.imgClick;

        Cyan.$("camera_imageListInner").appendChild(img);
    },
    imgClick: function ()
    {
        var selectedImg = Cyan.$$("#camera_imageListInner").childElements().searchFirst(function ()
        {
            return this.className == "selected";
        });

        if (selectedImg)
            selectedImg.className = "";

        this.className = "selected";
    },
    getSelectedIndex: function ()
    {
        var index = Cyan.$$("#camera_imageListInner").childElements().each(function (index)
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
        System.Camera.images = [];
        System.Camera.index = 1;
        Cyan.$("camera_imageListInner").innerHTML = "";
    },
    deleteImg: function (index)
    {
        if (index == null)
            index = System.Camera.getSelectedIndex();

        if (index != null && index >= 0)
        {
            Cyan.Array.remove(System.Camera.images, index);
            var imagesDiv = Cyan.$("camera_imageListInner");
            imagesDiv.removeChild(imagesDiv.childNodes[index]);
        }
        else
        {
            alert("请选择要删除的图片");
        }
    },
    camera: function ()
    {

        if (Cyan.navigator.isFF())
        {
            f=function(base64){
                if (base64)
                {
                    Cyan.Arachne.post("/richupload/base64", [base64, (System.Camera.index++ ) + ".jpg"],
                            function (path)
                            {
                                if (path)
                                {
                                    System.Camera.addImg(path, "/richupload/thumb?path=" + path);
                                }
                                else
                                {
                                    alert("拍照错误");
                                }
                            }
                    );
                }
                else
                {
                    alert("拍照错误");
                }
            }
            System.Camera.cameraToBase64(f);
        }
        else
        {
            var base64 = System.Camera.cameraToBase64();

            if (base64)
            {
                Cyan.Arachne.post("/richupload/base64", [base64, (System.Camera.index++ ) + ".jpg"],
                        function (path)
                        {
                            if (path)
                            {
                                System.Camera.addImg(path, "/richupload/thumb?path=" + path);
                            }
                            else
                            {
                                alert("拍照错误");
                            }
                        }
                );
            }
            else
            {
                alert("拍照错误");
            }
        }

    },
    getActiveX: function ()
    {
        return Cyan.$(System.Camera.id);
    }
};

Cyan.importJs("/platform/webcamera/imp.js");