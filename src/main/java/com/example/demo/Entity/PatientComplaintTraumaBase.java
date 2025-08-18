package com.example.demo.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

public class PatientComplaintTraumaBase extends BaseEntity {

     @NotNull
    private Long patientComplaintId;

    @Size(max = 8000)
    private String natureOfTrauma;

    private LocalDate traumaDate;

    private LocalTime traumaTime;

    @Size(max = 8000)
    private String placeOfTrauma;

    private boolean isPriorTreatment;

    @Size(max = 8000)
    private String priorTreatmentDetail;

    @Size(max = 8000)
    private String traumaRemarks;

    @Size(max = 255)
    private String attenderDetail;

    private String identificationMarks;

    private boolean isMlcCase;

    @NotNull
    private UUID patientComplaintUniqueId;

    // getters and setters
}
