function hideComponent(name)
{
    var div = getComponentDiv(name);
    if (div)
        div.style.display = "none";
}

function showComponent(name)
{
    var div = getComponentDiv(name);
    if (div)
        div.style.display = "block";
}

function setComponentLabel(name, text)
{
    Cyan.Elements.setText(getComponentLabelElement(name), text);
}

function getComponentLabel(name)
{
    return getComponentLabelElement(name).innerHTML;
}

function getComponentLabelElement(name)
{
    return Cyan.$$(getComponentDiv(name)).$(".label")[0];
}

function getComponentDiv(name)
{
    var element = Cyan.$(name);
    if (element)
    {
        while (element.nodeName != "DIV" || element.className != "component_item")
        {
            if (element.nodeName == "BODY")
                return null;
            element = element.parentNode;
        }
        return element;
    }
    else
    {
        return null;
    }
}

function getComponentUnitDiv(name)
{
    return Cyan.$$(getComponentDiv(name)).$(".unit")[0];
}

function getComponentUnit(name)
{
    var unitDiv = getComponentUnitDiv(name);

    return unitDiv ? unitDiv.innerHTML : null;
}

function setComponentUnit(name, unit)
{
    var unitDiv = getComponentUnitDiv(name);
    if (unitDiv)
        Cyan.Elements.setText(unitDiv, unit);
}