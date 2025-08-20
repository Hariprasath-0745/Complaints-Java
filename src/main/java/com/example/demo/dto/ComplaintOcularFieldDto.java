package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

public class ComplaintOcularFieldDto extends BaseDto2 {
    private String fieldName = "";
    private String fieldCode = "";
    private int sequence;
    private List<ComplaintOcularFieldItemDto> complaintOcularFieldItems;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<ComplaintOcularFieldItemDto> getComplaintOcularFieldItems() {
        return complaintOcularFieldItems;
    }

    public void setComplaintOcularFieldItems(List<ComplaintOcularFieldItemDto> complaintOcularFieldItems) {
        this.complaintOcularFieldItems = complaintOcularFieldItems;
    }
}