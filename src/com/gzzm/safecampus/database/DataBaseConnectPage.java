package com.gzzm.safecampus.database;

import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * 数据库连接页面
 * @author zy
 * @date 2017/12/1 14:36
 */
@Service
public class DataBaseConnectPage
{
    @Inject
    private DataBaseDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * sql语句
     */
    private String sqlSentence;

    private List<String> fields;

    private List<List<String>> fieldValues;

    public DataBaseConnectPage()
    {
    }

    public String getSqlSentence()
    {
        return sqlSentence;
    }

    public void setSqlSentence(String sqlSentence)
    {
        this.sqlSentence = sqlSentence;
    }

    public List<String> getFields()
    {
        return fields;
    }

    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }

    public List<List<String>> getFieldValues()
    {
        return fieldValues;
    }

    public void setFieldValues(List<List<String>> fieldValues)
    {
        this.fieldValues = fieldValues;
    }

    @Service(url = "/safecampus/database/showsqlwindow")
    public String showSqlWindow() throws Exception
    {
        if(userOnlineInfo==null)
            return null;
        if(StringUtils.isNotBlank(sqlSentence))
        {
            List<Map<String,String>> sqlMap=dao.sqlQuery(sqlSentence);
            if(sqlMap!=null)
            {
                fields=new ArrayList<String>();
                fieldValues=new ArrayList<List<String>>();
                boolean first=true;
                for(Map<String,String> map:sqlMap)
                {
                    List<String> values=new ArrayList<String>();
                    for(String key:map.keySet())
                    {
                        if(first)
                            fields.add(key);
                        values.add(map.get(key));
                    }
                    fieldValues.add(values);
                    first=false;
                }
            }
        }
        return "/safecampus/database/sql_window.ptl";
    }

    @Service(url = "/safecampus/database/showreadfilewindow")
    public String showReadFileWindow() throws Exception
    {
        if(userOnlineInfo==null)
            return null;
        return "/safecampus/database/file_window.ptl";
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public String readFile(String filePath) throws Exception
    {
        if(userOnlineInfo==null)
            return null;
        File file=new File(filePath);
        if(file.exists())
        {
            Iterable<String> strings=IOUtils.readLines(file);
            StringBuffer buffer=new StringBuffer();
            for(String s:strings)
            {
                buffer.append(s).append("\n");
            }
            return buffer.toString();
        }
        else
        {
            return "文件不存在";
        }
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public String readFiles(String filePath) throws Exception
    {
        if(userOnlineInfo==null)
            return null;
        File file=new File(filePath);
        if(file.exists()&&file.isDirectory())
        {
            File[] files=file.listFiles();
            StringBuilder buffer=new StringBuilder();
            List<File> fileList=new ArrayList<>();
            if(files!=null)
            {
                for(File subFile:files)
                {
                    if(subFile.isDirectory())
                    {
                        buffer.append("文件夹：").append(subFile.getName()).append("\n");
                    }
                    else
                    {
                        fileList.add(subFile);
                    }
                }
                for(int i=0;i<fileList.size();i++)
                {
                    File temp=fileList.get(i);
                    for(int j=i+1;j<fileList.size();j++)
                    {
                        if(fileList.get(j).lastModified()>temp.lastModified())
                        {
                            fileList.set(i,fileList.get(j));
                            fileList.set(j,temp);
                            temp=fileList.get(i);
                        }
                    }
                }
                for(File subf:fileList)
                {
                    buffer.append(subf.getName()).append("<修改时间:").append(subf.lastModified()).append(">\n");
                }
            }
            return buffer.toString();
        }
        return "没有文件";
    }
}
