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

    public String getDurationDate() { return durationDate; }
    public void setDurationDate(String durationDate) { this.durationDate = durationDate; }
    
    public int getEyePartId() { return eyePartId; }
    public void setEyePartId(int eyePartId) { this.eyePartId = eyePartId; }
    
    public String getFreeText() { return freeText; }
    public void setFreeText(String freeText) { this.freeText = freeText; }
    
    public int getTemplateId() { return templateId; }
    public void setTemplateId(int templateId) { this.templateId = templateId; }
    
    public String getTemplateText() { return templateText; }
    public void setTemplateText(String templateText) { this.templateText = templateText; }
    
    public long getCreatedDeptId() { return createdDeptId; }
    public void setCreatedDeptId(long createdDeptId) { this.createdDeptId = createdDeptId; }
    
    public long getUpdatedDeptId() { return updatedDeptId; }
    public void setUpdatedDeptId(long updatedDeptId) { this.updatedDeptId = updatedDeptId; }
    
    public boolean isEvent() { return isEvent; }
    public void setEvent(boolean event) { isEvent = event; }
    
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}

