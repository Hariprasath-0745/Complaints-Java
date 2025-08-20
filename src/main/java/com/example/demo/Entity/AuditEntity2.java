package com.example.demo.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class AuditEntity2 extends BaseEntity2 {
    
    @Column(name = "is_deleted")
    private boolean isDeleted;
    
    @Column(name = "created_by")
    private int createdBy;
    
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    
    @Column(name = "created_ip", length = 30)
    private String createdIp = "";
    
    @Column(name = "created_dept_id")
    private int createdDeptId;
    
    @Column(name = "updated_by")
    private Integer updatedBy;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    @Column(name = "updated_ip", length = 30)
    private String updatedIp;
    
    @Column(name = "updated_dept_id")
    private Integer updatedDeptId;
    
    @Column(name = "deleted_by")
    private Integer deletedBy;
    
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
    
    @Column(name = "deleted_ip", length = 30)
    private String deletedIp;
    
    @Column(name = "deleted_dept_id")
    private Integer deletedDeptId;

    // Getters and setters
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedIp() { return createdIp; }
    public void setCreatedIp(String createdIp) { this.createdIp = createdIp; }
    public int getCreatedDeptId() { return createdDeptId; }
    public void setCreatedDeptId(int createdDeptId) { this.createdDeptId = createdDeptId; }
    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedIp() { return updatedIp; }
    public void setUpdatedIp(String updatedIp) { this.updatedIp = updatedIp; }
    public Integer getUpdatedDeptId() { return updatedDeptId; }
    public void setUpdatedDeptId(Integer updatedDeptId) { this.updatedDeptId = updatedDeptId; }
    public Integer getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Integer deletedBy) { this.deletedBy = deletedBy; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }
    public String getDeletedIp() { return deletedIp; }
    public void setDeletedIp(String deletedIp) { this.deletedIp = deletedIp; }
    public Integer getDeletedDeptId() { return deletedDeptId; }
    public void setDeletedDeptId(Integer deletedDeptId) { this.deletedDeptId = deletedDeptId; }
}