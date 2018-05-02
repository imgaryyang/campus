package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.annotation.DeptId;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.classes.Teacher;
import com.gzzm.safecampus.campus.classes.TeacherListModel;
import com.gzzm.safecampus.campus.common.Constants;
import com.gzzm.safecampus.campus.common.Importable;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.DateUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.Null;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.importers.DataRecord;
import net.cyan.crud.importers.EntityImportor;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.LabelCell;
import net.cyan.crud.view.ObjectColumn;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 排班日历Crud
 *
 * @author czy
 */
@Service(url = "/campus/bus/schedule")
public class BusScheduleView extends CalendarView<BusSchedule> implements Importable
{
    @Inject
    private BusScheduleDao busScheduleDao;

    @DeptId
    private Integer deptId;

    private Integer busId;

    private boolean readOnly;

    private Integer teacherId;

    private Integer oldTeacherId;

    private String teacherName;

    private boolean new$;

    private InputFile imp;

    @NotSerialized
    private List<BusScheduleItem> items;

    private Map<String, ScheduleType> itemMap;

    private Date startTime;

    private Date endTime;

    private TeacherListModel teacherListModel;


    public BusScheduleView()
    {
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public InputFile getImp()
    {
        return imp;
    }

    public void setImp(InputFile imp)
    {
        this.imp = imp;
    }

    public boolean isNew$()
    {
        return new$;
    }

    public void setNew$(boolean new$)
    {
        this.new$ = new$;
    }

    public List<BusScheduleItem> getItems()
    {
        return items;
    }

    public void setItems(List<BusScheduleItem> items)
    {
        this.items = items;
    }

    public Map<String, ScheduleType> getItemMap()
    {
        return itemMap;
    }

    public void setItemMap(Map<String, ScheduleType> itemMap)
    {
        this.itemMap = itemMap;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getBusId()
    {
        return busId;
    }

    public void setBusId(Integer busId)
    {
        this.busId = busId;
    }

    public Integer getTeacherId()
    {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId)
    {
        this.teacherId = teacherId;
    }

    public Integer getOldTeacherId()
    {
        return oldTeacherId;
    }

    public void setOldTeacherId(Integer oldTeacherId)
    {
        this.oldTeacherId = oldTeacherId;
    }

    @Select(field = {"teacherId"})
    public TeacherListModel getTeacherListModel() throws Exception
    {
        if (teacherListModel == null)
            teacherListModel = Tools.getBean(TeacherListModel.class);
        return teacherListModel;
    }

    @Override
    protected Date getTime(BusSchedule busSchedule)
    {
        return busSchedule.getScheduleTime();
    }

    @Override
    protected List<BusSchedule> loadItems(Date startTime, Date endTime) throws Exception
    {
        this.startTime = startTime;
        this.endTime = endTime;
        String teacherName0 = StringUtils.isEmpty(teacherName) ? null : "%" + teacherName + "%";
        return busScheduleDao.getBusSchedules(deptId, (busId == null || busId == 0 ? null : busId), startTime, endTime, teacherName0);
    }

    protected List<CalendarItemList<BusSchedule>> createList() throws Exception
    {
        String teacherName0 = StringUtils.isEmpty(teacherName) ? null : "%" + teacherName + "%";
        List<BusTeacher> busSchedules = busScheduleDao.getBusTeachers((busId == null || busId == 0 ? null : busId), startTime, endTime, deptId, teacherName0);
        if (busSchedules != null && busSchedules.size() > 0)
        {
            List<CalendarItemList<BusSchedule>> list = new ArrayList<>(busSchedules.size());
            for (BusTeacher busTeacher : busSchedules)
            {
                String id = busTeacher.getBusId() + "_" + busTeacher.getTeacherId();
                String name = busTeacher.getBusName() + "_" + busTeacher.getTeacherName();
                list.add(new CalendarItemList<BusSchedule>(id, name));
            }
            return list;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    protected boolean isItemInCalendarItemList(BusSchedule schedule, CalendarItemList<BusSchedule> calendarItemList)
    {
        if (Null.isNull(calendarItemList.getId()))
            return false;
        String busTeacherId = (String) calendarItemList.getId();
        String[] busTeacherIds = busTeacherId.split("_");
        Integer busId = Integer.valueOf(busTeacherIds[0]);
        Integer teacherId = Integer.valueOf(busTeacherIds[1]);
        return schedule.getBusId().equals(busId) && schedule.getTeacherId().equals(teacherId);
    }


    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new BusInfoListDisplay(), "busId", false);
        view.setDocType();
        view.setCellSelectable(true);
        view.setForceFit(true);
        initTime();

        view.addComponent("姓名", "teacherName");
        view.addButton(Buttons.query());
        view.addButton("新增排班", "addSchedule()");
        view.addButton("前一个星期", "previous()").setIcon(Buttons.getIcon("calendar_previous"));
        view.addButton("后一个星期", "next()").setIcon(Buttons.getIcon("calendar_next"));
        view.addButton(Buttons.imp());

        initWeekTime();

        //一星期七天，每天为一列
        DateFormat format = new SimpleDateFormat("EEE(M/dd)");
        //第一列显示小时，或者其他的名称
        view.addColumn("姓名", new LabelCell(new FieldCell("name")
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                CalendarItemList item = (CalendarItemList) entity;
                return item.getName().split("_")[1];
            }
        }.setOrderable(false)).setClass("name")).setWidth("100px");

        view.addColumn("校巴名称", new LabelCell(new FieldCell("name")
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                CalendarItemList item = (CalendarItemList) entity;
                return item.getName().split("_")[0];
            }
        }.setOrderable(false)).setClass("name"));


        for (int i = 0; i < 7; i++)
        {
            view.addColumn(new ObjectColumn(format.format(dateList[i]), "calendarItems[" + i + "]")).setLocked(
                    true).setAlign(Align.center).setWidth("95px");
        }
        view.addColumn("修改", new CButton("修改", "edit('${id}')"));
        view.addColumn("删除", new CButton("删除", "delete0('${id}')"));

        view.importCss("/platform/commons/calendar.css");
        view.importJs("/platform/commons/calendar.js");
        view.importJs("/safecampus/campus/bus/busschedulelist.js");
        view.importCss("/safecampus/campus/bus/busschedulelist.css");
        return view;
    }

    public boolean isShowTemplate()
    {
        //templatePath不为空就显示下载模板
        return StringUtils.isNotEmpty(getTemplatePath());
    }

    public String getTemplatePath()
    {
        return "/safecampus/campus/bus/校巴管理_跟车排班表导入模板.xlsx";
    }

    @Service(method = HttpMethod.post)
    public void deleteBusTeacher(String key) throws Exception
    {
        String[] busTeacherIds = key.split("_");
        Integer busId = Integer.valueOf(busTeacherIds[0]);
        Integer teacherId = Integer.valueOf(busTeacherIds[1]);
        busScheduleDao.deleteSchedule(busId, teacherId, time, DateUtils.addDate(time, 6));
    }

    @Override
    protected List<CalendarItemList<BusSchedule>> loadList(Date startTime, Date endTime) throws Exception
    {
        endTime = DateUtils.addDate(endTime, -1);
        List<BusSchedule> items = loadItems(startTime, endTime);
        List<CalendarItemList<BusSchedule>> list = createList();
        initWeekList(list, items);
        return list;
    }

    @Override
    protected void initWeekList(List<CalendarItemList<BusSchedule>> list, List<BusSchedule> items)
    {
        for (CalendarItemList<BusSchedule> calendarItemList : list)
        {
            CalendarItem<BusSchedule>[] calendarItems = new CalendarItem[7];
            for (int i = 0; i < 7; i++)
                calendarItems[i] = new CalendarItem<>(dateList[i], dateList[i + 1]);
            calendarItemList.setCalendarItems(calendarItems);
        }

        int index = 0;
        //分配每项数据到相应的日历，数据从数据库中加载时已经排序过
        for (BusSchedule item : items)
        {
            //计算此数据所属的区间，一个数据所属的区别必定不小于前一个数据的区间
            Date itemTime = getTime(item);
            for (; index < 6 && !itemTime.before(dateList[index + 1]); index++)
                ;

            for (CalendarItemList<BusSchedule> calendarItemList : list)
            {
                if (isItemInCalendarItemList(item, calendarItemList))
                    calendarItemList.getCalendarItems()[index].addItem(item);
            }
        }
    }

    @Service
    @Forward(page = Constants.IMP_PAGE)
    public String showImp() throws Exception
    {
        return null;
    }

    @Service(method = HttpMethod.post)
    public void imp() throws Exception
    {
        EntityImportor importor = new EntityImportor<BusSchedule>(BusSchedule.class)
        {
            @Override
            protected void fill(BusSchedule busSchedule, DataRecord record) throws Exception
            {
                String[] headers = getHeaders();
                int n = headers.length;
                int columnCount = record.getColumnCount();
                if (n > columnCount)
                    n = columnCount;

                Map<String, String> attributes = null;
                for (int i = 0; i < n; i++)
                {
                    String header = headers[i];
                    if (header.equals("校巴名称"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setBusLicense(value);
                    } else if (header.equals("工号"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setTeacherNo(value);
                    } else if (header.equals("老师姓名"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setTeacherName(value);
                    } else if (header.equals("星期日"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setSunday(value);
                    } else if (header.equals("星期一"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setMonday(value);
                    } else if (header.equals("星期二"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setTuesday(value);
                    } else if (header.equals("星期三"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setWednesday(value);
                    } else if (header.equals("星期四"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setThursday(value);
                    } else if (header.equals("星期五"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setFriday(value);
                    } else if (header.equals("星期六"))
                    {
                        String value = record.get(String.class, i);
                        busSchedule.setSaturday(value);
                    }

                }
            }

            @Override
            protected void save(BusSchedule entity, DataRecord record) throws Exception
            {
                //entity是excel每一行的内容
                //获取校巴id
                initTime();
                Integer busId = busScheduleDao.getBusIdByBusLicense(entity.getBusLicense(), deptId);
                if (busId != null)
                {
                    Integer teacherId = busScheduleDao.getTeacherId(entity.getTeacherNo(), entity.getTeacherName(), deptId);
                    if (teacherId != null)
                    {
                        boolean existsSchedule = busScheduleDao.existsSchedule(busId, teacherId, new java.sql.Date(getTime().getTime()), getsqlDate(6));
                        if (!existsSchedule)
                        {
                            if (StringUtils.isNotBlank(entity.getSunday()))
                            {
                                //星期日
                                BusSchedule busSchedule = new BusSchedule();
                                busSchedule.setScheduleTime(new java.sql.Date(getTime().getTime()));
                                busSchedule.setScheduleType(getType(entity.getSunday()));
                                getSchedule(busSchedule, busId, teacherId);
                                busScheduleDao.save(busSchedule);
                            }
                            if (StringUtils.isNotBlank(entity.getMonday()))
                            {
                                //星期一
                                BusSchedule busSchedule = new BusSchedule();
                                busSchedule.setScheduleTime(getsqlDate(1));
                                busSchedule.setScheduleType(getType(entity.getMonday()));
                                getSchedule(busSchedule, busId, teacherId);
                                busScheduleDao.save(busSchedule);
                            }
                            if (StringUtils.isNotBlank(entity.getTuesday()))
                            {
                                //星期二
                                BusSchedule busSchedule = new BusSchedule();
                                busSchedule.setScheduleTime(getsqlDate(2));
                                busSchedule.setScheduleType(getType(entity.getTuesday()));
                                getSchedule(busSchedule, busId, teacherId);
                                busScheduleDao.save(busSchedule);
                            }
                            if (StringUtils.isNotBlank(entity.getWednesday()))
                            {
                                //星期三
                                BusSchedule busSchedule = new BusSchedule();
                                busSchedule.setScheduleTime(getsqlDate(3));
                                busSchedule.setScheduleType(getType(entity.getWednesday()));
                                getSchedule(busSchedule, busId, teacherId);
                                busScheduleDao.save(busSchedule);
                            }
                            if (StringUtils.isNotBlank(entity.getThursday()))
                            {
                                //星期四
                                BusSchedule busSchedule = new BusSchedule();
                                busSchedule.setScheduleTime(getsqlDate(4));
                                busSchedule.setScheduleType(getType(entity.getThursday()));
                                getSchedule(busSchedule, busId, teacherId);
                                busScheduleDao.save(busSchedule);
                            }
                            if (StringUtils.isNotBlank(entity.getFriday()))
                            {
                                //星期五
                                BusSchedule busSchedule = new BusSchedule();
                                busSchedule.setScheduleTime(getsqlDate(5));
                                busSchedule.setScheduleType(getType(entity.getFriday()));
                                getSchedule(busSchedule, busId, teacherId);
                                busScheduleDao.save(busSchedule);
                            }
                            if (StringUtils.isNotBlank(entity.getSaturday()))
                            {
                                //星期六
                                BusSchedule busSchedule = new BusSchedule();
                                busSchedule.setScheduleTime(getsqlDate(6));
                                busSchedule.setScheduleType(getType(entity.getSaturday()));
                                getSchedule(busSchedule, busId, teacherId);
                                busScheduleDao.save(busSchedule);
                            }
                        } else
                        {
                            throw new NoErrorException("[" + entity.getTeacherName() + "]" + "该老师当周已排班！");
                        }
                    } else
                    {
                        throw new NoErrorException("[" + entity.getTeacherName() + "]" + "该老师信息不匹配！");
                    }
                } else
                {
                    throw new NoErrorException("[" + entity.getBusLicense() + "]" + "该校巴不存在！");
                }
            }
        };

        try
        {
            importor.load(this.imp);
            importor.imp();
        } finally
        {
            importor.close();
        }
    }


    //获得该天的排班类型
    private ScheduleType getType(String day)
    {
        ScheduleType scheduleType = null;
        if (day.equals("全天"))
        {
            scheduleType = ScheduleType.ALLDAY;
        } else if (day.equals("晚班"))
        {
            scheduleType = ScheduleType.NIGHT;
        } else if (day.equals("早班"))
        {
            scheduleType = ScheduleType.MORNING;
        }
        return scheduleType;
    }

    private java.sql.Date getsqlDate(Integer n)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new java.sql.Date(getTime().getTime()));
        calendar.add(calendar.DATE, n);
        date = calendar.getTime();
        return new java.sql.Date(date.getTime());
    }

    //组装排班实体
    private BusSchedule getSchedule(BusSchedule busSchedule, Integer busId, Integer teacherId) throws Exception
    {
        busSchedule.setBusId(busId);
        busSchedule.setTeacherId(teacherId);
        busSchedule.setDeptId(deptId);
        return busSchedule;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void saveSchedule() throws Exception
    {
        boolean isNull = true;
        for (Map.Entry<String, ScheduleType> i : itemMap.entrySet())
        {
            if (!Null.isNull(i.getValue()))
            {
                isNull = false;
            }
        }
        if (isNull)
        {
            throw new NoErrorException("请选择排班!");
        }
        Date weekStart = DateUtils.getWeekStart(time, 1);
        Date weekEnd = DateUtils.getWeekEnd(time);
        if (isNew$() || (!oldTeacherId.equals(teacherId)))
        {
            //新增或修改老师的时候检查存在排班
            boolean existsSchedule = busScheduleDao.existsSchedule(busId, teacherId, weekStart, weekEnd);
            if (existsSchedule)
            {
                throw new NoErrorException("该老师当周已有排班表!");
            }
        }
        if (!isNew$())
        {
            //修改，先删除本周现有的记录，然后再add
            busScheduleDao.deleteSchedule(busId, oldTeacherId, weekStart, weekEnd);
        }
        //保存排班
        for (Map.Entry<String, ScheduleType> i : itemMap.entrySet())
        {
            //循环获取排班类型
            if (i.getValue() != null)
            {
                BusSchedule busSchedule = new BusSchedule();
                busSchedule.setDeptId(deptId);
                String date = i.getKey();
                busSchedule.setScheduleTime(DateUtils.toDate(date));
                busSchedule.setTeacherId(teacherId);
                busSchedule.setBusId(getBusId());
                busSchedule.setScheduleType(i.getValue());
                busScheduleDao.save(busSchedule);
            }
        }
    }

    @Service(url = "/campus/bus/schedule/add/{$0}/{$1}")
    public String add(Integer busId, Long weekTime)
    {
        setReadOnly(false);
        setNew$(true);
        setBusId(busId);
        items = new ArrayList<>(7);
        setTime(new Date(weekTime));
        for (int i = 0; i < 7; i++)
        {
            BusScheduleItem item = new BusScheduleItem();
            Date scheduleTime = DateUtils.addDate(time, i);
            item.setScheduleTime(scheduleTime);
            item.setScheduleName(DateUtils.toString(scheduleTime, "E"));
            items.add(item);
        }
        return "/safecampus/campus/bus/busschedule.ptl";
    }

    @Service(url = "/campus/bus/schedule/show/{$0}/{$1}")
    public String showScheduleDetail(String busTeacherId, Long weekTime) throws Exception
    {
        setReadOnly(true);
        setNew$(false);
        String[] busTeacherIds = busTeacherId.split("_");
        setBusId(Integer.valueOf(busTeacherIds[0]));
        setTeacherId(Integer.valueOf(busTeacherIds[1]));
        setTeacherName(busScheduleDao.load(Teacher.class, teacherId).getTeacherName());
        items = new ArrayList<>(7);
        setTime(new Date(weekTime));
        for (int i = 0; i < 7; i++)
        {
            BusScheduleItem busScheduleItem = new BusScheduleItem();
            Date scheduleTime = DateUtils.addDate(time, i);
            busScheduleItem.setScheduleTime(scheduleTime);
            busScheduleItem.setScheduleName(DateUtils.toString(scheduleTime, "E"));
            BusSchedule busSchedule = busScheduleDao.getBusScheduleByTeaDate(teacherId, busId, scheduleTime);
            if (busSchedule != null)
                busScheduleItem.setType(busSchedule.getScheduleType());
            items.add(busScheduleItem);
        }
        return "/safecampus/campus/bus/busschedule.ptl";
    }

    /**
     * 重写实现一周从周一开始
     */
    @Override
    protected void initWeekTime()
    {
        if (date == null)
            date = DateUtils.truncate(time);

        if (type == CalendarViewType.weekday)
        {
            time = date;
        } else
        {
            //周视图，从周一开始
            time = DateUtils.getWeekStart(time, 1);
        }

        if (dateList == null)
        {
            if (date == null)
                date = new Date();

            date = DateUtils.truncate(date);

            dateList = new Date[8];

            if (type == CalendarViewType.week)
            {
                dateList[0] = time;
                for (int i = 1; i < 8; i++)
                {
                    dateList[i] = DateUtils.addDate(time, i);
                }
            } else
            {
                dateList[3] = date;
                for (int i = 1; i <= 3; i++)
                {
                    dateList[3 - i] = DateUtils.addDate(date, -i);
                }

                for (int i = 1; i <= 4; i++)
                {
                    dateList[3 + i] = DateUtils.addDate(date, i);
                }
            }
        }
    }
}
