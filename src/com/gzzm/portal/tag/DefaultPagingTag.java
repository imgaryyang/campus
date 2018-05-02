package com.gzzm.portal.tag;

import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.annotation.Tag;

import java.util.Map;

/**
 * 默认分页标签
 *
 * @author camel
 * @date 2011-6-15
 */
@Tag(name = "paging:default", singleton = true)
public class DefaultPagingTag extends AbstractPagingTag
{
    private boolean turn = true;

    public DefaultPagingTag()
    {
    }

    public boolean isTurn()
    {
        return turn;
    }

    public void setTurn(boolean turn)
    {
        this.turn = turn;
    }

    protected String getHtml(String href, String className) throws Exception
    {
        return "<span class=\"" + className + "\">" + href + "</span>";
    }

    protected String getPageUpHtml(int pageNo, int pagingSize) throws Exception
    {
        return getHtml(getHref(pageNo, getPageUpText(pagingSize)), "pre");
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getPageUpText(int pagingSize) throws Exception
    {
        return "<<<";
    }

    protected String getPageDownHtml(int pageNo, int pagingSize) throws Exception
    {
        return getHtml(getHref(pageNo, getPageDownText(pagingSize)), "next");
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getPageDownText(int pagingSize) throws Exception
    {
        return ">>>";
    }

    protected String getPageHtml(int pageNo, boolean currentPage) throws Exception
    {
        String text = getPageText(pageNo, currentPage);
        return getHtml(currentPage ? text : getHref(pageNo, text), currentPage ? "current" : "page");
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getPageText(int pageNo, boolean currentPage) throws Exception
    {
        return Integer.toString(pageNo);
    }

    protected String getPageTurnHtml() throws Exception
    {
        return getHtml(getPageTurnText() + ":<input onkeydown=\"if(event.keyCode==13){" + getAction("'+this.value+'") + "}\">", "turn");
    }

    protected String getPageTurnText() throws Exception
    {
        return Tools.getMessage("portal.paging.turn");
    }

    @Override
    protected String getHtml(int pageNo, int totalCount, int pageCount, Map<String, Object> context) throws Exception
    {
        int pagingSize = getPagingSize(context);

        //开始页码
        int begin = 1;
        //结束页码
        int end = pageCount;

        if (pagingSize > 0)
        {
            int x = pageNo / pagingSize;
            if (pageNo % pagingSize > 0)
                x++;

            end = x * pagingSize;
            begin = end - pagingSize + 1;

            if (end > pageCount)
                end = pageCount;
        }

        StringBuilder buffer = new StringBuilder();

        //往前翻页的html
        if (begin > 1)
            buffer.append(getPageUpHtml(begin - 1, pagingSize));

        //分页html
        for (int i = begin; i <= end; i++)
            buffer.append(getPageHtml(i, i == pageNo));

        //往后翻页的html
        if (end < pageCount)
            buffer.append(getPageDownHtml(end + 1, pagingSize));

        //跳到某一页的html
        if (turn)
            buffer.append(getPageTurnHtml());

        return buffer.toString();
    }
}
