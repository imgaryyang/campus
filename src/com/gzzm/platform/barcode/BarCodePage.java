package com.gzzm.platform.barcode;

import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.io.Mime;

/**
 * @author camel
 * @date 2015/8/9
 */
@Service
public class BarCodePage
{
    public BarCodePage()
    {
    }

    @Service(url = "/qr")
    public InputFile qr(String content, int width) throws Exception
    {
        if (width <= 0)
            width = 200;

        byte[] qr = BarCodeUtils.qr(content, width);

        return new InputFile(qr, "qr.png", Mime.PNG);
    }

    @Service(url = "/ean13")
    public InputFile ean13(String content, int width, int height) throws Exception
    {
        if (width <= 0)
            width = 200;

        if (height <= 0)
            height = 50;

        byte[] ean13 = BarCodeUtils.ean13(content, width, height);

        return new InputFile(ean13, "ean13.png", Mime.PNG);
    }

    @Service(url = "/itf")
    public InputFile itf(String content, int width, int height) throws Exception
    {
        if (width <= 0)
            width = 200;

        if (height <= 0)
            height = 50;

        byte[] itf = BarCodeUtils.itf(content, width, height);

        return new InputFile(itf, "itf.png", Mime.PNG);
    }
}
