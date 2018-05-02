package com.gzzm.platform.commons.filestore;

/**
 * @author camel
 * @date 13-12-3
 */
public interface FileItemTransformer
{
    public FileItem transform(Object bean) throws Exception;
}