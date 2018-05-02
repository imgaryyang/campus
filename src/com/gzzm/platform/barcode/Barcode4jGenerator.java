package com.gzzm.platform.barcode;

import org.krysalis.barcode4j.*;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * @author camel
 * @date 2015/12/16
 */
public class Barcode4jGenerator extends AbstractBarCodeGenerator
{
    private static final Barcode4jGenerator INSTANCE = new Barcode4jGenerator();

    public static Barcode4jGenerator getInstance()
    {
        BarcodeGenerator.class.getName();

        return INSTANCE;
    }

    public Barcode4jGenerator()
    {
    }

    @Override
    public void generateITF(String content, int width, int height, OutputStream out) throws Exception
    {
        Interleaved2Of5Bean bean = new Interleaved2Of5Bean();

        int dpi = 96;

        bean.setHeight(height / 2.5);
        bean.doQuietZone(true);
        bean.setQuietZone(2);
        bean.setChecksumMode(ChecksumMode.CP_AUTO);
        bean.setDisplayChecksum(false);
        bean.setModuleWidth((double) width / 466);
        bean.setFontName("Helvetica");
        bean.setFontSize(3);
        bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);

        BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

        bean.generateBarcode(canvas, content);

        canvas.finish();
    }
}
