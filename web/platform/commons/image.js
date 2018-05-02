Cyan.importJs("/lightbox/lightbox.js");
Cyan.importCss("/lightbox/lightbox.css");

(function ()
{
    var showImage = function (url, win)
    {
        showLightbox({href: url});
        if (win && win != window)
        {
            if (win.getNextImg)
            {
                window.getNextImg = function (callback)
                {
                    win.getNextImg(callback);
                };
            }
            if (win.getPreImg)
            {
                window.getPreImg = function (callback)
                {
                    win.getPreImg(callback);
                };
            }
        }
    };
    if (window.System)
        System.showImage = showImage;
    else
        window.showImage = showImage;
})();