Cyan.initBasePrototpe();
Cyan.Plain = {
    stylePath: Cyan.adapterPath + "default/",
    importCss: function (name)
    {
        Cyan.importCss(Cyan.Plain.stylePath + name + ".css");
    },
    importLanguage: function (name)
    {
        Cyan.importJs(Cyan.adapterPath + "locale/" + window.languageForCyan$ + "/" + name + ".js");
    }
};

Cyan.importJs("widgets/window.js");

Cyan.tip = function (message)
{
};