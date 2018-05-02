package com.gzzm.portal.solicit;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.commons.PortalUtils;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.captcha.ArachneCaptchaSupport;
import net.cyan.nest.annotation.Inject;
import net.cyan.nest.integration.RemoteAddr;

import java.util.Date;

/**
 * 民意征集相关服务
 * Created by sjy on 2016/3/22.
 */
@Service()
public class WriteSolicitReply {

  private SolicitReply reply;

  @RemoteAddr
  private String ip;

  @Inject
  private UserOnlineInfo userOnlineInfo;

  @Inject
  private SolicitDao dao;

  /**
   * 保存一条民意征集回复
   *
   * @return
   * @throws Exception
   */
  @Service(url = "/portal/solicit/saveSolicitReply", method = HttpMethod.post, validateType = ValidateType.auto)
  public Integer saveSolicitReply() throws Exception {
    ArachneCaptchaSupport.check();
    if (reply != null) {
      reply.setIp(PortalUtils.getIp(RequestContext.getContext().getRequest()));
      if (userOnlineInfo != null) {
        reply.setUserId(userOnlineInfo.getUserId());
        //未填写真实名称，但是已登录，设置真实名称为登录用户名
        if (reply.getRealName() == null || "".equals(reply.getRealName()))
          reply.setRealName(userOnlineInfo.getUserName());
      }
      reply.setState(SolicitReplyState.NOAUDITED);
      reply.setReplyTime(new Date());
      dao.save(reply);
      return 1;
    }
    return -1;
  }

  public SolicitReply getReply() {
    return reply;
  }

  public void setReply(SolicitReply reply) {
    this.reply = reply;
  }
}
