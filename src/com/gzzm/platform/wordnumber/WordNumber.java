package com.gzzm.platform.wordnumber;

import com.gzzm.platform.commons.SystemException;
import net.cyan.commons.util.*;
import net.cyan.thunwind.annotation.Index;

import java.util.*;
import java.util.regex.*;

/**
 * 字号
 *
 * @author camel
 * @date 11-10-27
 */
public class WordNumber
{
    private static final Pattern PATTERN = Pattern.compile("\\$[a-zA-Z]+\\([^)]*\\)");

    private static final Map<String, Class<? extends WordNumberElement>> ELEMENT_CLASSES =
            new HashMap<String, Class<? extends WordNumberElement>>();

    /**
     * 元素列表
     */
    private List<WordNumberElement> elements = new ArrayList<WordNumberElement>();

    /**
     * 字号所属的部门
     */
    @Index
    private Integer deptId;

    /**
     * 字号的类型，标识字号的通途，如od表示公文，ga表示审批，等
     *
     * @see Serial#type
     */
    private String type;

    /**
     * 执行编号的时间
     */
    private Date time;

    /**
     * 扩展的属性
     */
    private Map<String, Object> properties;

    static
    {
        ELEMENT_CLASSES.put("string", StringElement.class);
        ELEMENT_CLASSES.put("year", YearElement.class);
        ELEMENT_CLASSES.put("serial", SerialElement.class);
        ELEMENT_CLASSES.put("date", DateElement.class);
        ELEMENT_CLASSES.put("var", VarElement.class);
    }

    public WordNumber(String s) throws Exception
    {
        parse(s);
    }

    public WordNumber(List<WordNumberElement> elements)
    {
        this.elements = elements;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Date getTime()
    {
        if (time == null)
            time = new Date();
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setProperty(String name, Object value)
    {
        if (properties == null)
            properties = new HashMap<String, Object>();

        properties.put(name, value);
    }

    public Object getProperty(String name)
    {
        return properties == null ? null : properties.get(name);
    }

    public List<WordNumberElement> getElements()
    {
        return Collections.unmodifiableList(elements);
    }

    private void parse(String s) throws Exception
    {
        StringUtils.match(s, PATTERN, new StringUtils.StringMatcher<Object>()
        {
            /**
             * 记录上一次位移
             */
            private int pos;

            public void onMatch(String s, String g, int start) throws Exception
            {
                if (start > pos)
                    elements.add(new StringElement(s.substring(pos, start)));

                elements.add(parseElement(g));

                pos = start + g.length();
            }

            public Object getResult(String s)
            {
                if (pos < s.length())
                    elements.add(new StringElement(s.substring(pos, s.length())));
                return null;
            }
        });

        if (elements.size() == 0)
            elements.add(new StringElement(""));
    }

    private WordNumberElement parseElement(String s) throws Exception
    {
        int index = s.indexOf('(');

        String name = s.substring(1, index);
        String parameter = s.substring(index + 1, s.length() - 1);

        Class<? extends WordNumberElement> c = ELEMENT_CLASSES.get(name);
        if (c == null)
            throw new SystemException("unrecognized element " + name);

        WordNumberElement element = c.newInstance();
        element.parse(parameter);

        return element;
    }

    public String getResult() throws Exception
    {
        StringBuilder buffer = new StringBuilder();
        for (WordNumberElement element : elements)
        {
            buffer.append(element.getResult(this));
        }

        return buffer.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        for (WordNumberElement element : elements)
        {
            buffer.append(element.toString());
        }

        return buffer.toString();
    }

    public String getPattern()
    {
        StringBuilder buffer = new StringBuilder();

        for (WordNumberElement element : elements)
        {
            buffer.append(element.getPattern());
        }

        return buffer.toString();
    }

    public String save()
    {
        StringBuilder buffer = new StringBuilder();
        for (WordNumberElement element : elements)
        {
            buffer.append(element.save());
        }

        return buffer.toString();
    }

    public boolean matches(String s)
    {
        return Pattern.compile(getPattern()).matcher(s).matches();
    }

    public List<Pair<WordNumberElement, String>> match(String s)
    {
        List<Pair<WordNumberElement, String>> result = new ArrayList<Pair<WordNumberElement, String>>(elements.size());

        for (WordNumberElement element : elements)
        {
            Pattern pattern = Pattern.compile(element.getPattern());
            Matcher matcher = pattern.matcher(s);
            if (matcher.find())
            {
                String part = matcher.group();

                result.add(new Pair<WordNumberElement, String>(element, part));

                s = s.substring(part.length());
            }
            else
            {
                return null;
            }
        }

        return result;
    }

    public List<SerialMatch> matchSerials(String s) throws Exception
    {
        List<Pair<WordNumberElement, String>> matches = match(s);
        if (matches == null)
            return null;

        List<SerialMatch> serialMatches = new ArrayList<SerialMatch>();
        for (Pair<WordNumberElement, String> match : matches)
        {
            if (match.getKey() instanceof SerialElement)
            {
                SerialMatch serialMatch = ((SerialElement) match.getKey()).match(match.getValue(), this);
                serialMatches.add(serialMatch);
            }
        }

        return serialMatches;
    }
}
