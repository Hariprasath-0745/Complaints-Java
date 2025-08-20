package com.example.demo.Entity.Master;

import java.util.UUID;

import com.example.demo.Entity.BaseEntity2;

import jakarta.persistence.*;

@Entity
@Table(name = "complaint_ocular_field_item")
public class ComplaintOcularFieldItem extends BaseEntity2 {
    
    @Column(name = "complaint_ocular_field_id", nullable = false)
    private UUID complaintOcularFieldId;
    
    @Column(name = "field_item_name", nullable = false)
    private String fieldItemName = "";
    
    @Column(name = "field_item_display_name", nullable = false)
    private String fieldItemDisplayName = "";
    
    @Column(name = "sequence")
    private int sequence;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_ocular_field_id", insertable = false, updatable = false)
    private ComplaintOcularField complaintOcularField;

    // Getters and setters
    public UUID getComplaintOcularFieldId() { return complaintOcularFieldId; }
    public void setComplaintOcularFieldId(UUID complaintOcularFieldId) { this.complaintOcularFieldId = complaintOcularFieldId; }
    public String getFieldItemName() { return fieldItemName; }
    public void setFieldItemName(String fieldItemName) { this.fieldItemName = fieldItemName; }
    public String getFieldItemDisplayName() { return fieldItemDisplayName; }
    public void setFieldItemDisplayName(String fieldItemDisplayName) { this.fieldItemDisplayName = fieldItemDisplayName; }
    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }
    public ComplaintOcularField getComplaintOcularField() { return complaintOcularField; }
    public void setComplaintOcularField(ComplaintOcularField complaintOcularField) { this.complaintOcularField = complaintOcularField; }
}