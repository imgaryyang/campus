package com.gzzm.portal.solicit;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.tag.EntityQueryTag;
import net.cyan.crud.OrderType;

/**
 * 查询民意征集回复的标签
 * Created by sjy on 2016/3/25.
 */
@Tag(name = "solicitReply")
public class SolicitReplyListTag extends EntityQueryTag<SolicitReply, Integer> {

  public SolicitReplyListTag() {
    addOrderBy("replyTime", OrderType.desc);
  }

  /**
   * 民意征集Id
   */
  private Integer solicitId;

  @Override
  protected String getQueryString() throws Exception {
    return "select t from SolicitReply t where t.solicitId=:solicitId and t.state=1";
  }
  public Integer getSolicitId() {
    return solicitId;
  }

  public void setSolicitId(Integer solicitId) {
    this.solicitId = solicitId;
  }
}
