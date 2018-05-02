/*
 Lightbox JS: Fullsize Image Overlays
 by Lokesh Dhakar - http://www.huddletogether.com

 For more information on this script, visit:
 http://huddletogether.com/projects/lightbox/

 Licensed under the Creative Commons Attribution 2.5 License - http://creativecommons.org/licenses/by/2.5/
 (basically, do anything you want, just leave my name and link)

 Table of Contents
 -----------------
 Configuration

 Functions
 - getPageScroll()
 - getPageSize()
 - pause()
 - getKey()
 - listenKey()
 - showLightbox()
 - hideLightbox()
 - initLightbox()
 - addLoadEvent()

 Function Calls
 - addLoadEvent(initLightbox)

 */



//
// Configuration
//

// If you would like to use a custom loading image or close button reference them in the next two lines.
var loadingImage = '/lightbox/loading.gif';
var closeButton = '/lightbox/close.gif';


//
// getPageScroll()
// Returns array with x,y page scroll values.
// Core code from - quirksmode.org
//
function getPageScroll()
{

    var yScroll;

    if (self.pageYOffset)
    {
        yScroll = self.pageYOffset;
    }
    else if (document.documentElement && document.documentElement.scrollTop)
    {     // Explorer 6 Strict
        yScroll = document.documentElement.scrollTop;
    }
    else if (document.body)
    {// all other Explorers
        yScroll = document.body.scrollTop;
    }

    return new Array('', yScroll);
}


//
// getPageSize()
// Returns array with page width, height and window width, height
// Core code from - quirksmode.org
// Edit for Firefox by pHaez
//
function getPageSize()
{

    var xScroll, yScroll;

    if (window.innerHeight && window.scrollMaxY)
    {
        xScroll = document.body.scrollWidth;
        yScroll = window.innerHeight + window.scrollMaxY;
    }
    else if (document.body.scrollHeight > document.body.offsetHeight)
    { // all but Explorer Mac
        xScroll = document.body.scrollWidth;
        yScroll = document.body.scrollHeight;
    }
    else
    { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
        xScroll = document.body.offsetWidth;
        yScroll = document.body.offsetHeight;
    }

    var windowWidth, windowHeight;
    if (self.innerHeight)
    {    // all except Explorer
        windowWidth = self.innerWidth;
        windowHeight = self.innerHeight;
    }
    else if (document.documentElement && document.documentElement.clientHeight)
    { // Explorer 6 Strict Mode
        windowWidth = document.documentElement.clientWidth;
        windowHeight = document.documentElement.clientHeight;
    }
    else if (document.body)
    { // other Explorers
        windowWidth = document.body.clientWidth;
        windowHeight = document.body.clientHeight;
    }

    // for small pages with total height less then height of the viewport
    var pageHeight;
    if (yScroll < windowHeight)
    {
        pageHeight = windowHeight;
    }
    else
    {
        pageHeight = yScroll;
    }

    // for small pages with total width less then width of the viewport
    var pageWidth;
    if (xScroll < windowWidth)
    {
        pageWidth = windowWidth;
    }
    else
    {
        pageWidth = xScroll;
    }

    return new Array(pageWidth, pageHeight, windowWidth, windowHeight);
}


//
// pause(numberMillis)
// Pauses code execution for specified time. Uses busy code, not good.
// Code from http://www.faqts.com/knowledge_base/view.phtml/aid/1602
//
function pause(numberMillis)
{
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while (true)
    {
        now = new Date();
        if (now.getTime() > exitTime)
            return;
    }
}

//
// getKey(key)
// Gets keycode. If 'x' is pressed then it hides the lightbox.
//

function getKey(e)
{
    var keycode;
    if (e == null)
    { // ie
        keycode = event.keyCode;
    }
    else
    { // mozilla
        keycode = e.which;
    }
    var key = String.fromCharCode(keycode).toLowerCase();

    if (key == 'x')
    {
        hideLightbox();
    }
}


//
// listenKey()
//
function listenKey()
{
    document.onkeypress = getKey;
}


//
// showLightbox()
// Preloads images. Pleaces new image in lightbox then centers and displays.
//
function showLightbox(objLink)
{
    // prep objects
    var objOverlay = document.getElementById('overlay');
    var objLightbox = document.getElementById('lightbox');
    var objCaption = document.getElementById('lightboxCaption');
    var objImage = document.getElementById('lightboxImage');
    var objLoadingImage = document.getElementById('loadingImage');
    var objLightboxDetails = document.getElementById('lightboxDetails');


    var arrayPageSize = getPageSize();
    var arrayPageScroll = getPageScroll();

    // center loadingImage if it exists
    if (objLoadingImage)
    {
        objLoadingImage.style.top =
                (arrayPageScroll[1] + ((arrayPageSize[3] - 35 - objLoadingImage.height) / 2) + 'px');
        objLoadingImage.style.left = (((arrayPageSize[0] - 20 - objLoadingImage.width) / 2) + 'px');
        objLightbox.style.display = 'none';
        objLoadingImage.style.display = 'block';
    }

    // set height of Overlay to take up whole page and show
    objOverlay.style.height = (arrayPageSize[1] + 'px');
    objOverlay.style.display = 'block';

    // preload image
    var imgPreload = new Image();

    imgPreload.onload = function ()
    {
        objImage.src = objLink.href;

        // center lightbox and make sure that the top and left values are not negative
        // and the image placed outside the viewport
        var height = imgPreload.height;
        var width = imgPreload.width;
        var height0 = height;
        var width0 = width;

        if (height > arrayPageSize[3] - 20)
            height = arrayPageSize[3] - 20;

        if (width > arrayPageSize[0] - 20)
            width = arrayPageSize[0] - 20;

        var p1 = height / height0;
        var p2 = width / width0;

        if (p1 > p2)
            height = height0 * p2;
        else if (p2 > p1)
            width = width0 * p1;

        objImage.style.width = width + "px";
        objImage.style.height = height + "px";

        var lightboxTop = arrayPageScroll[1] + ((arrayPageSize[3] - 20 - height) / 2);
        var lightboxLeft = ((arrayPageSize[0] - 20 - width) / 2);

        objLightbox.style.top = (lightboxTop < 0) ? "0px" : lightboxTop + "px";
        objLightbox.style.left = (lightboxLeft < 0) ? "0px" : lightboxLeft + "px";


        objLightboxDetails.style.width = width + 'px';

        //		if(objLink.getAttribute('title')){
        //			objCaption.style.display = 'block';
        //objCaption.style.width = imgPreload.width + 'px';
        //			objCaption.innerHTML = objLink.getAttribute('title');
        //		} else {
        objCaption.style.display = 'none';
        //		}

        // A small pause between the image loading and displaying is required with IE,
        // this prevents the previous image displaying for a short burst causing flicker.
        if (navigator.appVersion.indexOf("MSIE") != -1)
        {
            pause(250);
        }

        if (objLoadingImage)
        {
            objLoadingImage.style.display = 'none';
        }

        // Hide select boxes as they will 'peek' through the image in IE
        var selects = document.getElementsByTagName("select");
        for (var i = 0; i != selects.length; i++)
        {
            selects[i].style.visibility = "hidden";
        }


        objLightbox.style.display = 'block';

        // After image is loaded, update the overlay height as the new image might have
        // increased the overall page height.
        arrayPageSize = getPageSize();
        objOverlay.style.height = (arrayPageSize[1] + 'px');

        // Check for 'x' keypress
        listenKey();

        return false;
    };

    imgPreload.src = objLink.href;

}


//
// hideLightbox()
//
function hideLightbox()
{
    // get objects
    var objOverlay = document.getElementById('overlay');
    var objLightbox = document.getElementById('lightbox');

    // hide lightbox and overlay
    objOverlay.style.display = 'none';
    objLightbox.style.display = 'none';

    // make select boxes visible
    var selects = document.getElementsByTagName("select");
    for (var i = 0; i != selects.length; i++)
    {
        selects[i].style.visibility = "visible";
    }

    // disable keypress listener
    document.onkeypress = '';

    window.getNextImg = null;
    window.getPreImg = null;

    return false;
}


//
// initLightbox()
// Function runs on window load, going through link tags looking for rel="lightbox".
// These links receive onclick events that enable the lightbox display for their targets.
// The function also inserts html markup at the top of the page which will be used as a
// container for the overlay pattern and the inline image.
//
function initLightbox()
{
    if (!document.getElementsByTagName)
    {
        return;
    }
    var anchors = document.getElementsByTagName("a");

    // loop through all anchor tags
    for (var i = 0; i < anchors.length; i++)
    {
        var anchor = anchors[i];

        if (anchor.getAttribute("href") && (anchor.getAttribute("rel") == "lightbox"))
        {
            anchor.onclick = function ()
            {
                showLightbox(this);
                return false;
            }
        }
    }

    // the rest of this code inserts html at the top of the page that looks like this:
    //
    // <div id="overlay">
    //		<a href="#" onclick="hideLightbox(); return false;"><img id="loadingImage" /></a>
    //	</div>
    // <div id="lightbox">
    //		<a href="#" onclick="hideLightbox(); return false;" title="Click anywhere to close image">
    //			<img id="closeButton" />
    //			<img id="lightboxImage" />
    //		</a>
    //		<div id="lightboxDetails">
    //			<div id="lightboxCaption"></div>
    //			<div id="keyboardMsg"></div>
    //		</div>
    // </div>

    var objBody = document.getElementsByTagName("body").item(0);

    // create overlay div and hardcode some functional styles (aesthetic styles are in CSS file)
    var objOverlay = document.createElement("div");
    objOverlay.id = 'overlay';
    objOverlay.onclick = function ()
    {
        hideLightbox();
        return false;
    };
    objOverlay.style.display = 'none';
    objOverlay.style.position = 'absolute';
    objOverlay.style.top = '0';
    objOverlay.style.left = '0';
    objOverlay.style.zIndex = '19000';
    objOverlay.style.width = '100%';
    objBody.insertBefore(objOverlay, objBody.firstChild);

    // preload and create loader image
    var imgPreloader = new Image();

    // if loader image found, create link to hide lightbox and create loadingimage
    imgPreloader.onload = function ()
    {

        var objLoadingImageLink = document.createElement("a");
        objLoadingImageLink.setAttribute('href', '#');
        objLoadingImageLink.onclick = function ()
        {
            hideLightbox();
            return false;
        };
        objOverlay.appendChild(objLoadingImageLink);

        var objLoadingImage = document.createElement("img");
        objLoadingImage.src = loadingImage;
        objLoadingImage.id = 'loadingImage';
        objLoadingImage.style.position = 'absolute';
        objLoadingImage.style.zIndex = '19150';
        objLoadingImageLink.appendChild(objLoadingImage);

        imgPreloader.onload = function ()
        {
        };	//	clear onLoad, as IE will flip out w/animated gifs

        return false;
    };

    imgPreloader.src = loadingImage;

    // create lightbox div, same note about styles as above
    var objLightbox = document.createElement("div");
    objLightbox.id = 'lightbox';
    objLightbox.style.display = 'none';
    objLightbox.style.position = 'absolute';
    objLightbox.style.zIndex = '19100';
    objBody.insertBefore(objLightbox, objOverlay.nextSibling);

    function up()
    {
        var objOverlay = document.getElementById('overlay');
        if (objOverlay.style.display != "none")
        {
            window.getPreImg(function (url)
            {
                if (url)
                    showLightbox({href: url});
                else
                    alert("没有上一张");
            });
        }
    }

    function down()
    {
        var objOverlay = document.getElementById('overlay');
        if (objOverlay.style.display != "none")
        {
            window.getNextImg(function (url)
            {
                if (url)
                    showLightbox({href: url});
                else
                    alert("没有下一张");
            });
        }
    }

    // create link
    var objLink = document.createElement("a");
    objLink.setAttribute('href', '#');

    var pageHeight = getPageSize()[1];
    objLink.onmousemove = function (event)
    {
        if (window.getPreImg)
        {
            if (event.pageY < pageHeight / 3)
            {
                objLink.className = "up";
                objLink.setAttribute("title", "上一张图片");
                objLink.onclick = up;
                return;
            }
        }

        if (window.getNextImg)
        {
            objLink.className = "down";
            objLink.setAttribute("title", "下一张图片");
            objLink.onclick = down;
        }
        return false;
    };
    objLightbox.appendChild(objLink);

    // preload and create close button image
    var imgPreloadCloseButton = new Image();

    // if close button image found,
    imgPreloadCloseButton.onload = function ()
    {
        var objCloseButton = document.createElement("img");
        objCloseButton.src = closeButton;
        objCloseButton.id = 'closeButton';
        objCloseButton.style.position = 'absolute';
        objCloseButton.style.zIndex = '19200';
        objCloseButton.title = "单击关闭图片";
        objCloseButton.style.cursor = "pointer";
        objCloseButton.onclick = hideLightbox;
        objLink.appendChild(objCloseButton);

        return false;
    };

    imgPreloadCloseButton.src = closeButton;

    // create image
    var objImage = document.createElement("img");
    objImage.id = 'lightboxImage';
    objLink.appendChild(objImage);

    // create details div, a container for the caption and keyboard message
    var objLightboxDetails = document.createElement("div");
    objLightboxDetails.id = 'lightboxDetails';
    objLightbox.appendChild(objLightboxDetails);

    // create caption
    var objCaption = document.createElement("div");
    objCaption.id = 'lightboxCaption';
    objCaption.style.display = 'none';
    objLightboxDetails.appendChild(objCaption);

    // create keyboard message
    //	var objKeyboardMsg = document.createElement("div");
    //	objKeyboardMsg.setAttribute('id','keyboardMsg');
    //	objKeyboardMsg.innerHTML = 'press <a href="#" onclick="hideLightbox(); return false;"><kbd>x</kbd></a> to close';
    //	objLightboxDetails.appendChild(objKeyboardMsg);


}


//
// addLoadEvent()
// Adds event to window.onload without overwriting currently assigned onload functions.
// Function found at Simon Willison's weblog - http://simon.incutio.com/
//
function addLoadEvent(func)
{
    if (window.addEventListener)
        window.addEventListener("load", func, false);
    else
        window.attachEvent("onload", func);
}


addLoadEvent(initLightbox);	// run initLightbox onLoad