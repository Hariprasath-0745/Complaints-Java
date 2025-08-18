package com.example.demo.ViewModel;

import java.util.List;

public class Complaint {

    private String complaintInformant;
    private String complaintInformantOther;
    private List<ComplaintDetail> complaintDetails;

    public String getComplaintInformant() {
        return complaintInformant;
    }

    public void setComplaintInformant(String complaintInformant) {
        this.complaintInformant = complaintInformant;
    }

    public String getComplaintInformantOther() {
        return complaintInformantOther;
    }

    public void setComplaintInformantOther(String complaintInformantOther) {
        this.complaintInformantOther = complaintInformantOther;
    }

    public List<ComplaintDetail> getComplaintDetails() {
        return complaintDetails;
    }

    public void setComplaintDetails(List<ComplaintDetail> complaintDetails) {
        this.complaintDetails = complaintDetails;
    }
}

