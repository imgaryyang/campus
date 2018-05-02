package com.gzzm.portal.cms.information;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.cms.commons.CmsConfig;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 信息采编和发布
 *
 * @author camel
 * @date 2011-5-17
 */
@Service
public class InformationCrudBase<I extends InformationBase0> extends BaseNormalCrud<I, Long>
{
    @Inject
    protected InformationService service;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    @Inject
    private CmsConfig config;

    private Class<I> informationClass;

    private Class<? extends InformationContentBase> contentClass;

    private InputFile photo;

    public InformationCrudBase()
    {
        setLog(true);
    }

    public InputFile getPhoto()
    {
        return photo;
    }

    public void setPhoto(InputFile photo)
    {
        this.photo = photo;
    }

    @SuppressWarnings("unchecked")
    private Class<I> getInformationClass() throws Exception
    {
        if (informationClass == null)
        {
            informationClass = BeanUtils
                    .toClass(BeanUtils.getRealType(InformationCrudBase.class, "I", BeanUtils.getRealClass(getClass())));
        }

        return informationClass;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends InformationContentBase> getContentClass() throws Exception
    {
        if (contentClass == null)
        {
            contentClass = BeanUtils.toClass(BeanUtils
                    .getRealType(InformationBase0.class, "C", getInformationClass()));
        }

        return contentClass;
    }

    @SuppressWarnings("unchecked")
    private <C extends InformationContentBase> C createContent() throws Exception
    {
        return (C) getContentClass().newInstance();
    }

    @NotSerialized
    public Integer getUserId() throws Exception
    {
        return userOnlineInfo.getUserId();
    }

    @Override
    public String getAlias()
    {
        return "i";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        InformationBase0 information = getEntity();

        //初始化页码
        List<? extends InformationContentBase> contents = information.getContents();
        if (contents != null)
        {
            int n = contents.size();
            for (int i = 0; i < n; i++)
            {
                InformationContentBase content = contents.get(i);
                content.setPageNo(i);

                //从网络中抓取图片
                if (config != null && config.isFetchImg())
                {
                    String html = content.getContentString();

                    if (!StringUtils.isEmpty(html))
                    {
                        String html1 = service.fetchImg(html);

                        if (html1 != null && !html1.equals(html))
                        {
                            content.setContent(html1.toCharArray());
                        }
                    }
                }
            }
        }

        if (photo != null)
        {
            information.setPhoto(photo.getBytes());
            information.setExtName(photo.getExtName());
        }

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initEntity(I entity) throws Exception
    {
        super.initEntity(entity);

        if (CollectionUtils.isEmpty(entity.getContents()))
        {
            //添加一个默认页
            List<InformationContentBase> contents = new ArrayList<InformationContentBase>(1);
            InformationContentBase content = createContent();
            content.setPageNo(0);
            content.setContent(new char[0]);
            contents.add(content);
            entity.setContents(contents);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        InformationBase0 information = getEntity();
        if (information.getContents() == null || information.getContents().size() == 0)
        {
            //添加一页
            List<InformationContentBase> contents = new ArrayList<InformationContentBase>(1);
            InformationContentBase content = createContent();
            content.setPageNo(0);
            content.setContent(new char[0]);
            contents.add(content);
            information.setContents(contents);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I clone(I entity) throws Exception
    {
        I c = super.clone(entity);

        //复制信息采编的内容
        List<InformationContentBase> contents = new ArrayList<InformationContentBase>(5);
        for (InformationContentBase content : (List<InformationContentBase>) entity.getContents())
        {
            InformationContentBase contentC = createContent();
            contentC.setPageNo(content.getPageNo());
            contentC.setContent(content.getContent());

            contents.add(contentC);
        }
        c.setContents(contents);

        return c;
    }

    /**
     * 获得标题图片
     *
     * @param informationId 信息ID
     * @return 标题图片的字节数组
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/{@class}/{$0}/photo")
    public byte[] getPhoto(Long informationId) throws Exception
    {
        return getEntity(informationId).getPhoto();
    }
}
