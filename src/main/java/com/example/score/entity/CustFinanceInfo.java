package com.example.score.entity;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/11 19:33
 * @Created by hdw
 */
@Table(name = "kyp_cust_finance_info")
@Entity
@Immutable
public class CustFinanceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键序列
     */
    @Id
    @Column(insertable = false, name = "kyp_serial_id", nullable = false)
    private String kypSerialId;

    /**
     * 财报编号
     */
    @Column(name = "report_no")
    private String reportNo;

    /**
     * 归属日期
     */
    @Column(name = "report_date")
    private Date reportDate;

    /**
     * 状态
     */
    @Column(name = "status")
    private String status = "1";

    /**
     * 财报单位
     */
    @Column(name = "caliber_6")
    private String caliber;

    @JoinColumn(name = "cust_no", referencedColumnName = "cust_no", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ViewTruCustomerInfo viewTruCustomerInfo;

    @JoinColumn(referencedColumnName = "report_no", name = "report_no", updatable = false, insertable = false)
    @OneToMany(fetch = FetchType.LAZY)
    @Where(clause = "account_type = '01' and status = '1' and IETM_KEY = (SELECT FDI.dict_code FROM VIEW_FP_DICT_INFO FDI\n" +
            "    where FDI.dict_type = 'ITEM_KEY'\n" +
            "    and FDI.dict_name = '总资产'\n" +
            "    and FDI.STATUS = '1')")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<CustFinanceDetail> totalAssets;

    @JoinColumn(referencedColumnName = "report_no", name = "report_no", updatable = false, insertable = false)
    @OneToMany(fetch = FetchType.LAZY)
    @Where(clause = "account_type = '01' and status = '1' and IETM_KEY = (SELECT FDI.dict_code FROM VIEW_FP_DICT_INFO FDI\n" +
            "    where FDI.dict_type = 'ITEM_KEY'\n" +
            "    and FDI.dict_name = '营业总收入'\n" +
            "    and FDI.STATUS = '1')")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<CustFinanceDetail> operatingIncome;

    public String getKypSerialId() {
        return kypSerialId;
    }

    public void setKypSerialId(String kypSerialId) {
        this.kypSerialId = kypSerialId;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCaliber() {
        return caliber;
    }

    public void setCaliber(String caliber) {
        this.caliber = caliber;
    }

    public ViewTruCustomerInfo getViewTruCustomerInfo() {
        return viewTruCustomerInfo;
    }

    public void setViewTruCustomerInfo(ViewTruCustomerInfo viewTruCustomerInfo) {
        this.viewTruCustomerInfo = viewTruCustomerInfo;
    }

    public List<CustFinanceDetail> getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(List<CustFinanceDetail> totalAssets) {
        this.totalAssets = totalAssets;
    }

    public List<CustFinanceDetail> getOperatingIncome() {
        return operatingIncome;
    }

    public void setOperatingIncome(List<CustFinanceDetail> operatingIncome) {
        this.operatingIncome = operatingIncome;
    }
}
