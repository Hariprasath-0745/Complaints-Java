package com.example.demo.ViewModel;

import java.util.List;

public class ComplaintSummary {

    private long peid;
    private String informant;
    private String visitPurpose;
    private List<String> complaints;

    // getters and setters
    public long getPeid() { return peid; }
    public void setPeid(long peid) { this.peid = peid; }
    
    public String getInformant() { return informant; }
    public void setInformant(String informant) { this.informant = informant; }
    
    public String getVisitPurpose() { return visitPurpose; }
    public void setVisitPurpose(String visitPurpose) { this.visitPurpose = visitPurpose; }
    
    public List<String> getComplaints() { return complaints; }
    public void setComplaints(List<String> complaints) { this.complaints = complaints; }
}
