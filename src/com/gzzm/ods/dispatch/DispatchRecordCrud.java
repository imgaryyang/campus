package com.gzzm.ods.dispatch;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.*;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 公文发送记录，可手工录入
 *
 * @author LDP
 * @date 2017/7/18
 */
@Service(url = "/ods/dispatch/dispatchrecordcrud")
public class DispatchRecordCrud extends BaseNormalCrud<DispatchRecord, Long>
{

    @Inject
    private DispatchRecordDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 发文编号
     */
    @Like
    private String sendNumber;

    /**
     * 公文的标题
     */
    @Like
    private String title;

    /**
     * 发起单位
     */
    @Like
    private String deptName;

    /**
     * 状态
     * 0=未办结
     * 1=已办结
     */
    private Boolean finishTag;

    @Lower(column = "sendTime")
    private Date sendTime_start;

    @Upper(column = "sendTime")
    private Date sendTime_end;

    @Lower(column = "limitDate")
    private Date limitDate_start;

    @Upper(column = "limitDate")
    private Date limitDate_end;

    @Lower(column = "limitDate")
    private Date onlyLimitDate_start;

    @Upper(column = "limitDate")
    private Date onlyLimitDate_end;

    /**
     * 1=外部发文
     * 2=OA发文
     */
    private Integer type;

    /**
     * 告警类型
     * 0=黄牌；1=红牌；2=红黄牌
     */
    private Integer warningType;

    public DispatchRecordCrud()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Date getSendTime_start()
    {
        return sendTime_start;
    }

    public void setSendTime_start(Date sendTime_start)
    {
        this.sendTime_start = sendTime_start;
    }

    public Date getSendTime_end()
    {
        return sendTime_end;
    }

    public void setSendTime_end(Date sendTime_end)
    {
        this.sendTime_end = sendTime_end;
    }

    public Date getLimitDate_start()
    {
        return limitDate_start;
    }

    public void setLimitDate_start(Date limitDate_start)
    {
        this.limitDate_start = limitDate_start;
    }

    public Date getLimitDate_end()
    {
        return limitDate_end;
    }

    public void setLimitDate_end(Date limitDate_end)
    {
        this.limitDate_end = limitDate_end;
    }

    public Date getOnlyLimitDate_start()
    {
        return onlyLimitDate_start;
    }

    public void setOnlyLimitDate_start(Date onlyLimitDate_start)
    {
        this.onlyLimitDate_start = onlyLimitDate_start;
    }

    public Date getOnlyLimitDate_end()
    {
        return onlyLimitDate_end;
    }

    public void setOnlyLimitDate_end(Date onlyLimitDate_end)
    {
        this.onlyLimitDate_end = onlyLimitDate_end;
    }

    public Boolean getFinishTag()
    {
        return finishTag;
    }

    public void setFinishTag(Boolean finishTag)
    {
        this.finishTag = finishTag;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getWarningType()
    {
        return warningType;
    }

    public void setWarningType(Integer warningType)
    {
        this.warningType = warningType;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String oql = " createDeptId in ?authDeptIds ";

        if(!Null.isNull(type))
        {
            if(type == 1) oql += " and sendId is null";
            else if(type == 2) oql += " and sendId is not null";
        }

        if(!Null.isNull(limitDate_start) || !Null.isNull(limitDate_end))
        {
            oql += " and redWarnTag = 1";
        }

        if(!Null.isNull(warningType))
        {
            if(warningType == 0)
            {
                oql +=
                        " and (finishTag is null or finishTag = 0) and warningDate is not null and (redWarnTag = 0 or redWarnTag is null) and warningDate < sysdate() ";
            }
            else if(warningType == 1)
            {
                oql += " and redWarnTag = 1";
            }
            else if(warningType == 2)
            {
                oql +=
                        " and ((redWarnTag = 1) or ((finishTag is null or finishTag = 0) and warningDate is not null and (redWarnTag = 0 or redWarnTag is null) and warningDate < sysdate()))";
            }
        }
        return oql;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.setClass("${receipt.newReplyCount > 0 ? 'new_bold' : ''}");

        view.addComponent("预警", new CCombox("warningType", new Object[]{
                new KeyValue<String>("0", "黄牌"),
                new KeyValue<String>("1", "红牌"),
                new KeyValue<String>("2", "红黄牌")
        })).setWidth("70px");
        view.addComponent("类型", new CCombox("type", new Object[]{
                new KeyValue<String>("1", "纸质公文"),
                new KeyValue<String>("2", "OA发文")
        }));
        view.addComponent("状态", new CCombox("finishTag", new Object[]{
                new KeyValue<String>("false", "未办结"),
                new KeyValue<String>("true", "已办结")
        }));
        view.addComponent("文件名称", "title");
        view.addComponent("发起单位", "deptName");
        view.addMoreComponent("发起时间", "sendTime_start", "sendTime_end");
        view.addMoreComponent("红牌发生时间", "limitDate_start", "limitDate_end");
        view.addMoreComponent("限时办结时间", "onlyLimitDate_start", "onlyLimitDate_end");

        view.addColumn("预警", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object o) throws Exception
            {
                DispatchRecord dr = (DispatchRecord) o;

                if(dr.getRedWarnTag())
                {
                    return "<img src='/ods/dispatch/images/red.png' style='width:15px;height:15px'>";
                }
                else
                {
                    if(dr.getFinishTag() == null || !dr.getFinishTag())
                    {
                        if(dr.getWarningDate() == null || dr.getWarningDate().after(new java.util.Date()))
                        {
                            return "";
                        }
                        else
                        {
                            return "<img src='/ods/dispatch/images/yellow.png' style='width:15px;height:15px'>";
                        }
                    }
                    else
                    {
                        return "";
                    }
                }
            }
        }).setWidth("40").setAlign(Align.center);

        view.addColumn("类型", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                DispatchRecord record = (DispatchRecord) entity;
                if(record.getSendId() == null) return "纸质公文";
                else return "OA发文";
            }
        }).setWidth("80").setAlign(Align.center);
        view.addColumn("文件名称", new HrefCell("title").setProperty("title", "${title}").setAction(Actions.show(null)));
        view.addColumn("发起单位", "deptName").setWidth("200");
        view.addColumn("状态", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                DispatchRecord dr = (DispatchRecord) entity;
                return dr.getFinishTag() != null && dr.getFinishTag() ? "已办结" : "未办结";
            }
        }).setWidth("80").setAlign(Align.center);
        view.addColumn("录入人", "creatorName").setAlign(Align.center);
        view.addColumn("发起时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("130");
        view.addColumn("限时办结时间", new FieldCell("limitDate").setFormat("yyyy-MM-dd HH:mm")).setWidth("130");
        view.addColumn("查看反馈", new ConditionComponent()
                .add("receiptId != null", new CHref("查看反馈").setAction("showReceipt(${receiptId})"))).setWidth("75");

        view.setCheckable("${sendId == null}");

        view.defaultInit(false);
        view.addButton("导出表格", "exportXlx()");

        view.importJs("/ods/dispatch/dispatch.js");
        return view;
    }

    @Override
    public boolean isReadOnly()
    {
        if("show".equals(getAction()))
        {
            return getEntity().getSendId() != null;
        }
        return super.isReadOnly();
    }

    @Override
    @Forward(page = "/ods/dispatch/dispatch.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/ods/dispatch/dispatch.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    /**
     * 保存实体
     */
    @Transactional
    @Service(method = HttpMethod.post)
    public Long saveData() throws Exception
    {
        return saveEntity(getEntity(), Null.isNull(getEntity().getRecordId()));
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setCreatorId(userOnlineInfo.getUserId());
        getEntity().setCreateDeptId(userOnlineInfo.getBureauId());
        return super.beforeInsert();
    }

    @Service(url = "/ods/dispatch/dispatchrecordcrud/exportXLS")
    public InputFile exportXLS() throws Exception
    {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFCellStyle background = wb.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        //背景色
        background.setFillPattern(CellStyle.SOLID_FOREGROUND);
        background.setBottomBorderColor(HSSFColor.BLACK.index);
        background.setFillForegroundColor(HSSFColor.PALE_BLUE.index);

        //边框
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        background.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        background.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        background.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        background.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        background.setWrapText(true);
        background.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        background.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        //建立新的sheet对象（excel的表单）
        HSSFSheet sheet = wb.createSheet("公文办理信息表");
        //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        HSSFRow row1 = sheet.createRow(0);
        //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        HSSFCell cell = row1.createCell(0);
        //设置单元格内容
        cell.setCellValue("公文办理信息一览表");
        //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        CellRangeAddress brodercel0 = new CellRangeAddress(0, 0, 0, 14);
        sheet.addMergedRegion(brodercel0);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel0, sheet, wb);
        cell.setCellStyle(background);
        //在sheet里创建第二行
        HSSFRow row2 = sheet.createRow(1);
        HSSFRow row3 = sheet.createRow(2);
        //创建单元格并设置单元格内容
        row2.createCell(0).setCellValue("序号");
        CellRangeAddress brodercell = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(brodercell);
        row2.getCell(0).setCellStyle(background);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercell, sheet, wb);

        row2.createCell(1).setCellValue("预警");
        CellRangeAddress brodercel11 = new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegion(brodercel11);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel11, sheet, wb);
        row2.getCell(1).setCellStyle(background);

        row2.createCell(2).setCellValue("类型");
        CellRangeAddress brodercel2 = new CellRangeAddress(1, 2, 2, 2);
        sheet.addMergedRegion(brodercel2);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel2, sheet, wb);
        row2.getCell(2).setCellStyle(background);
        row2.createCell(3).setCellValue("状态");
        CellRangeAddress brodercel3 = new CellRangeAddress(1, 2, 3, 3);
        sheet.addMergedRegion(brodercel3);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel3, sheet, wb);
        row2.getCell(3).setCellStyle(background);
        row2.createCell(4).setCellValue("文件名称");
        CellRangeAddress brodercel4 = new CellRangeAddress(1, 2, 4, 4);
        sheet.addMergedRegion(brodercel4);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel4, sheet, wb);
        row2.getCell(4).setCellStyle(background);
        row2.createCell(5).setCellValue("发起单位");
        CellRangeAddress brodercel5 = new CellRangeAddress(1, 2, 5, 5);
        sheet.addMergedRegion(brodercel5);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel5, sheet, wb);
        row2.getCell(5).setCellStyle(background);

        row2.createCell(6).setCellValue("录入人");
        CellRangeAddress brodercel6 = new CellRangeAddress(1, 2, 6, 6);
        sheet.addMergedRegion(brodercel6);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel6, sheet, wb);
        row2.getCell(6).setCellStyle(background);

        row2.createCell(7).setCellValue("发起时间");
        CellRangeAddress brodercel7 = new CellRangeAddress(1, 2, 7, 7);
        sheet.addMergedRegion(brodercel7);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel7, sheet, wb);
        row2.getCell(7).setCellStyle(background);

        row2.createCell(8).setCellValue("限时办结时间");
        CellRangeAddress brodercel8 = new CellRangeAddress(1, 2, 8, 8);
        sheet.addMergedRegion(brodercel8);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel8, sheet, wb);
        row2.getCell(8).setCellStyle(background);


        HSSFCell cel3 = row2.createCell(9);
        cel3.setCellValue("回复信息");
        cel3.setCellStyle(background);
        CellRangeAddress brodercel9 = new CellRangeAddress(1, 1, 9, 14);
        sheet.addMergedRegion(brodercel9);
        setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, brodercel9, sheet, wb);
        row3.createCell(9).setCellValue("办理单位");
        row3.getCell(9).setCellStyle(background);
        row3.createCell(10).setCellValue("联系人");
        row3.getCell(10).setCellStyle(background);
        row3.createCell(11).setCellValue("联系电话");
        row3.getCell(11).setCellStyle(background);
        row3.createCell(12).setCellValue("回复时间");
        row3.getCell(12).setCellStyle(background);
        row3.createCell(13).setCellValue("回复状态");
        row3.getCell(13).setCellStyle(background);
        row3.createCell(14).setCellValue("备注");
        row3.getCell(14).setCellStyle(background);
        sheet.setColumnWidth(4, 30 * 255);
        sheet.setColumnWidth(5, 15 * 255);
        sheet.setColumnWidth(7, 15 * 255);
        sheet.setColumnWidth(8, 15 * 255);
        sheet.setColumnWidth(9, 15 * 255);
        sheet.setColumnWidth(11, 15 * 255);
        sheet.setColumnWidth(12, 15 * 255);
        sheet.setColumnWidth(13, 15 * 255);
        sheet.setColumnWidth(14, 20 * 255);

        int i = 3;
        int index=1;
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");*/
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List list = this.loadAll();
        List<DispatchRecord> dispatchRecords = list;
        for (DispatchRecord dr : dispatchRecords)
        {
            //在sheet里创建第三行
            int row = 0;
            if(dr.getItems() != null && dr.getItems().size() > 0)
            {
                row = dr.getItems().size() - 1;
            }
            int mark = i;
            if(dr.getItems() != null && dr.getItems().size() > 0)
            {
                for (DispatchItem di : dr.getItems())
                {
                    HSSFRow row4 = sheet.createRow(mark);
                    row4.createCell(9).setCellValue(di.getDeptName());
                    row4.getCell(9).setCellStyle(cellStyle);
                    row4.createCell(10).setCellValue(di.getUserName());
                    row4.getCell(10).setCellStyle(cellStyle);
                    row4.createCell(11).setCellValue(di.getPhone());
                    row4.getCell(11).setCellStyle(cellStyle);
                    row4.createCell(12).setCellValue(di.getReplayTime() == null ? "" : sdf1.format(di.getReplayTime()));
                    row4.getCell(12).setCellStyle(cellStyle);
                    row4.createCell(13).setCellValue(di.getReplayTime() == null || dr.getLimitDate() == null ? "" :
                            di.getReplayTime().getTime() - dr.getLimitDate().getTime() > 0 ?
                                    getOverTime(di.getReplayTime(), dr.getLimitDate()) : "正常回复");
                    row4.getCell(13).setCellStyle(cellStyle);
                    row4.createCell(14).setCellValue(di.getRemark() == null ? "" : di.getRemark());
                    row4.getCell(14).setCellStyle(cellStyle);
                    mark++;
                }
            }
            else
            {
                HSSFRow row4 = sheet.createRow(i);
                row4.createCell(9).setCellValue("");
                row4.getCell(9).setCellStyle(cellStyle);
                row4.createCell(10).setCellValue("");
                row4.getCell(10).setCellStyle(cellStyle);
                row4.createCell(11).setCellValue("");
                row4.getCell(11).setCellStyle(cellStyle);
                row4.createCell(12).setCellValue("");
                row4.getCell(12).setCellStyle(cellStyle);
                row4.createCell(13).setCellValue("");
                row4.getCell(13).setCellStyle(cellStyle);
                row4.createCell(14).setCellValue("");
                row4.getCell(14).setCellStyle(cellStyle);
            }

            HSSFRow row5 = sheet.getRow(i);
            row5.createCell(0).setCellValue(index);
            row5.getCell(0).setCellStyle(cellStyle);
            CellRangeAddress c0 = new CellRangeAddress(i, i + row, 0, 0);
            sheet.addMergedRegion(c0);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c0, sheet, wb);

            row5.createCell(1).setCellValue(getWarn(dr));
            row5.getCell(1).setCellStyle(cellStyle);
            CellRangeAddress c1 = new CellRangeAddress(i, i + row, 1, 1);
            sheet.addMergedRegion(c1);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c1, sheet, wb);

            row5.createCell(2).setCellValue(getOdsType(dr));
            row5.getCell(2).setCellStyle(cellStyle);
            CellRangeAddress c10 = new CellRangeAddress(i, i + row, 2, 2);
            sheet.addMergedRegion(c10);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c10, sheet, wb);

            row5.createCell(3).setCellValue(dr.getFinishTag() != null && dr.getFinishTag() ? "已办结" : "未办结");
            row5.getCell(3).setCellStyle(cellStyle);
            CellRangeAddress c2 = new CellRangeAddress(i, i + row, 3, 3);
            sheet.addMergedRegion(c2);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c2, sheet, wb);
            row5.createCell(4).setCellValue(dr.getTitle());
            row5.getCell(4).setCellStyle(cellStyle);
            CellRangeAddress c3 = new CellRangeAddress(i, i + row, 4, 4);
            sheet.addMergedRegion(c3);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c3, sheet, wb);
            row5.createCell(5).setCellValue(dr.getDeptName());
            row5.getCell(5).setCellStyle(cellStyle);
            CellRangeAddress c4 = new CellRangeAddress(i, i + row, 5, 5);
            sheet.addMergedRegion(c4);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c4, sheet, wb);
            row5.createCell(6).setCellValue(dr.getCreator() == null ? "" : dr.getCreator().getUserName());
            row5.getCell(6).setCellStyle(cellStyle);
            CellRangeAddress c5 = new CellRangeAddress(i, i + row, 6, 6);
            sheet.addMergedRegion(c5);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c5, sheet, wb);
            row5.createCell(7).setCellValue(dr.getSendTime() == null ? "" : sdf1.format(dr.getSendTime()));
            row5.getCell(7).setCellStyle(cellStyle);
            CellRangeAddress c6 = new CellRangeAddress(i, i + row, 7, 7);
            sheet.addMergedRegion(c6);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c6, sheet, wb);
            row5.createCell(8).setCellValue(dr.getLimitDate() == null ? "" : sdf1.format(dr.getLimitDate()));
            row5.getCell(8).setCellStyle(cellStyle);
            CellRangeAddress c7 = new CellRangeAddress(i, i + row, 8, 8);
            sheet.addMergedRegion(c7);
            setBorderForMergeCell(HSSFCellStyle.BORDER_THIN, c7, sheet, wb);
            i = i + row + 1;
            index++;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);

        sheet.autoSizeColumn((short) 3); //调整第一列宽度
        sheet.autoSizeColumn((short) 5); //调整第一列宽度
        sheet.autoSizeColumn((short) 6); //调整第一列宽度
        sheet.autoSizeColumn((short) 10); //调整第一列宽度
        InputFile inputFile = new InputFile(new Inputable.ByteInput(out.toByteArray()), "公文办理信息一览表.xls");
        return inputFile;
    }

    @Override
    public void initEntity(DispatchRecord entity) throws Exception
    {
        entity.setSendTime(new java.util.Date());
        entity.setCreatorId(userOnlineInfo.getUserId());
        entity.setCreator(userOnlineInfo.getUserEntity());
        entity.setCreatorName(userOnlineInfo.getUserName());
        entity.setDeptName(userOnlineInfo.getDept().getAllName(1));
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        entity.setLimitDate(new java.util.Date(cal.getTime().getTime() - 60 * 1000));
        entity.setFinishTag(false);
        super.initEntity(entity);
    }

    public String getWarn(DispatchRecord dr)
    {
        if(dr.getRedWarnTag())
        {
            return "红牌";
        }
        else
        {
            if(dr.getFinishTag() == null || !dr.getFinishTag())
            {
                if(dr.getWarningDate() == null || dr.getWarningDate().after(new java.util.Date()))
                {
                    return "";
                }
                else
                {
                    return "黄牌";
                }
            }
            else
            {
                return "";
            }
        }
    }

    public String getOdsType(DispatchRecord dr)
    {
        if(dr.getSendId() == null) return "纸质公文";
        else return "OA发文";
    }

    public void setBorderForMergeCell(int i, CellRangeAddress cellRangeTitle, HSSFSheet sheet, HSSFWorkbook workBook)
    {
        RegionUtil.setBorderBottom(i, cellRangeTitle, sheet, workBook);
        RegionUtil.setBorderLeft(i, cellRangeTitle, sheet, workBook);
        RegionUtil.setBorderRight(i, cellRangeTitle, sheet, workBook);
        RegionUtil.setBorderTop(i, cellRangeTitle, sheet, workBook);
    }

    public String getOverTime(java.util.Date date1, java.util.Date date2)
    {
        Long overTime = date1.getTime() - date2.getTime();
        if(overTime > 24 * 60 * 60 * 1000)
        {
            return "超时" + overTime / (24 * 60 * 60 * 1000) + "天";
        }
        else
        {
            return "超时" + overTime / (60 * 60 * 1000) + "小时";
        }
    }

    @Service(method = HttpMethod.all)
    public Boolean checkDispatchNumber(String number)
    {
        Integer count = dao.checkSendNumber(number, getEntity().getRecordId());
        return count == 0 ? true : false;
    }
}
