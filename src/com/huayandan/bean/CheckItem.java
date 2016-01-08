package com.huayandan.bean;


/**
 * 化验单单项
 *
 * @author 温韬 2013-9-2
 */
public class CheckItem {
    /**
     * 记录编号:主键
     */
    private Integer id;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目简称
     */
    private String shortName;

    /**
     * 英文名称
     */
    private String engName;

    /**
     * 英文简称
     */
    private String engShortName;

    /**
     * 所属化验单id
     */
    private Integer assayId;

    /**
     * 所属化验单名称
     */
    private String SLB_NM;

    /**
     * 医学意义
     *
     * @return
     */
    private String significance;

    /**
     * 测定值
     */
    private Float ceding;
    /**
     * 高界
     */
    private Float highUnit;
    /**
     * 低界限
     */
    private Float lowUnit;
    /**
     * 最终是高-1、正常-0、偏低-3
     */
    private String resultFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public String getEngShortName() {
        return engShortName;
    }

    public void setEngShortName(String engShortName) {
        this.engShortName = engShortName;
    }

    public Integer getAssayId() {
        return assayId;
    }

    public void setAssayId(Integer assayId) {
        this.assayId = assayId;
    }

    public String getSLB_NM() {
        return SLB_NM;
    }

    public void setSLB_NM(String SLB_NM) {
        this.SLB_NM = SLB_NM;
    }

    public String getSignificance() {
        return significance;
    }

    public void setSignificance(String significance) {
        this.significance = significance;
    }

    public Float getCeding() {
        return ceding;
    }

    public void setCeding(Float ceding) {
        this.ceding = ceding;
    }

    public Float getHighUnit() {
        return highUnit;
    }

    public void setHighUnit(Float highUnit) {
        this.highUnit = highUnit;
    }

    public Float getLowUnit() {
        return lowUnit;
    }

    public void setLowUnit(Float lowUnit) {
        this.lowUnit = lowUnit;
    }

    public String getResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(String resultFlag) {
        this.resultFlag = resultFlag;
    }
}
