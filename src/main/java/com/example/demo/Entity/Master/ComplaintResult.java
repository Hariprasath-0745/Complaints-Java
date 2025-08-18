package com.example.demo.Entity.Master;

import org.antlr.v4.runtime.misc.NotNull;

import com.example.demo.Entity.AuditEntity;
import com.example.demo.Entity.Size;

public class ComplaintResult extends AuditEntity {

    @NotNull
    @Size(max = 100)
    private String resultText = "";

    // getters and setters
}

