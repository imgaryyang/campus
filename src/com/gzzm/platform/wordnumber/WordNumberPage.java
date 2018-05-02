package com.gzzm.platform.wordnumber;

import net.cyan.arachne.annotation.*;

/**
 * @author camel
 * @date 11-10-28
 */
@Service
public class WordNumberPage
{
    private String text;

    @NotSerialized
    private WordNumber wordNumber;

    public WordNumberPage()
    {
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public WordNumber getWordNumber() throws Exception
    {
        if (wordNumber == null)
            wordNumber = new WordNumber(text);

        return wordNumber;
    }

    @Service(url = "/wordnumber")
    public String show()
    {
        return "wordnumber";
    }
}
