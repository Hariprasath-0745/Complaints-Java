package com.example.demo.Entity;

import java.util.List;

public class VwPatientComplaint extends PatientComplaintBase {

    private List<PatientComplaintReading> patientComplaintReadings;
    private List<PatientComplaintTrauma> patientComplaintTrauma;
    private Long complaintId;

    public List<PatientComplaintReading> getPatientComplaintReadings() {
        return patientComplaintReadings;
    }

    public void setPatientComplaintReadings(List<PatientComplaintReading> patientComplaintReadings) {
        this.patientComplaintReadings = patientComplaintReadings;
    }

    public List<PatientComplaintTrauma> getPatientComplaintTrauma() {
        return patientComplaintTrauma;
    }

    public void setPatientComplaintTrauma(List<PatientComplaintTrauma> patientComplaintTrauma) {
        this.patientComplaintTrauma = patientComplaintTrauma;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }
}

