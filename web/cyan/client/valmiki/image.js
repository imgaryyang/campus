Cyan.importJs("widgets/fileupload.js");

Cyan.Valmiki.Image = {};
Cyan.Valmiki.Image.init = function (name)
{
    var fileUpload = new Cyan.FileUpload(name);

    fileUpload.bind(Cyan.$(name + "$img"));
};