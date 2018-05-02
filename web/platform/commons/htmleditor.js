if (Cyan.navigator.isIE() && Cyan.navigator.version <= 7)
    Cyan.importJs("/platform/commons/htmleditor_ckeditor3.js");
else
    Cyan.importJs("/platform/commons/htmleditor_ckeditor.js");