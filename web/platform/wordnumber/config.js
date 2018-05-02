Cyan.importJs("/platform/wordnumber/wordnumber.js");

Cyan.onload(function ()
{
    var textElement = Cyan.$("text");
    textElement.readOnly = true;
    textElement.style.cursor = "pointer";
    Cyan.attach(textElement, "click", function ()
    {
        showWordNumber(Cyan.$("entity.wordNumber"), textElement);
    });
});