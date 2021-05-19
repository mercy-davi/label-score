package com.example.labelscore.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description 企业客户财报详情
 * @Date 2021/4/11 20:13
 * @Created by hdw
 */
@Immutable
@Table(name = "kyp_cust_finance_detail")
@Entity
public class CustFinanceDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键序列
     */
    @Id
    @Column(insertable = false, name = "kyp_serial_id", nullable = false)
    private String kypSerialId;

    /**
     * 科目代码
     */
    @Column(name = "item_key")
    private String itemKey;

    /**
     * 科目值
     */
    @Column(name = "item_value")
    private String itemValue;

    /**
     * 科目类型
     */
    @Column(name = "account_type")
    private String accountType;

    /**
     * 状态
     */
    @Column(name = "status")
    private String status = "1";

    public String getKypSerialId() {
        return kypSerialId;
    }

    public void setKypSerialId(String kypSerialId) {
        this.kypSerialId = kypSerialId;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
