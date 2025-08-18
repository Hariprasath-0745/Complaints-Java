package com.example.demo.Model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_log_details") 
public class UserLogDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedIp() {
        return createdIp;
    }

    public void setCreatedIp(String createdIp) {
        this.createdIp = createdIp;
    }

    public Long getCreatedDeptId() {
        return createdDeptId;
    }

    public void setCreatedDeptId(Long createdDeptId) {
        this.createdDeptId = createdDeptId;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedIp() {
        return updatedIp;
    }

    public void setUpdatedIp(String updatedIp) {
        this.updatedIp = updatedIp;
    }

    public Long getUpdatedDeptId() {
        return updatedDeptId;
    }

    public void setUpdatedDeptId(Long updatedDeptId) {
        this.updatedDeptId = updatedDeptId;
    }

    public Integer getCreatedDeptIdV2() {
        return createdDeptIdV2;
    }

    public void setCreatedDeptIdV2(Integer createdDeptIdV2) {
        this.createdDeptIdV2 = createdDeptIdV2;
    }

    public Integer getUpdatedDeptIdV2() {
        return updatedDeptIdV2;
    }

    public void setUpdatedDeptIdV2(Integer updatedDeptIdV2) {
        this.updatedDeptIdV2 = updatedDeptIdV2;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Integer getDeletedDeptIdV2() {
        return deletedDeptIdV2;
    }

    public void setDeletedDeptIdV2(Integer deletedDeptIdV2) {
        this.deletedDeptIdV2 = deletedDeptIdV2;
    }
}

