package com.example.score.entity;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description TODO
 * @Date 2021/4/11 19:27
 * @Created by hdw
 */
@Entity
@Immutable
@Table(name = "ecif_cust_info")
public class EcifCustomerRelationInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 客户编号
     */
    @Id
    @Column(insertable = false, name = "cust_no", nullable = false)
    private String custNo;

    /**
     * 集团客户编号
     */
    @Column(name = "grp_cust_no")
    private String grpCustNo;

    @OneToOne(mappedBy = "ecifCustomerRelationInfo", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private EcifCropInfo ecifCropInfo;

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getGrpCustNo() {
        return grpCustNo;
    }

    public void setGrpCustNo(String grpCustNo) {
        this.grpCustNo = grpCustNo;
    }

    public EcifCropInfo getEcifCropInfo() {
        return ecifCropInfo;
    }

    public void setEcifCropInfo(EcifCropInfo ecifCropInfo) {
        this.ecifCropInfo = ecifCropInfo;
    }
}
