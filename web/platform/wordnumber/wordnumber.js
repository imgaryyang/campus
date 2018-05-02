function showWordNumber(valueElement, textElement)
{
    System.showModal("/wordnumber?text=" + encodeURIComponent(valueElement.value), function(result)
    {
        if (result)
        {
            valueElement.value = result.value;
            textElement.value = result.text;
        }
    });
}