package com.example.demo.Entity;

import org.antlr.v4.runtime.misc.NotNull;

public class VisitPurpose extends AuditEntity {

    @NotNull
    @Size(max = 100)
    private String visitPurposeText = "";

    @NotNull
    private Integer sequence;

    private String key = "";
    private String value = "";
    private String icon = "";
    private Boolean isSelected = false;
    private Boolean isGrouped = false;

    public String getVisitPurposeText() {
        return visitPurposeText;
    }

    public void setVisitPurposeText(String visitPurposeText) {
        this.visitPurposeText = visitPurposeText;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsGrouped() {
        return isGrouped;
    }

    public void setIsGrouped(Boolean isGrouped) {
        this.isGrouped = isGrouped;
    }
}

