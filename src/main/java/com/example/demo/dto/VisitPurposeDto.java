package com.example.demo.dto;

public class VisitPurposeDto extends BaseDto {

    private String key = "";
    private String value = "";
    private boolean isSelected = false;
    private boolean isGrouped = false;
    private int sequence;
    private String icon = "";

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isGrouped() {
        return isGrouped;
    }

    public void setIsGrouped(boolean isGrouped) {
        this.isGrouped = isGrouped;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

