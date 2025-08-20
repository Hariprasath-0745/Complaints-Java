package com.example.demo.dto;

import java.util.UUID;

public class ComplaintOcularFieldItemDto extends BaseDto2 {
    private UUID key;
    private String value = "";
    private UUID complaintOcularFieldId;
    private String fieldItemName = "";
    private String fieldItemDisplayName = "";
    private int sequence;

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UUID getComplaintOcularFieldId() {
        return complaintOcularFieldId;
    }

    public void setComplaintOcularFieldId(UUID complaintOcularFieldId) {
        this.complaintOcularFieldId = complaintOcularFieldId;
    }

    public String getFieldItemName() {
        return fieldItemName;
    }

    public void setFieldItemName(String fieldItemName) {
        this.fieldItemName = fieldItemName;
    }

    public String getFieldItemDisplayName() {
        return fieldItemDisplayName;
    }

    public void setFieldItemDisplayName(String fieldItemDisplayName) {
        this.fieldItemDisplayName = fieldItemDisplayName;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
