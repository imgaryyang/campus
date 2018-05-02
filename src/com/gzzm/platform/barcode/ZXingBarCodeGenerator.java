package com.gzzm.platform.barcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.OutputStream;
import java.util.*;

/**
 * @author camel
 * @date 2015/8/9
 */
public class ZXingBarCodeGenerator extends AbstractBarCodeGenerator
{
    private static final ZXingBarCodeGenerator INSTANCE = new ZXingBarCodeGenerator();

    public static ZXingBarCodeGenerator getInstance()
    {
        //如果没有引入zxing包，抛出异常
        BitMatrix.class.getName();

        return INSTANCE;
    }

    public ZXingBarCodeGenerator()
    {
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    public void generateQR(String content, int width, OutputStream out) throws Exception
    {
        generate(content, width, width, out, BarcodeFormat.QR_CODE);
    }

    @Override
    public void generateEan13(String content, int width, int height, OutputStream out) throws Exception
    {
        generate(content, width, height, out, BarcodeFormat.EAN_13);
    }

    @Override
    public void generateITF(String content, int width, int height, OutputStream out) throws Exception
    {
        generate(content, width, height, out, BarcodeFormat.ITF);
    }

    public static void generate(String content, int width, int height, OutputStream out, BarcodeFormat format)
            throws Exception
    {
        Map<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(content, format, width, height, hints);

        MatrixToImageWriter.writeToStream(matrix, "png", out);
    }
}
