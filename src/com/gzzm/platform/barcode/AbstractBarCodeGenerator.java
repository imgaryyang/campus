package com.gzzm.platform.barcode;

import java.io.OutputStream;

/**
 * @author camel
 * @date 2015/12/16
 */
public abstract class AbstractBarCodeGenerator implements BarCodeGenerator
{
    public AbstractBarCodeGenerator()
    {
    }

    @Override
    public void generateQR(String content, int width, OutputStream out) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public void generateEan13(String content, int width, int height, OutputStream out) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public void generateITF(String content, int width, int height, OutputStream out) throws Exception
    {
        throw new UnsupportedOperationException("Method not implemented.");
    }
}
