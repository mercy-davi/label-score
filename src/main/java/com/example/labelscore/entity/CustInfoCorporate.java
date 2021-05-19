package com.example.labelscore.entity;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description 企业客户信息主表
 * @Date 2021/4/11 19:11
 * @Created by hdw
 */
@Entity
@Table(name = "kyp_cust_info_corporate")
@Immutable
@Where(clause = "status = '1' and new_flag = '1' and data_status in ('4','5') and rownum = 1")
public class CustInfoCorporate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 客户信息主键ID
     */
    @Id
    @Column(insertable = false, name = "kyp_serial_id", nullable = false)
    private String kypSerialId;

    /**
     * 客户编码
     */
    @Column(name = "cust_no", nullable = false)
    private String cust_no;

    /**
     * 企业性质
     */
    @Column(name = "company_property")
    private String companyProperty;

    /**
     * 数据状态（1：新建，2：待审核，3：被退回，4：审核通过，5：已下发）
     */
    @Column(name = "data_status")
    private String dataStatus;

    /**
     * 有效标识
     */
    @Column(name = "status")
    private String status = "1";

    /**
     * 显示标记
     */
    @Column(name = "new_flag")
    private String newFlag;

    public String getKypSerialId() {
        return kypSerialId;
    }

    public void setKypSerialId(String kypSerialId) {
        this.kypSerialId = kypSerialId;
    }

    public String getCust_no() {
        return cust_no;
    }

    public void setCust_no(String cust_no) {
        this.cust_no = cust_no;
    }

    public String getCompanyProperty() {
        return companyProperty;
    }

    public void setCompanyProperty(String companyProperty) {
        this.companyProperty = companyProperty;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNewFlag() {
        return newFlag;
    }

    public void setNewFlag(String newFlag) {
        this.newFlag = newFlag;
    }
}
