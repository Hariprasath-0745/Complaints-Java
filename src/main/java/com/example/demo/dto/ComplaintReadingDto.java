package com.example.demo.dto;

public class ComplaintReadingDto extends BaseDto {

    private long complaintSubcomplaintId;
    private long resultId;
    private int sequence;

    public long getComplaintSubcomplaintId() {
        return complaintSubcomplaintId;
    }

    public void setComplaintSubcomplaintId(long complaintSubcomplaintId) {
        this.complaintSubcomplaintId = complaintSubcomplaintId;
    }

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}

