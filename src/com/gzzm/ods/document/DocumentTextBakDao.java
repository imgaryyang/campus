package com.gzzm.ods.document;

import net.cyan.thunwind.annotation.OQL;

import java.util.List;

/**
 * @author camel
 * @date 11-10-20
 */
public abstract class DocumentTextBakDao extends OdDao
{
    public DocumentTextBakDao()
    {
    }

    public DocumentTextBak getBak(Long bakId) throws Exception
    {
        return load(DocumentTextBak.class, bakId);
    }

    public DocumentText getText(Long textId) throws Exception
    {
        return load(DocumentText.class, textId);
    }

    @OQL("select b from DocumentTextBak b where b.textId=:1 order by b.saveTime desc")
    public abstract List<DocumentTextBak> getBaks(Long textId) throws Exception;
}
