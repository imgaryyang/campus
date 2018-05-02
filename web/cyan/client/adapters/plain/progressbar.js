Cyan.Plain.importCss("progressbar");

Cyan.ProgressBar.prototype.init = function (el)
{
    this.el = Cyan.$$$(el);
    this.el.html("<div class='cyan-progress'>" +
    "<div class='cyan-progress-message' style='display: none'></div>" +
    "<div class='cyan-progress-progress'>" +
    "<div class='cyan-progress-progress-bar' style='width: 0'></div>" +
    "<div class='cyan-progress-progress-text'></div></div>");
};

Cyan.ProgressBar.prototype.update = function (percentage, message)
{
    var s = Math.round(percentage * 100) + "%";
    this.el.$(".cyan-progress-progress-bar").css("width", s);
    this.el.$(".cyan-progress-progress-text").html(message);
};