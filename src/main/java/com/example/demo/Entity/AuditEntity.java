package com.example.demo.Entity;
import java.time.OffsetDateTime;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

public abstract class AuditEntity extends BaseEntity {

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_by")
    private int createdBy;

    @NotNull
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Size(max = 30)
    @Column(name = "created_ip")
    private String createdIp = "";

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Size(max = 30)
    @Column(name = "updated_ip")
    private String updatedIp;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Size(max = 30)
    @Column(name = "deleted_ip")
    private String deletedIp;

}