package com.example.demo.dto;

import java.util.List;

public class ComplaintSubcomplaintDto extends BaseDto {

    private long complaintId;
    private long subcomplaintId;
    private int sequence;
    private List<ComplaintReadingDto> complaintReadings;

    public long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(long complaintId) {
        this.complaintId = complaintId;
    }

    public long getSubcomplaintId() {
        return subcomplaintId;
    }

    public void setSubcomplaintId(long subcomplaintId) {
        this.subcomplaintId = subcomplaintId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<ComplaintReadingDto> getComplaintReadings() {
        return complaintReadings;
    }

    public void setComplaintReadings(List<ComplaintReadingDto> complaintReadings) {
        this.complaintReadings = complaintReadings;
    }
}