package com.example.demo.ViewModel;

import java.util.List;

public class ComplaintDetail {

    private Long complaintId;
    private String complaint;
    private String durationDate;
    private int eyePartId;
    private String freeText = "";
    private int templateId;
    private String templateText = "";
    private boolean isNoHistory = false;
    private long createdDeptId;
    private long updatedDeptId;
    private boolean isEvent;
    private String templateName = "";
    private String duration = "";
    private List<SubComplaint> subComplaints;
    private List<ComplaintTrauma> complaintTrauma;

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public boolean isNoHistory() {
        return isNoHistory;
    }

    public void setNoHistory(boolean noHistory) {
        isNoHistory = noHistory;
    }

    public List<ComplaintTrauma> getComplaintTrauma() {
        return complaintTrauma;
    }

    public void setComplaintTrauma(List<ComplaintTrauma> complaintTrauma) {
        this.complaintTrauma = complaintTrauma;
    }

    public List<SubComplaint> getSubComplaints() {
        return subComplaints;
    }

    public void setSubComplaints(List<SubComplaint> subComplaints) {
        this.subComplaints = subComplaints;
    }
}

