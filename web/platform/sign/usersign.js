Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "showSign", function (userId)
    {
        System.showImage("/sign/user/" + userId + "/sign");
    });
});
