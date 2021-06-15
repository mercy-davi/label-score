package com.example.score.entity;

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
 * @Description 财务指标表
 * @Date 2021/4/11 19:47
 * @Created by hdw
 */
@Immutable
@Entity
@Table(name = "ecif_ratio_sheet")
public class EcifRatioSheet implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物理主键
     */
    @Id
    @Column(insertable = false, name = "id_ecif_ratio_sheet", nullable = false)
    private String idEcifRatioSheet;

    /**
     * 客户编号
     */
    @Column(name = "cust_no")
    private String custNo;

    /**
     * 指标对应值码值
     */
    @Column(name = "code_value")
    private String codeValue;

    /**
     * 财务指标值
     */
    @Column(name = "proc_mc")
    private BigDecimal procMc;

    /**
     * 报告日期
     */
    @Column(name = "date_date")
    private String dateDate;

    /**
     * 单位
     */
    @Column(name = "mon_unit")
    private String monUnit;

    @JoinColumn(updatable = false, insertable = false, name = "cust_no", referencedColumnName = "cust_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private EcifCropInfo ecifCropInfo;

    public String getIdEcifRatioSheet() {
        return idEcifRatioSheet;
    }

    public void setIdEcifRatioSheet(String idEcifRatioSheet) {
        this.idEcifRatioSheet = idEcifRatioSheet;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    public BigDecimal getProcMc() {
        return procMc;
    }

    public void setProcMc(BigDecimal procMc) {
        this.procMc = procMc;
    }

    public String getDateDate() {
        return dateDate;
    }

    public void setDateDate(String dateDate) {
        this.dateDate = dateDate;
    }

    public String getMonUnit() {
        return monUnit;
    }

    public void setMonUnit(String monUnit) {
        this.monUnit = monUnit;
    }

    public EcifCropInfo getEcifCropInfo() {
        return ecifCropInfo;
    }

    public void setEcifCropInfo(EcifCropInfo ecifCropInfo) {
        this.ecifCropInfo = ecifCropInfo;
    }
}
