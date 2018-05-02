package com.gzzm.platform.barcode;

import java.io.OutputStream;

/**
 * @author camel
 * @date 2015/8/9
 */
public interface BarCodeGenerator
{
    public void generateQR(String content, int width, OutputStream out) throws Exception;

    public void generateEan13(String content, int width, int height, OutputStream out) throws Exception;

    public void generateITF(String content, int width, int height, OutputStream out) throws Exception;
}