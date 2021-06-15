package com.example.score.entity;

import com.example.score.core.RootModel;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * @Description TODO
 * @Date 2021/4/11 18:57
 * @Created by hdw
 */
public class AfterProjApply extends RootModel implements Serializable {
    private static final long serialVersionUID = 1L;

    protected AfterProjApply() {
        super("afterModel");
    }

    /**
     * 物理主键
     */
    @Id
    @Column(name = "id_fp_proj_apply", insertable = false, nullable = false)
    private String idFpProjApply;

    /**
     * 项目编码
     */
    @Column(name = "kyp_fp_no", nullable = false)
    private String kypFpNo;

    /**
     * 申请类型
     */
    @Column(name = "applyType", nullable = false)
    private String applyType;

    /**
     * 项目类型
     */
    @Column(name = "proj_type", nullable = false)
    private String projType;

    /**
     * 所属板块
     */
    @Column(name = "plate_category", nullable = false)
    private String plateCategory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projApply")
    @NotFound(action = NotFoundAction.IGNORE)
    @Where(clause = "del_flag = 1")
    @OrderBy(clause = "DATE_CREATED DESC")
    private List<GuarantorInfoApply> guarantorInfoApply;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projApply")
    @NotFound(action = NotFoundAction.IGNORE)
    @Where(clause = "del_flag = 1")
    @OrderBy(clause = "DATE_CREATED DESC")
    private List<GuarantyInfoApply> guarantyInfoApply;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "projApply")
    @NotFound(action = NotFoundAction.IGNORE)
    @Fetch(FetchMode.SUBSELECT) // 子查询
    @Where(clause = "del_flag = 1")
    @OrderBy(clause = "DATE_CREATED DESC")
    private List<ProjBidRelationApply> projBidRelationApply;

    public String getIdFpProjApply() {
        return idFpProjApply;
    }

    public void setIdFpProjApply(String idFpProjApply) {
        this.idFpProjApply = idFpProjApply;
    }

    public String getKypFpNo() {
        return kypFpNo;
    }

    public void setKypFpNo(String kypFpNo) {
        this.kypFpNo = kypFpNo;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getProjType() {
        return projType;
    }

    public void setProjType(String projType) {
        this.projType = projType;
    }

    public String getPlateCategory() {
        return plateCategory;
    }

    public void setPlateCategory(String plateCategory) {
        this.plateCategory = plateCategory;
    }

    public List<GuarantorInfoApply> getGuarantorInfoApply() {
        return guarantorInfoApply;
    }

    public void setGuarantorInfoApply(List<GuarantorInfoApply> guarantorInfoApply) {
        this.guarantorInfoApply = guarantorInfoApply;
    }

    public List<GuarantyInfoApply> getGuarantyInfoApply() {
        return guarantyInfoApply;
    }

    public void setGuarantyInfoApply(List<GuarantyInfoApply> guarantyInfoApply) {
        this.guarantyInfoApply = guarantyInfoApply;
    }

    public List<ProjBidRelationApply> getProjBidRelationApply() {
        return projBidRelationApply;
    }

    public void setProjBidRelationApply(List<ProjBidRelationApply> projBidRelationApply) {
        this.projBidRelationApply = projBidRelationApply;
    }
}
