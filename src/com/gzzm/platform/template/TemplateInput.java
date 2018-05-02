package com.gzzm.platform.template;

import com.gzzm.platform.commons.Tools;
import net.cyan.activex.OfficeUtils;
import net.cyan.commons.util.*;

import java.io.*;
import java.util.Map;

/**
 * @author camel
 * @date 11-9-26
 */
public class TemplateInput extends Inputable.AbstractInput
{
    private String name;

    private Object model;

    private boolean toDoc;

    private Map<String, ?> context;

    private byte[] bytes;

    public TemplateInput(String name, Object model, Map<String, ?> context, boolean toDoc)
    {
        this.name = name;
        this.model = model;
        this.toDoc = toDoc;
        this.context = context;
    }

    public TemplateInput(String name, Object model, boolean toDoc)
    {
        this.name = name;
        this.model = model;
        this.toDoc = toDoc;
    }

    public byte[] getBytes() throws IOException
    {
        if (bytes == null)
        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            try
            {
                TemplateService.processTemplate(name, model, context, bout);
            }
            catch (IOException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                throw new IOException(ex);
            }

            bytes = bout.toByteArray();

            if (toDoc)
            {
                bout = new ByteArrayOutputStream();
                try
                {
                    OfficeUtils.xmlToWord(new ByteInput(bytes)).writeTo(bout);
                    bytes = bout.toByteArray();
                }
                catch (IOException ex)
                {
                    throw ex;
                }
                catch (Exception ex)
                {
                    Tools.log(ex);
                }
            }
        }
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(getBytes());
    }

    @Override
    public void saveAs(String path) throws IOException
    {
        IOUtils.bytesToFile(getBytes(), path);
    }

    @Override
    public long size() throws IOException
    {
        return getBytes().length;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException
    {
        ByteArrayOutputStream bout = null;

        if (toDoc)
            bout = new ByteArrayOutputStream();

        try
        {
            TemplateService.processTemplate(name, model, context, toDoc ? bout : out);
        }
        catch (IOException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new IOException(ex);
        }

        if (toDoc)
        {
            try
            {
                OfficeUtils.xmlToWord(new ByteInput(bout.toByteArray())).writeTo(out);
            }
            catch (IOException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                Tools.log(ex);

                IOUtils.bytesToStream(bout.toByteArray(), out);
            }
        }
    }
}
