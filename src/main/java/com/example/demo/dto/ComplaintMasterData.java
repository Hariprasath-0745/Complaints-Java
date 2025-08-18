package com.example.demo.dto;

public class ComplaintMasterData {

    private long id;
    private String complaintText = "";
    private boolean isOcular;
    private boolean isEvent;
    private boolean isActive;
    private List<SubcomplaintData> subcomplaints;
    private List<Long> complaintAssociateId;
    private List<Long> departmentId;

    // getters and setters
}

