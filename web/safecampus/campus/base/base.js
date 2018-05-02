Cyan.importJs("/safecampus/campus/base/toastr/toastr.min.js");
Cyan.importCss("/safecampus/campus/base/toastr/toastr.css");

window.Sesame = {};
Sesame.tip = function (message, callback)
{
    var container = document.createElement("div");
    container.id = "toast-container";
    container.className = "toast-top-center";
    container.style.opacity = 0;
    container.style.display = "none";
    document.body.appendChild(container);
    var toast = document.createElement("div");
    container.appendChild(toast);
    toast.className = "toast toast-info";
    toast.setAttribute("aria-live", "polite");
    toast.style.display = "block";
    var toastmessage = document.createElement("div");
    toast.appendChild(toastmessage);
    toastmessage.className = "toast-message";
    toastmessage.innerText = message;
    showTips(container, 0.1, 1500);
    if (callback)
        callback();
};