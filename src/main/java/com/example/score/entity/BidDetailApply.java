package com.example.score.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Classname BidDetailApply
 * @Date 2021/5/20
 * @Author hdw
 */
@Entity
@Table(name = "fp_bid_detail_apply")
@Immutable
public class BidDetailApply implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物理主键
     */
    @Id
    @Column(name = "id_fp_bid_detail_apply", insertable = false, updatable = false)
    private String idFpBidDetailApply;

    /**
     * 关联申请号
     */
    @Column(name = "rel_id")
    private String relId;

    /**
     * 关联流程主键
     */
    @Column(name = "task_id")
    private String taskId;

    /**
     * 项目性质，码值：BID_PROJ_NATURE_1
     */
    @Column(name = "proj_nature")
    private String projNature;

    /**
     * 标的项目类型，码值：BID_TYPE_1
     */
    @Column(name = "bid_type")
    private String bidType;

    /**
     * 总投资金额
     */
    @Column(name = "total_investment_amount")
    private BigDecimal totalInvestmentAmount;

    /**
     * 其他金融机构投资金额
     */
    @Column(name = "other_organ_funder_invest")
    private BigDecimal otherOrganFunderInvest;

    /**
     * 资本金比例
     */
    @Column(name = "capital_scale")
    private BigDecimal capitalScale;

    public String getIdFpBidDetailApply() {
        return idFpBidDetailApply;
    }

    public void setIdFpBidDetailApply(String idFpBidDetailApply) {
        this.idFpBidDetailApply = idFpBidDetailApply;
    }

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProjNature() {
        return projNature;
    }

    public void setProjNature(String projNature) {
        this.projNature = projNature;
    }

    public String getBidType() {
        return bidType;
    }

    public void setBidType(String bidType) {
        this.bidType = bidType;
    }

    public BigDecimal getTotalInvestmentAmount() {
        return totalInvestmentAmount;
    }

    public void setTotalInvestmentAmount(BigDecimal totalInvestmentAmount) {
        this.totalInvestmentAmount = totalInvestmentAmount;
    }

    public BigDecimal getOtherOrganFunderInvest() {
        return otherOrganFunderInvest;
    }

    public void setOtherOrganFunderInvest(BigDecimal otherOrganFunderInvest) {
        this.otherOrganFunderInvest = otherOrganFunderInvest;
    }

    public BigDecimal getCapitalScale() {
        return capitalScale;
    }

    public void setCapitalScale(BigDecimal capitalScale) {
        this.capitalScale = capitalScale;
    }
}
