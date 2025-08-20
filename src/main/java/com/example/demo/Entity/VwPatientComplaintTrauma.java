package com.example.demo.Entity;

public class VwPatientComplaintTrauma extends PatientComplaintTraumaBase {

    private VwPatientComplaint patientComplaint;

    // getters and setters
    public VwPatientComplaint getPatientComplaint() { return patientComplaint; }
    public void setPatientComplaint(VwPatientComplaint patientComplaint) { this.patientComplaint = patientComplaint; }
    
    public long getPatientComplaintId() { 
        return patientComplaint != null ? patientComplaint.getId() : 0L; 
    }
}