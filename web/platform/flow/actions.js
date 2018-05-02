function showAction(actionId)
{
    var action = Cyan.$(actionId);
    if (action)
    {
        if (action.nextSibling && action.nextSibling.nodeName == "#text")
            action.nextSibling.nodeValue = " ";
        action.style.display = "";
    }
}

function hideAction(actionId)
{
    var action = Cyan.$(actionId);
    if (action)
    {
        if (action.nextSibling && action.nextSibling.nodeName == "#text")
            action.nextSibling.nodeValue = "";
        action.style.display = "none";
    }
}

function setActionText(actionId, text)
{
    var action = Cyan.$(actionId);
    if (action)
    {
        Cyan.Elements.setText(action, text);
    }
}

function addActionBefore(actionId, text, action, nextActionId)
{
    var nextButton;
    if (nextActionId)
    {
        nextButton = Cyan.$(nextActionId);
        if (nextButton)
            nextButton = nextButton.parentNode;
    }

    var buttons;
    if (nextButton)
    {
        buttons = nextButton.parentNode;
    }
    else
    {
        buttons = Cyan.$("buttons");
        if (!buttons)
            buttons = Cyan.$$(".buttons")[0];
    }

    if (buttons)
    {
        var span = document.createElement("SPAN");
        var button = document.createElement("BUTTON");
        button.className = "btn";
        try
        {
            button.type = "button";
        }
        catch (e)
        {
        }
        button.id = actionId;
        Cyan.Elements.setText(button, text);
        button.onclick = action;
        span.appendChild(button);

        if (window.initButton)
            initButton(button);

        if (nextButton)
            buttons.insertBefore(span, nextButton);
        else
            buttons.appendChild(span);
    }
}