package com.example.labelscore.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 抵质押物申请信息表
 * @Date 2021/4/11 19:18
 * @Created by hdw
 */
@Entity
@Table(name = "fp_guaranty_info_apply")
@Immutable
public class GuarantyInfoApply implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物理主键
     */
    @Id
    @Column(name = "id_fp_guaranty_info_apply", insertable = false, nullable = false)
    private String idFpGuarantyInfoApply;

    /**
     * 关联流程主键
     */
    @Column(name = "task_id")
    private String taskId;

    /**
     * 对象类型
     */
    @Column(name = "object_type")
    private String objectType;

    /**
     * 抵质押类型
     */
    @Column(name = "guaranty_type")
    private String guarantyType;

    /**
     * 抵质押顺位
     */
    @Column(name = "arrangement_pledge")
    private String arrangementPledge;

    /**
     * 空档期期限
     */
    @Column(name = "gap_period_term")
    private BigDecimal gapPeriodTerm;

    /**
     * 空档期期限单位 0-自然日，1-工作日，2-月
     */
    @Column(name = "gap_period_term_unit")
    private String gapPeriodTermUnit;

    @JoinColumn(referencedColumnName = "id_fp_proj_apply", name = "rel_id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ProjApply projApply;

    public String getIdFpGuarantyInfoApply() {
        return idFpGuarantyInfoApply;
    }

    public void setIdFpGuarantyInfoApply(String idFpGuarantyInfoApply) {
        this.idFpGuarantyInfoApply = idFpGuarantyInfoApply;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getGuarantyType() {
        return guarantyType;
    }

    public void setGuarantyType(String guarantyType) {
        this.guarantyType = guarantyType;
    }

    public String getArrangementPledge() {
        return arrangementPledge;
    }

    public void setArrangementPledge(String arrangementPledge) {
        this.arrangementPledge = arrangementPledge;
    }

    public BigDecimal getGapPeriodTerm() {
        return gapPeriodTerm;
    }

    public void setGapPeriodTerm(BigDecimal gapPeriodTerm) {
        this.gapPeriodTerm = gapPeriodTerm;
    }

    public String getGapPeriodTermUnit() {
        return gapPeriodTermUnit;
    }

    public void setGapPeriodTermUnit(String gapPeriodTermUnit) {
        this.gapPeriodTermUnit = gapPeriodTermUnit;
    }

    public ProjApply getProjApply() {
        return projApply;
    }

    public void setProjApply(ProjApply projApply) {
        this.projApply = projApply;
    }
}
