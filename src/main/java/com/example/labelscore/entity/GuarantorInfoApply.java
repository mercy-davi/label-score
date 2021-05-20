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
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description TODO
 * @Date 2021/4/11 19:14
 * @Created by hdw
 */
@Immutable
@Entity
@Table(name = "fp_guarantor_info_apply")
public class GuarantorInfoApply implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物理主键
     */
    @Id
    @Column(name = "id_fp_guarantor_info_apply", insertable = false, nullable = false)
    private String idFpGuarantorInfoApply;

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
     * 担保明细分类
     */
    @Column(name = "guaranty_classification")
    private String guarantyClassification;

    /**
     * 是否主要担保人
     */
    @Column(name = "is_guarantor_main")
    private String isGuarantorMain;

    /**
     * 担保比例
     */
    @Column(name = "guaranty_prop")
    private BigDecimal guarantyProp;

    @JoinColumn(referencedColumnName = "cust_no", name = "guarantor_id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private ViewTruCustomerInfo viewTruCustomerInfo;

    @JoinColumn(referencedColumnName = "cust_no", name = "guarantor_id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private CustInfoCorporate custInfoCorporate;

    @JoinColumn(referencedColumnName = "id_fp_proj_apply", name = "rel_id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ProjApply projApply;

    public String getIdFpGuarantorInfoApply() {
        return idFpGuarantorInfoApply;
    }

    public void setIdFpGuarantorInfoApply(String idFpGuarantorInfoApply) {
        this.idFpGuarantorInfoApply = idFpGuarantorInfoApply;
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

    public String getGuarantyClassification() {
        return guarantyClassification;
    }

    public void setGuarantyClassification(String guarantyClassification) {
        this.guarantyClassification = guarantyClassification;
    }

    public String getIsGuarantorMain() {
        return isGuarantorMain;
    }

    public void setIsGuarantorMain(String isGuarantorMain) {
        this.isGuarantorMain = isGuarantorMain;
    }

    public BigDecimal getGuarantyProp() {
        return guarantyProp;
    }

    public void setGuarantyProp(BigDecimal guarantyProp) {
        this.guarantyProp = guarantyProp;
    }

    public ViewTruCustomerInfo getViewTruCustomerInfo() {
        return viewTruCustomerInfo;
    }

    public void setViewTruCustomerInfo(ViewTruCustomerInfo viewTruCustomerInfo) {
        this.viewTruCustomerInfo = viewTruCustomerInfo;
    }

    public CustInfoCorporate getCustInfoCorporate() {
        return custInfoCorporate;
    }

    public void setCustInfoCorporate(CustInfoCorporate custInfoCorporate) {
        this.custInfoCorporate = custInfoCorporate;
    }

    public ProjApply getProjApply() {
        return projApply;
    }

    public void setProjApply(ProjApply projApply) {
        this.projApply = projApply;
    }
}
