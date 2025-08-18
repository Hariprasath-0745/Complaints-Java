package com.example.demo.Entity;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

public class PatientComplaintReadingBase extends BaseEntity {

    @NotNull
    private Long patientComplaintId;

    private Long complaintSubcomplaintId;

    @NotNull
    private Long complaintReadingId;

    @NotNull
    private UUID patientComplaintUniqueId;

    // getters and setters
}

