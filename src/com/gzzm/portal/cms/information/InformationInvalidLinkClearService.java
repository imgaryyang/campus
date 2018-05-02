package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.HtmlUtils;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2017/11/23
 */
public class InformationInvalidLinkClearService
{
    private HtmlUtils.URLMatcher invalidLinksClear;

    @Inject
    private InformationInvalidLinkClearDao dao;

    private Long lastInformationId;

    private Long lastInformationId1;

    public InformationInvalidLinkClearService()
    {
    }

    public void clear(String server) throws Exception
    {
        invalidLinksClear = new HtmlUtils.InvalidLinksClear(server)
        {
            @Override
            public String onMatch(String s, String url, String innerHTML) throws Exception
            {
                String s1 = super.onMatch(s, url, innerHTML);

                if (s1 != null)
                {
                    Tools.log("clear links " + url);
                }

                return s1;
            }
        };

        clearInformations();
        clearInformationEdits();
    }

    public void clearInformations() throws Exception
    {
        if (lastInformationId == null)
            lastInformationId = Tools.getConfig("lastClearLinksInformationId", Long.class);

        while (true)
        {
            List<Long> informationIds = dao.queryInformationIds(lastInformationId);
            if (informationIds.size() == 0)
                return;

            for (Long informationId : informationIds)
            {
                Tools.log("clearing informationId:" + informationId);

                clearInformation(dao.getInformation(informationId), InformationContent.class);

                lastInformationId = informationId;
                Tools.setConfig("lastClearLinksInformationId", lastInformationId);
            }
        }
    }

    public void clearInformationEdits() throws Exception
    {
        if (lastInformationId1 == null)
            lastInformationId1 = Tools.getConfig("lastClearLinksInformationEditId", Long.class);

        while (true)
        {
            List<Long> informationIds = dao.queryInformationEditIds(lastInformationId1);
            if (informationIds.size() == 0)
                return;

            for (Long informationId : informationIds)
            {
                clearInformation(dao.getInformationEdit(informationId), InformationContentEdit.class);

                lastInformationId1 = informationId;
                Tools.setConfig("lastClearLinksInformationEditId", lastInformationId1);
            }
        }
    }

    private <C extends InformationContentBase> void clearInformation(
            InformationBase<C, ?> information, Class<C> contentType) throws Exception
    {
        if (information.getType() == InformationType.information)
        {
            Long informationId = information.getInformationId();

            for (C content : information.getContents())
            {
                char[] chars = content.getContent();
                if (chars != null)
                {
                    String text0 = new String(chars);

                    String text = HtmlUtils.matchHrefs(text0, invalidLinksClear);
                    text = HtmlUtils.matchImgs(text, invalidLinksClear);

                    if (!text0.equals(text))
                    {
                        C content1 = contentType.newInstance();
                        content1.setInformationId(informationId);
                        content1.setPageNo(content.getPageNo());
                        content1.setContent(text.toCharArray());
                        dao.update(content1);

                        InvalidLinkInformation content2 = new InvalidLinkInformation();
                        content2.setInformationId(informationId);
                        content2.setPageNo(content.getPageNo());
                        content2.setContent(chars);
                        dao.save(content2);

                        Tools.log("clear links informationId:" + informationId);
                    }
                }
            }
        }
    }
}
