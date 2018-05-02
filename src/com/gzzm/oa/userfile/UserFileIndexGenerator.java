package com.gzzm.oa.userfile;

import com.gzzm.platform.commons.IndexGenerator;

/**
 * 个人文件的全文索引建立
 *
 * @author camel
 * @date 2010-8-25
 */
public class UserFileIndexGenerator extends IndexGenerator
{
    public UserFileIndexGenerator()
    {
        setClasses(UserFile.class);
    }
}
