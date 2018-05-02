package com.gzzm.platform.log;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.commons.util.language.Language;
import net.cyan.commons.util.xml.*;
import net.cyan.thunwind.annotation.*;
import org.w3c.dom.Document;

import java.util.Date;

/**
 * 操作日志，记录管理员的每一个操作
 *
 * @author camel
 * @date 2009-7-18
 */
@Entity(table = "PFOPERATIONLOG", keys = "logId")
public class OperationLog extends UserLog
{
    /**
     * 日志类型，为一个字符串，由各功能模块定义
     */
    @Index
    @ColumnDescription(type = "varchar(200)")
    private String type;

    /**
     * 动作，如删除等
     */
    private LogAction logAction;

    /**
     * 删除的目标
     */
    @ColumnDescription(type = "varchar(50)")
    private String target;

    /**
     * 目标的名称，建立全文索引
     */
    @FullText
    @ColumnDescription(type = "varchar(500)")
    private String targetName;

    /**
     * 说明，建立全文索引
     */
    @FullText
    @Lazy(false)
    private char[] remark;

    public OperationLog()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public LogAction getLogAction()
    {
        return logAction;
    }

    public void setLogAction(LogAction logAction)
    {
        this.logAction = logAction;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getTargetName()
    {
        return targetName;
    }

    public void setTargetName(String targetName)
    {
        this.targetName = targetName;
    }

    public char[] getRemark()
    {
        return remark;
    }

    public void setRemark(char[] remark)
    {
        this.remark = remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark == null ? null : remark.toCharArray();
    }

    public String getTypeString()
    {
        return Tools.getMessage(type);
    }

    /**
     * 获得展示给用户的remark
     *
     * @return 将xml格式的remark进行处理，使得相对对用户有好些
     */
    public String getRemarkDisplayString()
    {
        if (remark != null && remark.length > 0)
        {
            Class c = null;
            try
            {
                c = Class.forName(type);
            }
            catch (ClassNotFoundException ex)
            {
                //type不是一个class，跳过，直接显示remmark
            }

            if (c != null)
            {
                Document document = null;

                try
                {
                    document = XmlUtils.createDocument(new String(remark).getBytes("UTF-8"));
                }
                catch (Exception ex)
                {
                    //remark不是xml。跳过，直接显示remmark
                }

                if (document != null)
                {
                    try
                    {
                        Object obj = XmlBeanSerializer.load(document.getDocumentElement(), c);

                        StringBuilder buffer = new StringBuilder();
                        for (PropertyInfo property : BeanUtils.getProperties(c, null))
                        {
                            Object value = property.get(obj);
                            if (value != null)
                            {
                                if (buffer.length() > 0)
                                    buffer.append("\n");

                                String name = property.getName();
                                String s = Language.getLanguage().getWord(type + "." + name, "");
                                if (!StringUtils.isEmpty(s))
                                    name = s;

                                buffer.append(name).append(":").append(DataConvert.toString(value));
                            }
                        }

                        return buffer.toString();
                    }
                    catch (Exception ex)
                    {
                        //出现错误，直接返回remark，只记录日志
                        Tools.log(ex);
                    }
                }
            }
        }

        return new String(remark);
    }

    @Override
    @IndexTimestamp
    @Index
    public Date getLogTime()
    {
        return super.getLogTime();
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof OperationLog && logId.equals(((Log) o).logId);
    }
}
