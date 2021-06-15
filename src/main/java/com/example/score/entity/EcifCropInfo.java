package com.example.score.entity;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/11 19:39
 * @Created by hdw
 */
@Entity
@Immutable
@Table(name = "ecif_corp_info")
public class EcifCropInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 客户编号
     */
    @Id
    @Column(insertable = false, name = "cust_no", nullable = false)
    private String custNo;

    /**
     * 是否上市
     */
    @Column(name = "is_list")
    private String isList;

    @JoinColumn(updatable = false, insertable = false, name = "cust_no", referencedColumnName = "cust_no")
    @OneToOne(fetch = FetchType.LAZY)
    private EcifCustomerRelationInfo ecifCustomerRelationInfo;

    /**
     * 净负债率
     */
    @OneToMany(mappedBy = "ecifCropInfo", fetch = FetchType.LAZY)
    @Where(clause = "CODE_VALUE = '50'")
    @OrderBy(clause = "to_date(DATE_DATE,'yyyy/mm/dd hh24:mi:ss') DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<EcifRatioSheet> netDebtRatio;

    /**
     * 毛利率
     */
    @OneToMany(mappedBy = "ecifCropInfo", fetch = FetchType.LAZY)
    @Where(clause = "CODE_VALUE = '73'")
    @OrderBy(clause = "to_date(DATE_DATE,'yyyy/mm/dd hh24:mi:ss') DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<EcifRatioSheet> grossProfitMargin;

    /**
     * 净利率
     */
    @OneToMany(mappedBy = "ecifCropInfo", fetch = FetchType.LAZY)
    @Where(clause = "CODE_VALUE = '74'")
    @OrderBy(clause = "to_date(DATE_DATE,'yyyy/mm/dd hh24:mi:ss') DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<EcifRatioSheet> netProfitRatio;

    /**
     * 有息负债规模
     */
    @OneToMany(mappedBy = "ecifCropInfo", fetch = FetchType.LAZY)
    @Where(clause = "CODE_VALUE = '76'")
    @OrderBy(clause = "to_date(DATE_DATE,'yyyy/mm/dd hh24:mi:ss') DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<EcifRatioSheet> interestBearingLiabilities;

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getIsList() {
        return isList;
    }

    public void setIsList(String isList) {
        this.isList = isList;
    }

    public EcifCustomerRelationInfo getEcifCustomerRelationInfo() {
        return ecifCustomerRelationInfo;
    }

    public void setEcifCustomerRelationInfo(EcifCustomerRelationInfo ecifCustomerRelationInfo) {
        this.ecifCustomerRelationInfo = ecifCustomerRelationInfo;
    }

    public List<EcifRatioSheet> getNetDebtRatio() {
        return netDebtRatio;
    }

    public void setNetDebtRatio(List<EcifRatioSheet> netDebtRatio) {
        this.netDebtRatio = netDebtRatio;
    }

    public List<EcifRatioSheet> getGrossProfitMargin() {
        return grossProfitMargin;
    }

    public void setGrossProfitMargin(List<EcifRatioSheet> grossProfitMargin) {
        this.grossProfitMargin = grossProfitMargin;
    }

    public List<EcifRatioSheet> getNetProfitRatio() {
        return netProfitRatio;
    }

    public void setNetProfitRatio(List<EcifRatioSheet> netProfitRatio) {
        this.netProfitRatio = netProfitRatio;
    }

    public List<EcifRatioSheet> getInterestBearingLiabilities() {
        return interestBearingLiabilities;
    }

    public void setInterestBearingLiabilities(List<EcifRatioSheet> interestBearingLiabilities) {
        this.interestBearingLiabilities = interestBearingLiabilities;
    }
}
