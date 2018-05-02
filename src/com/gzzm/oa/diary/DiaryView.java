package com.gzzm.oa.diary;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author wxj
 * @date 11-10-27
 */
@Service(url = "/oa/diary/view")
public class DiaryView extends CalendarView<Diary>
{
    @Inject
    private DiaryDao dao;

    /**
     * 注入当前用户的ID
     */
    @UserId
    private Integer userId;

    public DiaryView()
    {
    }

    public void setType(CalendarViewType type)
    {
        this.type = type;
    }

    @Override
    protected Date getTime(Diary diary)
    {
        return diary.getDiaryTime();
    }

    @Override
    protected List<Diary> loadItems(Date startTime, Date endTime) throws Exception
    {
        return dao.getUserDiaries(userId, startTime, endTime);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        initView(view);

        view.importCss("/oa/diary/view.css");
        view.importJs("/oa/diary/view.js");

        return view;
    }

    @Service(url = "/oa/diary/view/{$0}")
    public Diary loadDiary(Integer diaryId) throws Exception
    {
        return dao.getDiary(diaryId);
    }

    @Service(url = "/oa/diary/view.load")
    public List<Diary> loadDiaries(Integer[] diaryIds) throws Exception
    {
        return dao.getDiaries(diaryIds);
    }

    @ObjectResult
    @Service(url = "/oa/diary/view.remove")
    public void removeDiary(Integer diaryId) throws Exception
    {
        dao.deleteDiary(diaryId);
    }
}
