package com.example.demo.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

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

}

