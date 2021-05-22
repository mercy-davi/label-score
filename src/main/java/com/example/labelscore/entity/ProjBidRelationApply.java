package com.example.labelscore.entity;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description TODO
 * @Date 2021/4/11 19:20
 * @Created by hdw
 */
@Entity
@Table(name = "fp_proj_bid_relation_apply")
@Immutable
public class ProjBidRelationApply implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物理主键
     */
    @Id
    @Column(name = "id_proj_bid_relation_apply", insertable = false, nullable = false)
    private String idProjBidRelationApply;

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
     * 关联类型
     */
    @Column(name = "relation_type")
    private String relationType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(updatable = false, insertable = false, name = "rel_bid_id", referencedColumnName = "id_fp_bid_detail_apply")
    @NotFound(action = NotFoundAction.IGNORE)
    private BidDetailApply bidDetailApply;

    @JoinColumn(referencedColumnName = "id_fp_proj_apply", name = "rel_id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private ProjApply projApply;

    public String getIdProjBidRelationApply() {
        return idProjBidRelationApply;
    }

    public void setIdProjBidRelationApply(String idProjBidRelationApply) {
        this.idProjBidRelationApply = idProjBidRelationApply;
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

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public BidDetailApply getBidDetailApply() {
        return bidDetailApply;
    }

    public void setBidDetailApply(BidDetailApply bidDetailApply) {
        this.bidDetailApply = bidDetailApply;
    }

    public ProjApply getProjApply() {
        return projApply;
    }

    public void setProjApply(ProjApply projApply) {
        this.projApply = projApply;
    }
}
