package com.example.labelscore.entity;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/11 19:10
 * @Created by hdw
 */
@Immutable
@Entity
@Table(name = "view_tru_customer_info")
public class ViewTruCustomerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "cust_name")
    private String custName;

    @Column(name = "type")
    private String type;

    @Id
    @JoinColumn(name = "cust_no", updatable = false, insertable = false, referencedColumnName = "grp_cust_no")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private EcifCustomerRelationInfo ecifCustomerRelationInfo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewTruCustomerInfo")
    @NotFound(action = NotFoundAction.IGNORE)
    @Where(clause = "status = '1'")
    @OrderBy(clause = "REPORT_DATE DESC")
    private List<CustFinanceInfo> custFinanceInfo;

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EcifCustomerRelationInfo getEcifCustomerRelationInfo() {
        return ecifCustomerRelationInfo;
    }

    public void setEcifCustomerRelationInfo(EcifCustomerRelationInfo ecifCustomerRelationInfo) {
        this.ecifCustomerRelationInfo = ecifCustomerRelationInfo;
    }

    public List<CustFinanceInfo> getCustFinanceInfo() {
        return custFinanceInfo;
    }

    public void setCustFinanceInfo(List<CustFinanceInfo> custFinanceInfo) {
        this.custFinanceInfo = custFinanceInfo;
    }
}
