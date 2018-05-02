Cyan.importJs("/ckeditor3/ckeditor.js");
Cyan.importJs("widgets/fileupload.js");
Cyan.importJs("/platform/commons/htmleditor_base.js");

Cyan.Class.overwrite(window, "confirm", function (message)
{
    var editor;
    for (var name in CKEDITOR.instances)
    {
        editor = CKEDITOR.instances[name];
        if (editor)
            break;
    }

    if (message == editor.lang.pastefromword.confirmCleanup)
        return false;
    return this.inherited(message);
});

Cyan.onload(function ()
{
    CKEDITOR.cleanWord = function (h, i)
    {
        return h;
    };

    var upload = function (file, type, callback)
    {
        var form = Cyan.$("uploadForm");

        if (!form)
        {
            form = document.createElement("FORM");
            form.id = "uploadForm";
            document.body.appendChild(form);

            var fileTypeInput = document.createElement("INPUT");
            fileTypeInput.type = "hidden";
            fileTypeInput.name = "fileType";
            fileTypeInput.id = "fileType";
            form.appendChild(fileTypeInput);
        }

        form.appendChild(file);
        form["fileType"].value = type;

        doUpload({
            callback: function (uuid)
            {
                var path = "/attachment/" + uuid + "/1";
                var name = Cyan.getFileName(file.value, true);
                path += "/" + name.toLowerCase();

                callback(name, path);
            },
            wait: true,
            progress: true,
            form: "uploadForm",
            obj: {}
        });
    };

    var doUpload = function ()
    {
        Cyan.Arachne.doPost("/attachments/save", arguments, 3);
    };


    Cyan.run(function ()
    {
        return window.CKEDITOR;
    }, function ()
    {
        CKEDITOR.focusManager.prototype.focus =
                CKEDITOR.focusManager.prototype.blur = CKEDITOR.focusManager.prototype.forceBlur = function ()
                {
                };

        var line_height_style =
        {
            element: 'span',
            styles: {'line-height': '#(line_height)'},
            overrides: []
        };

        CKEDITOR.plugins.add('rowspacing',
                {
                    init: function (editor)
                    {
                        var styles = [];
                        editor.ui.addRichCombo("RowSpacing",
                                {
                                    label: "行距",
                                    title: "设置行距",
                                    className: "rowspacing",
                                    panel: {
                                        css: editor.skin.editor.css,
                                        multiSelect: false
                                    },
                                    init: function ()
                                    {
                                        this.addLineHeight("单倍行距", "100%");
                                        this.addLineHeight("1.5倍行距", "150%");
                                        this.addLineHeight("双倍行距", "200%");
                                    },
                                    addLineHeight: function (name, value)
                                    {
                                        styles[value] = new CKEDITOR.style(line_height_style, {line_height: value});
                                        this.add(value, "<span style='font-size:12px'>" + name + "</span>", name);
                                    },
                                    onClick: function (value)
                                    {
                                        editor.focus();
                                        editor.fire('saveSnapshot');
                                        var style = styles[value];

                                        if (this.getValue() == value)
                                            style.remove(editor.document);
                                        else
                                            style.apply(editor.document);

                                        editor.fire('saveSnapshot');
                                    },
                                    onRender: function ()
                                    {
                                        editor.on('selectionChange', function (ev)
                                        {
                                            var currentValue = this.getValue();
                                            var elementPath = ev.data.path,
                                                    elements = elementPath.elements;
                                            for (var i = 0, element; i < elements.length; i++)
                                            {
                                                element = elements[i];
                                                for (var value in styles)
                                                {
                                                    if (styles[value].checkElementRemovable(element, true))
                                                    {
                                                        if (value != currentValue)
                                                            this.setValue(value);
                                                        return;
                                                    }
                                                }
                                            }
                                        }, this);
                                    }
                                });
                    },
                    requires: ['richcombo', 'styles']
                });

        CKEDITOR.plugins.add('fileupload',
                {
                    init: function (editor)
                    {
                        editor.addCommand("fileupload", {
                            exec: function ()
                            {
                                System.showModal("/attachments/upload", function (result)
                                {
                                    if (result)
                                    {
                                        editor.insertHtml("<a href='" + result.path + "'>" + result.name + "</a>");
                                    }
                                });
                            }
                        });

                        editor.addCommand("video", {
                            exec: function ()
                            {
                                System.showModal("/attachments/upload?fileType=video", function (result)
                                {
                                    if (result)
                                    {
                                        editor.insertHtml(createVideoHTML(result.path, result.name, result.width,
                                                result.height));
                                    }
                                });
                            }
                        });

                        editor.addCommand("image", {
                            exec: function ()
                            {
                                System.showModal("/attachments/image", function (result)
                                {
                                    if (result)
                                    {
                                        var html = "<img src='" + result.path + "'";
                                        if (result.width)
                                            html += " width='" + result.width + "'";
                                        if (result.height)
                                            html += " height='" + result.height + "'";
                                        html += ">";
                                        editor.insertHtml(html);
                                    }
                                });
                            }
                        });

                        editor.ui.addButton("FileUpload",
                                {
                                    label: "文件上传",
                                    title: "上传文件到服务器，并插入到内容中",
                                    icon: "/platform/commons/icons/upload.gif",
                                    command: "fileupload"
                                });

                        editor.ui.addButton("ImageBatch",
                                {
                                    label: "批量上传图片",
                                    title: "批量上传图片到服务器，并插入到内容中",
                                    icon: "/platform/commons/icons/image.gif"
                                });

                        editor.ui.addButton("Video",
                                {
                                    label: "上传视频",
                                    title: "上传视频到服务器，并插入到内容中",
                                    icon: "/platform/commons/icons/video.gif",
                                    command: "video"
                                });
                    }
                });

        var toolbar = [
            ['Source', 'Undo', 'Redo', 'Copy', 'Paste', '-', 'Find', 'SelectAll', 'RemoveFormat'],
            ['Bold', 'Italic', 'Underline', 'Strike', '-', 'Subscript', 'Superscript'],
            ['Outdent', 'Indent'],
            ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
            ['Link', 'Unlink'],
            ['Image', "ImageBatch", 'FileUpload', "Video"],
            ['Table', 'HorizontalRule'],
            ['Styles', 'Format', 'Font', 'FontSize', "RowSpacing"],
            ['TextColor', 'BGColor']
        ];

        var font_names = CKEDITOR.config.font_names + ";宋体/宋体;仿宋/仿宋;新宋体/新宋体;黑体/黑体;楷体/楷体;隶书/隶书;幼圆/幼圆;" +
                "华文中宋/华文中宋;华文仿宋/华文仿宋;华文细黑/华文细黑;华文行楷/华文行楷;华文新魏/华文新魏;华文彩云/华文彩云;" +
                "方正舒体/方正舒体;方正姚体/方正姚体";

        var fontSize_sizes = "12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;24/24px;26/26px;28/28px;36/36px;48/48px;72/72px";

        CKEDITOR.config.pasteFromWordNumberedHeadingToList = true;

        var editor;
        var replace = function (component)
        {
            var width = Cyan.Elements.getCss(component, "width");
            if (!width)
                width = component.clientWidth;
            if (!width)
                width = 600;
            var height = component.clientHeight;
            if (!height)
                height = 300;

            if (component.value == "")
                component.value = "<span style='font-family:宋体;font-size:14px'>&nbsp;</span>";

            editor = CKEDITOR.replace(component, {
                width: width,
                height: height - 77,
                skin: "v2",
                resize_enabled: false,
                toolbar_Full: toolbar,
                enterMode: CKEDITOR.ENTER_DIV,
                shiftEnterMode: CKEDITOR.ENTER_P,
                pasteFromWordPromptCleanup: true,
                pasteFromWordRemoveStyles: false,
                pasteFromWordRemoveFontStyles: false,
                font_names: font_names,
                fontSize_sizes: fontSize_sizes,
                scayt_autoStartup: false,
                extraPlugins: "rowspacing,fileupload"
            });

            component.setValue = function (value)
            {
                CKEDITOR.instances[component.id || component.name].setData(component.value = value);
            };

            component.getValue = function ()
            {
                return this.value = CKEDITOR.instances[component.id || component.name].getData();
            };

            if (component.form)
            {
                Cyan.Class.overwrite(component.form, "cyanOnSubmit", function ()
                {
                    component.value = CKEDITOR.instances[component.id || component.name].getData();
                    if (this.inherited)
                        this.inherited();
                });
                Cyan.attach(component.form, "reset", function ()
                {
                    CKEDITOR.instances[component.id || component.name].setData(component.value);
                });
            }
        };

        Cyan.$$("textarea.htmleditor").each(function ()
        {
            var component = this;
            Cyan.run(function ()
            {
                return component.clientWidth && component.clientHeight;
            }, function ()
            {
                var container = component.parentNode;
                var name = component.name;
                replace(component);
            });
        });

        setTimeout(function ()
        {
            var upload = new System.RichUpload("/attachments/save");
            upload.autoUploadNextFile = false;
            upload.bindButton(Cyan.$("cke_33"));
            upload.displayProgressIn("progress");

            upload.addListener({
                onselect: function (file)
                {
                },
                onsuccess: function (file, result)
                {
                    var html = "<br><img src='/attachment/" + result + "/0'>";
                    editor.insertHtml(html);
                    upload.uploadNextFile();
                },
                oncomplete: function (file)
                {
                }
            });
        }, 500);
    });
});