package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_log_details") 
public class UserLogDetail {

    private Long id;
    private Integer createdBy;
    private OffsetDateTime createdAt;
    private String createdIp = "";
    private Long createdDeptId;
    private Integer updatedBy;
    private OffsetDateTime updatedAt;
    private String updatedIp;
    private Long updatedDeptId;
    private Integer createdDeptIdV2;
    private Integer updatedDeptIdV2;
    private OffsetDateTime deletedAt;
    private Integer deletedBy;
    private Integer deletedDeptIdV2;

    // getters and setters
}

