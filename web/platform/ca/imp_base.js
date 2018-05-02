Cyan.importJs("/platform/ca/basecert.js");

Cyan.onload(function ()
{
    System.Cert.addApi(baseCaApi("gdca"));
});