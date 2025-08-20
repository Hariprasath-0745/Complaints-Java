package com.example.demo.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Size;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

public class PatientComplaintBase extends AuditEntity {
    @NotNull
    private long peId;

    @Size(max = 30)
    private String uid;

    private long createdDeptId;
    private Long updatedDeptId;

    @NotNull
    private LocalDate enteredDate;

    private Long complaintId;
    private LocalDateTime durationDate;
    private int eyePartId;

    @Size(max = 8000)
    private String freeText = "";

    private int templateId;

    @Size(max = 8000)
    private String templateText = "";

    private boolean isNoHistory = false;

    private Integer visitPurpose;

    @Size(max = 50)
    private String informant = "";

    @Size(max = 100)
    private String informantOthers = "";

    @Size(max = 150)
    private String visitPurposeOthers = "";

    @Size(max = 50)
    private String duration = "";

    @NotNull
    private UUID uniqueId;

    // Getters and setters
    public long getPeId() { return peId; }
    public void setPeId(long peId) { this.peId = peId; }
    
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    
    public long getCreatedDeptId() { return createdDeptId; }
    public void setCreatedDeptId(long createdDeptId) { this.createdDeptId = createdDeptId; }
    
    public Long getUpdatedDeptId() { return updatedDeptId; }
    public void setUpdatedDeptId(Long updatedDeptId) { this.updatedDeptId = updatedDeptId; }
    
    public LocalDate getEnteredDate() { return enteredDate; }
    public void setEnteredDate(LocalDate enteredDate) { this.enteredDate = enteredDate; }
    
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    
    public LocalDateTime getDurationDate() { return durationDate; }
    public void setDurationDate(LocalDateTime durationDate) { this.durationDate = durationDate; }
    
    public int getEyePartId() { return eyePartId; }
    public void setEyePartId(int eyePartId) { this.eyePartId = eyePartId; }
    
    public String getFreeText() { return freeText; }
    public void setFreeText(String freeText) { this.freeText = freeText; }
    
    public int getTemplateId() { return templateId; }
    public void setTemplateId(int templateId) { this.templateId = templateId; }
    
    public String getTemplateText() { return templateText; }
    public void setTemplateText(String templateText) { this.templateText = templateText; }
    
    public boolean isNoHistory() { return isNoHistory; }
    public void setNoHistory(boolean noHistory) { isNoHistory = noHistory; }
    
    public Integer getVisitPurpose() { return visitPurpose; }
    public void setVisitPurpose(Integer visitPurpose) { this.visitPurpose = visitPurpose; }
    
    public String getInformant() { return informant; }
    public void setInformant(String informant) { this.informant = informant; }
    
    public String getInformantOthers() { return informantOthers; }
    public void setInformantOthers(String informantOthers) { this.informantOthers = informantOthers; }
    
    public String getVisitPurposeOthers() { return visitPurposeOthers; }
    public void setVisitPurposeOthers(String visitPurposeOthers) { this.visitPurposeOthers = visitPurposeOthers; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public UUID getUniqueId() { return uniqueId; }
    public void setUniqueId(UUID uniqueId) { this.uniqueId = uniqueId; }

}

