package com.gzzm.im;

/**
 * @author camel
 * @date 12-2-13
 */
public final class ImUtils
{
    private ImUtils()
    {
    }

    public static String decodeContent(String content) throws Exception
    {
        int index;
        while ((index = content.indexOf("[face]")) >= 0)
        {
            int index2 = content.indexOf("[/face]", index + 6);
            if (index2 < 0)
                break;
            String face = content.substring(index + 6, index2);
            content = content.substring(0, index) + "<img src='/im/images/faces/" + face + ".gif'>" +
                    content.substring(index2 + 7);
        }

        return content;
    }
}
