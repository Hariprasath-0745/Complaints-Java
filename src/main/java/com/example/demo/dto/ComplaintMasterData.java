package com.example.demo.dto;

import java.util.List;

public class ComplaintMasterData {

    private long id;
    private String complaintText = "";
    private boolean isOcular;
    private boolean isEvent;
    private boolean isActive;
    private List<SubcomplaintData> subcomplaints;
    private List<Long> complaintAssociateId;
    private List<Long> departmentId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComplaintText() {
        return complaintText;
    }

    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }

    public boolean isOcular() {
        return isOcular;
    }

    public void setOcular(boolean isOcular) {
        this.isOcular = isOcular;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean isEvent) {
        this.isEvent = isEvent;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<SubcomplaintData> getSubcomplaints() {
        return subcomplaints;
    }

    public void setSubcomplaints(List<SubcomplaintData> subcomplaints) {
        this.subcomplaints = subcomplaints;
    }

    public List<Long> getComplaintAssociateId() {
        return complaintAssociateId;
    }

    public void setComplaintAssociateId(List<Long> complaintAssociateId) {
        this.complaintAssociateId = complaintAssociateId;
    }

    public List<Long> getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(List<Long> departmentId) {
        this.departmentId = departmentId;
    }
}

