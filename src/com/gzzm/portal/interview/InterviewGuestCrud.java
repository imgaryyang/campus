package com.gzzm.portal.interview;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CButton;
import net.cyan.crud.view.components.CImage;

@Service(url={"/portal/interview/InterviewGuestCrud"})
public class InterviewGuestCrud
  extends BaseNormalCrud<InterviewGuest, Integer>
{
  @Like
  private String guestName;
  private boolean deletePhoto;
  private static final String blankImg = "/platform/commons/images/blank.gif";
  
  public boolean isDeletePhoto()
  {
    return this.deletePhoto;
  }
  
  public void setDeletePhoto(boolean deletePhoto)
  {
    this.deletePhoto = deletePhoto;
  }
  
  public String getBlankImg()
  {
    return "/platform/commons/images/blank.gif";
  }
  
  public String getGuestName()
  {
    return this.guestName;
  }
  
  public void setGuestName(String guestName)
  {
    this.guestName = guestName;
  }
  
  public String getOrderField()
  {
    return "orderId";
  }
  
  @Service(url={"/portal/interview/InterviewGuestCrud/photo/{$0}"})
  public byte[] getPhoto(Integer guestId)
    throws Exception
  {
    return ((InterviewGuest)getEntity(guestId)).getPhoto();
  }
  
  protected Object createListView()
    throws Exception
  {
    PageTableView view = new PageTableView();
    
    view.addColumn("姓名", "guestName");
    view.addColumn("用户类别", "guestType");
    view.addComponent("姓名", "guestName");
    view.defaultInit();
    view.addButton(Buttons.sort());
    
    return view;
  }
  
  protected Object createShowView()
    throws Exception
  {
    SimpleDialogView view = new SimpleDialogView();
    
    view.addComponent("姓名", "guestName");
    view.addComponent("简介", "introduction");
    view.addComponent("用户类型", "guestType");
    view.addComponent("图片", "photo");
    view.addComponent("", new CButton("清除图片", "clearPic()"));
    byte[] photo = ((InterviewGuest)getEntity()).getPhoto();
    view.addComponent("预览", ((CImage)new CImage("/portal/interview/InterviewGuestCrud/photo/" + ((InterviewGuest)getEntity()).getGuestId()).setProperty("id", "photoView")).setHeight(Integer.valueOf(200)));
    


    view.importJs("/portal/interview/interviewGuest.js");
    view.addDefaultButtons();
    
    return view;
  }
  
  public boolean beforeUpdate()
    throws Exception
  {
    InterviewGuest guest = (InterviewGuest)getEntity();
    if ((guest.getPhoto() == null) && (this.deletePhoto)) {
      guest.setPhoto(Null.ByteArray);
    }
    return true;
  }
}
