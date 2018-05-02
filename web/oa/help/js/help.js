Cyan.importCss("/oa/help/css/prompt.css");

Cyan.onload(function () {
    getValidInformation(0, function (ret) {
        if (ret) {
            var container = document.createElement("div");
            document.body.appendChild(container);
            container.setAttribute("class", "prompt_container");
            container.style.width = document.documentElement.clientWidth + "px";
            container.style.height = document.documentElement.clientHeight + "px";

            var div = document.createElement("div");
            container.appendChild(div);
            div.setAttribute("class", "prompt");

            var title = document.createElement("div");
            div.appendChild(title);
            title.setAttribute("class", "title");
            title.innerText = ret.title;

            var close = document.createElement("span");
            title.appendChild(close);
            close.setAttribute("class", "close");
            close.onclick = function () {
                container.style.display = "none";
            };

            var content = document.createElement("div");
            div.appendChild(content);
            content.setAttribute("class", "content");

            var contentTop = document.createElement("div");
            content.appendChild(contentTop);
            contentTop.setAttribute("class", "content_top");
            contentTop.innerText = ret.content;

            var contentBottom = document.createElement("div");
            content.appendChild(contentBottom);
            contentBottom.setAttribute("class", "content_bottom");

            //居中显示
            div.style.top = document.documentElement.clientHeight / 2 - div.clientHeight / 2 + "px";
            div.style.left = document.documentElement.clientWidth / 2 - div.clientWidth / 2 + "px";
        }
    });
});

function getValidInformation(pointId) {
    Cyan.Arachne.doGet("/oa/help/prompt/" + pointId + "/pointinfo", arguments, 3);
    return false;
}