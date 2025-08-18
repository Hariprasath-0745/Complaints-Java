package com.example.demo.Entity;

import org.antlr.v4.runtime.misc.NotNull;

public class ComplaintTemplate extends AuditEntity {

    @NotNull
    @Size(max = 20)
    private String templateName = "";

    @NotNull
    @Size(max = 8000)
    private String templateText = "";

    @NotNull
    private Long deptId;

    @NotNull
    private Integer siteId;

    // getters and setters
}

