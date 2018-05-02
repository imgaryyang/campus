package com.gzzm.platform.barcode;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * 二维码，条形码接口
 *
 * @author camel
 * @date 2015/8/9
 */
public final class BarCodeUtils
{
    private static List<BarCodeGenerator> generators = new ArrayList<BarCodeGenerator>();

    @Inject
    private static Provider<BarCodeDao> daoProvider;

    static
    {
        try
        {
            generators.add(Barcode4jGenerator.getInstance());
        }
        catch (Throwable ex)
        {
            //没有barcode4j包，跳过
        }

        try
        {
            generators.add(ZXingBarCodeGenerator.getInstance());
        }
        catch (Throwable ex)
        {
            //没有zxing包，跳过
        }
    }

    private BarCodeUtils()
    {
    }

    private static BarCodeDao getDao()
    {
        return daoProvider.get();
    }

    public static BarCode getBarCode(Long barCodeId) throws Exception
    {
        return getDao().getBarCode(barCodeId);
    }

    public static List<BarCode> getBarCodeByContent(String content) throws Exception
    {
        return getDao().getBarCodeByContent(content);
    }

    public static List<BarCode> getBarCodeByLinkContent(String content) throws Exception
    {
        return getDao().getBarCodeByLinkContent(content);
    }

    public static void qr(String content, int width, OutputStream out) throws Exception
    {
        for (BarCodeGenerator generator : generators)
        {
            try
            {
                generator.generateQR(content, width, out);
            }
            catch (UnsupportedOperationException ex)
            {
                //没有定义此方法，使用下一个生成器
            }
        }
    }

    public static byte[] qr(String content, int width) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        qr(content, width, out);

        return out.toByteArray();
    }

    public static void qr(String content, int width, File file) throws Exception
    {
        OutputStream out = new FileOutputStream(file);

        try
        {
            qr(content, width, out);
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (Throwable ex)
            {
                //释放资源错误
            }
        }
    }


    public static void qr(String content, int width, String path) throws Exception
    {
        qr(content, width, new File(path));
    }

    public static void ean13(String content, int width, int height, OutputStream out) throws Exception
    {
        if (content.length() < 13)
            content = StringUtils.leftPad(content, 13, '0');

        for (BarCodeGenerator generator : generators)
        {
            try
            {
                generator.generateEan13(content, width, height, out);
            }
            catch (UnsupportedOperationException ex)
            {
                //没有定义此方法，使用下一个生成器
            }
        }
    }

    public static byte[] ean13(String content, int width, int height) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ean13(content, width, height, out);

        return out.toByteArray();
    }

    public static void ean13(String content, int width, int height, File file) throws Exception
    {
        OutputStream out = new FileOutputStream(file);

        try
        {
            ean13(content, width, height, out);
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (Throwable ex)
            {
                //释放资源错误
            }
        }
    }

    public static void ean13(String content, int width, int height, String path) throws Exception
    {
        ean13(content, width, height, new File(path));
    }

    public static void itf(String content, int width, int height, OutputStream out) throws Exception
    {
        if (content.length() % 2 != 0)
            content = '0' + content;

        for (BarCodeGenerator generator : generators)
        {
            try
            {
                generator.generateITF(content, width, height, out);
            }
            catch (UnsupportedOperationException ex)
            {
                //没有定义此方法，使用下一个生成器
            }
        }
    }

    public static byte[] itf(String content, int width, int height) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        itf(content, width, height, out);

        return out.toByteArray();
    }

    public static void itf(String content, int width, int height, File file) throws Exception
    {
        OutputStream out = new FileOutputStream(file);

        try
        {
            itf(content, width, height, out);
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (Throwable ex)
            {
                //释放资源错误
            }
        }
    }

    public static void itf(String content, int width, int height, String path) throws Exception
    {
        itf(content, width, height, new File(path));
    }
}
