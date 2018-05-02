package com.gzzm.portal.cms.information;

import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.commons.PublishPeriodTime;
import com.gzzm.portal.commons.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 文章信息，用于在页面上展示
 *
 * @author camel
 * @date 2011-6-9
 */
public class InformationInfo implements ListItem
{
    @Inject
    private static Provider<InformationDao> daoProvider;

    /**
     * 一个全文检索显示的片段
     */
    private static class Segment
    {
        /**
         * 片段所在页码
         */
        private int pageNo;

        /**
         * 片段开始的位置
         */
        private int start;

        /**
         * 片段结束的位置
         */
        private int end;

        Segment(int pageNo, int start, int end)
        {
            this.pageNo = pageNo;
            this.start = start;
            this.end = end;
        }
    }

    private InformationBase<?, ?> information;

    /**
     * 页码，从1开始
     */
    private int pageNo = 1;

    /**
     * 全文检索时的检索内容
     */
    private String[] words;

    private Map<String, String> properties;

    private ChannelData channelObject;

    public InformationInfo(InformationBase<?, ?> information, ChannelData channelObject)
    {
        this.information = information;
        this.channelObject = channelObject;
    }

    public InformationInfo(InformationBase<?, ?> information, ChannelData channelObject, int pageNo)
    {
        if (pageNo < 1)
            pageNo = 1;

        this.information = information;
        this.channelObject = channelObject;
        this.pageNo = pageNo;
    }

    public InformationInfo(InformationBase<?, ?> information, int pageNo)
    {
        if (pageNo < 1)
            pageNo = 1;

        this.information = information;
        this.pageNo = pageNo;
    }

    protected ChannelData getChannelObject()
    {
        if (channelObject == null)
            channelObject = information.getChannel();

        return channelObject;
    }

    public Long getInfoId()
    {
        return information.getInformationId();
    }

    public String getTitle()
    {
        return information.getTitle();
    }

    public String getSubTitle()
    {
        return information.getSubTitle();
    }

    public String getType()
    {
        return information.getType().name();
    }

    public String getWords()
    {
        return information.getKeywords();
    }

    public String getSource()
    {
        return information.getSource();
    }

    public String getAuthor()
    {
        return information.getAuthor();
    }

    public String getSubject()
    {
        return information.getSubject();
    }

    public String getIndexCode()
    {
        return information.getIndexCode();
    }

    public String getFileCode()
    {
        return information.getFileCode();
    }

    public String getSummary()
    {
        return information.getSummary();
    }

    public String getOrgCode()
    {
        return information.getOrgCode();
    }

    public String getOrgName()
    {
        return information.getOrgName();
    }

    public Integer getCatalogId()
    {
        return information.getCatalogId();
    }

    public String getCatalog()
    {
        InformationCatalog catalog = information.getCatalog();

        return catalog == null ? null : catalog.getCatalogName();
    }

    public Date getPublishTime()
    {
        return information.getPublishTime();
    }

    public Date getUpdateTime()
    {
        return information.getUpdateTime();
    }

    public Integer getDeptId()
    {
        return information.getDeptId();
    }

    public Integer getReadTimes()
    {
        Integer ret = null;
        InformationReadTimes readTimes = information.getReadTimes();

        if (readTimes != null)
            ret = readTimes.getReadTimes();

        if (ret == null)
            ret = 0;

        return ret;
    }

    public boolean isValid()
    {
        return information.isValid() != null && information.isValid();
    }

    @NotSerialized
    public Map<String, String> getProperties()
    {
        if (properties == null)
        {
            properties = new Map<String, String>()
            {
                public int size()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public boolean isEmpty()
                {
                    return false;
                }

                public boolean containsKey(Object key)
                {
                    return true;
                }

                public boolean containsValue(Object value)
                {
                    return information.getProperties().containsValue(value);
                }

                public String get(Object key)
                {
                    return information.getProperties().get(key);
                }

                public String put(String key, String value)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public String remove(Object key)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public void putAll(Map<? extends String, ? extends String> m)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public void clear()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Set<String> keySet()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Collection<String> values()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Set<Entry<String, String>> entrySet()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }
            };
        }

        return properties;
    }

    /**
     * 访问此文章的url
     *
     * @param pageNo 页码
     * @return 访问此文章的url
     */
    public String getUrl(int pageNo)
    {
        return getUrl(information, channelObject, pageNo);
    }

    public static String getUrl(InformationBase information)
    {
        return getUrl(information, null, 1);
    }

    public static String getUrl(InformationBase information, ChannelData channel)
    {
        return getUrl(information, channel, 1);
    }

    public static String getUrl(InformationBase information, int pageNo)
    {
        return getUrl(information, null, pageNo);
    }

    public static String getUrl(InformationBase information, ChannelData channel, int pageNo)
    {
        if (channel == null)
            channel = information.getChannel();

        Long informationId = information.getInformationId();
        String url = channel.getUrl();

        if (!StringUtils.isEmpty(url))
        {
            if (pageNo <= 1)
                return url + "/info/" + informationId;
            else
                return url + informationId + "?pageNo=" + pageNo;
        }
        else if (!channel.getChannelId().equals(information.getChannelId()))
        {
            if (pageNo <= 1)
                return "/channel/" + channel.getChannelCode() + "/info/" + informationId;
            else
                return "/channel/" + channel.getChannelCode() + "/info/" + informationId + "?pageNo=" + pageNo;
        }
        else
        {
            if (pageNo <= 1)
                return "/info/" + informationId;
            else
                return "/info/" + informationId + "?pageNo=" + pageNo;
        }
    }

    public String getUrl()
    {
        return getUrl(pageNo);
    }

    /**
     * 获得所有页面的url
     *
     * @return 所有页面的url
     */
    @NotSerialized
    public String[] getUrls()
    {
        int n = getPageCount();
        String[] urls = new String[n];
        for (int i = 0; i < n; i++)
        {
            urls[i] = getUrl(i + 1);
        }

        return urls;
    }

    /**
     * 获得分页列表
     *
     * @return 分页列表
     */
    @NotSerialized
    public List<PageInfo> getPages()
    {
        int pageCount = getPageCount();
        if (pageCount == 1)
            return null;

        List<PageInfo> pageInfos = new ArrayList<PageInfo>();

        for (int i = 1; i <= pageCount; i++)
        {
            pageInfos.add(new PageInfo(i, getUrl(i), i == pageNo));
        }

        return pageInfos;
    }

    /**
     * 标题图片的url
     *
     * @return 标题图片的url
     */
    public String getPhoto()
    {
        return getPhoto(information);
    }

    public static String getPhoto(InformationBase<?, ?> information)
    {
        if (information.getPhoto() == null)
            return null;

        String s = "/info/" + information.getInformationId() + "/photo";

        String extName = information.getExtName();
        if (!StringUtils.isEmpty(extName))
            s += "." + extName;

        return s;
    }

    public int getPageNo()
    {
        return pageNo;
    }

    /**
     * 文章的页数
     *
     * @return 文章的页数
     */
    @NotSerialized
    public int getPageCount()
    {
        return information.getContents().size();
    }

    /**
     * 当前页的内容，包括html标签
     *
     * @return 当前页的内容
     */
    @NotSerialized
    public String getContent()
    {
        if (information.getType() == InformationType.information)
        {
            char[] content = information.getContents().get(pageNo - 1).getContent();
            if (content == null)
                return "";

            return HtmlUtils.clearComment(new String(content));
        }
        else
        {
            return "";
        }
    }

    /**
     * 当前页的内容，包括html标签，并且将阅读次数增加1
     *
     * @return 当前页的内容
     */
    @NotSerialized
    public String getContentUpdateReadTimes() throws Exception
    {
        daoProvider.get().updateReadTimes(getInfoId());

        return getContent();
    }

    /**
     * 当前页的内容，去除html标签
     *
     * @return 当前页的文本内容
     */
    @NotSerialized
    public String getText()
    {
        if (information.getType() == InformationType.information)
            return HtmlUtils.getPlainText(getContent());
        else
            return "";
    }

    public PublishPeriodTime getPeriodTime()
    {
        return new PublishPeriodTime(information.getPeriod(), information.getPeriodTime());
    }

    public void setWords(String[] words)
    {
        this.words = words;
    }

    /**
     * 获得和当前全文检索匹配的文本
     *
     * @return 和当前全文检索匹配的文本，并加上着色
     */
    @NotSerialized
    public String getMatchText() throws Exception
    {
        if (words == null || words.length == 0)
            return "";

        String[] contents = null;
        InformationType type = information.getType();
        if (type == InformationType.information)
        {
            List<? extends InformationContentBase> informationContents = information.getContents();
            int n = informationContents.size();
            contents = new String[n];

            for (int i = 0; i < n; i++)
            {
                char[] content = informationContents.get(i).getContent();
                if (content != null)
                {
                    contents[i] = HtmlUtils.getPlainText(new String(content));
                }
            }
        }
        else if (type == InformationType.file)
        {
            String content = information.getContentText();

            if (!StringUtils.isEmpty(content))
                contents = new String[]{content};
        }

        if (contents != null)
            return getRichContent(contents);

        return "";
    }

    /**
     * 文章所在的栏目的信息
     *
     * @return 文章所在的栏目的信息
     */
    @NotSerialized
    public ChannelInfo getChannel()
    {
        return new ChannelInfo(getChannelObject());
    }

    public String getTarget()
    {
        //固定用新页面打开文章
        return "_blank";
    }

    private String getRichContent(String... contents)
    {
        List<Segment> segments = new ArrayList<Segment>(words.length);
        for (String word : words)
        {
            if (word.length() > 0)
            {
                for (int pageNo = 0; pageNo < contents.length; pageNo++)
                {
                    String s = contents[pageNo];

                    if (s == null)
                        continue;

                    int index = 0;

                    while ((index = s.indexOf(word, index)) >= 0)
                    {
                        int start = 0;
                        if (index > 10)
                            start = index - 10;

                        int length = 20;
                        if (word.length() > 20)
                            length = word.length();

                        int end = index + length;
                        if (end > s.length())
                            end = s.length();

                        Segment segment = new Segment(pageNo, start, end);

                        int l = segments.size();
                        int h = 0;
                        for (; h < l; h++)
                        {
                            Segment segment2 = segments.get(h);
                            if (segment2.pageNo == segment.pageNo)
                            {
                                if ((segment2.start <= segment.end && segment2.start >= segment.start) ||
                                        (segment.start <= segment2.end && segment.start >= segment2.start))
                                {
                                    segment2.start = segment2.start > segment.start ? segment.start : segment2.start;
                                    segment2.end = segment2.end > segment.end ? segment2.end : segment.end;
                                }
                                else
                                {
                                    if (segment2.start > segment.start)
                                    {
                                        segments.add(h, segment);
                                    }
                                }
                            }
                            else if (segment2.pageNo > segment.pageNo)
                            {
                                segments.add(h, segment);
                            }
                        }

                        if (h == l)
                            segments.add(segment);

                        index += word.length();
                    }
                }
            }
        }

        String result;

        int m0 = -1;
        int m = segments.size();
        if (m > 0)
        {
            StringBuilder buffer = new StringBuilder();

            while (m != m0)
            {
                m0 = m;
                int length = 250 / m;
                for (int j = 0; j < m; j++)
                {
                    Segment segment = segments.get(j);
                    if (segment.end < segment.start + length)
                    {
                        segment.end = segment.start + length;
                        String s = contents[segment.pageNo];
                        if (s.length() < segment.end)
                            segment.end = s.length();
                    }

                    for (int k = 0; k < j; k++)
                    {
                        Segment segment2 = segments.get(k);
                        if (segment2.pageNo == segment.pageNo)
                        {
                            if ((segment2.start <= segment.end &&
                                    segment2.start >= segment.start) ||
                                    (segment.start <= segment2.end &&
                                            segment.start >= segment2.start))
                            {
                                segment2.start = segment2.start > segment.start ?
                                        segment.start : segment2.start;
                                segment2.end = segment2.end > segment.end ?
                                        segment2.end : segment.end;
                                segments.remove(j);
                                j--;
                                m--;
                            }
                        }
                    }
                }
            }

            for (int j = 0; j < m; j++)
            {
                Segment segment = segments.get(j);

                String s = contents[segment.pageNo];
                if (segment.pageNo != 0 || segment.start != 0)
                    buffer.append("...");

                buffer.append(s.substring(segment.start, segment.end));
            }

            result = buffer.toString();
        }
        else
        {
            String s = contents[0];

            if (s != null)
            {
                int length = 200;
                if (s.length() < 200)
                    length = s.length();

                result = s.substring(0, length);
            }
            else
            {
                return "";
            }
        }

        for (String word : words)
        {
            if (word.length() > 0)
                result = result.replaceAll(word, "<span class=\"highlight\">" + word + "</span>");
        }

        return result;
    }

    public List<Information> getRelatedInformationList()
    {
        if (information instanceof Information)
        {
            return ((Information) information).getRelatedInfos();
        }
        return null;
    }
}
