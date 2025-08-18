package com.example.demo.dto;

import java.util.List;

public class ComplaintMasterSet {
    private List<ComplaintMasterData> complaintMasterData;
    private List<VisitPurposeDto> visitPurposes;
    private List<ComplaintReadingDto> complaintReadings;
    private List<ComplaintSubcomplaintDto> subcomplaints;
    private List<ComplaintTemplateDto> complaintTemplates;

    public List<ComplaintMasterData> getComplaintMasterData() {
        return complaintMasterData;
    }

    public void setComplaintMasterData(List<ComplaintMasterData> complaintMasterData) {
        this.complaintMasterData = complaintMasterData;
    }

    public List<VisitPurposeDto> getVisitPurposes() {
        return visitPurposes;
    }

    public void setVisitPurposes(List<VisitPurposeDto> visitPurposes) {
        this.visitPurposes = visitPurposes;
    }

    public List<ComplaintReadingDto> getComplaintReadings() {
        return complaintReadings;
    }

    public void setComplaintReadings(List<ComplaintReadingDto> complaintReadings) {
        this.complaintReadings = complaintReadings;
    }

    public List<ComplaintSubcomplaintDto> getSubcomplaints() {
        return subcomplaints;
    }

    public void setSubcomplaints(List<ComplaintSubcomplaintDto> subcomplaints) {
        this.subcomplaints = subcomplaints;
    }

    public List<ComplaintTemplateDto> getComplaintTemplates() {
        return complaintTemplates;
    }

    public void setComplaintTemplates(List<ComplaintTemplateDto> complaintTemplates) {
        this.complaintTemplates = complaintTemplates;
    }
}

