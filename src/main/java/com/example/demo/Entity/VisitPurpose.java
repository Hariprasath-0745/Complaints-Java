package com.example.demo.Entity;

import org.antlr.v4.runtime.misc.NotNull;

public class VisitPurpose extends AuditEntity {

    @NotNull
    @Size(max = 100)
    private String visitPurposeText = "";

    @NotNull
    private Integer sequence;

    // getters and setters
}

