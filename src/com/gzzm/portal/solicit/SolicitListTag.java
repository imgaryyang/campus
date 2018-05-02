package com.gzzm.portal.solicit;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.tag.EntityQueryTag;
import net.cyan.crud.OrderType;

import java.util.Date;

/**
 * 查询民意征集的标签
 * Created by sjy on 2016/3/25.
 */
@Tag(name = "solicit")
public class SolicitListTag extends EntityQueryTag<Solicit, Integer> {

  public SolicitListTag() {
  }

  private String order="publishTime desc";

  /**
   * 民意征集Id
   */
  private Integer solicitId;

  private Integer stationId;

  private SolicitState state;

  private Boolean finish;

  //是否查询热门征集，热门就是回复最多的
  private Boolean hotSolicit;

  @Override
  protected String getQueryString() throws Exception {
    String oql = "select t from Solicit t where t.stationId=:stationId and t.state=1 ";
    if (solicitId != null) {
      oql += " and t.solicitId=?solicitId " ;
    }
    if (finish != null) {
      {
        if (finish)
          oql += " and t.endDate < sysdate()";
        else oql += " and ( t.endDate > sysdate() or t.endDate is null) ";
      }
    }
    oql+=" order by "+order;
    return oql;
  }

  public Boolean getFinish() {
    return finish;
  }

  public void setFinish(Boolean finish) {
    this.finish = finish;
  }

  public Boolean getHotSolicit() {
    return hotSolicit;
  }

  public void setHotSolicit(Boolean hotSolicit) {
    this.hotSolicit = hotSolicit;
  }

  public Integer getSolicitId() {
    return solicitId;
  }

  public void setSolicitId(Integer solicitId) {
    this.solicitId = solicitId;
  }

  public Integer getStationId() {
    return stationId;
  }

  public void setStationId(Integer stationId) {
    this.stationId = stationId;
  }

  public SolicitState getState() {
    return state;
  }

  public void setState(SolicitState state) {
    this.state = state;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }
}
