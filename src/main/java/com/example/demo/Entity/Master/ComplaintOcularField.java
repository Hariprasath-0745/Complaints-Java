package com.example.demo.Entity.Master;



import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.Entity.AuditEntity2;

@Entity
@Table(name = "complaint_ocular_field")
public class ComplaintOcularField extends AuditEntity2 {
    
    @Column(name = "field_name", nullable = false)
    private String fieldName = "";
    
    @Column(name = "field_code", nullable = false)
    private String fieldCode = "";
    
    @Column(name = "sequence")
    private int sequence;
    
    @OneToMany(mappedBy = "complaintOcularField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComplaintOcularFieldItem> complaintOcularFieldItems = new ArrayList<>();

    // Getters and setters
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getFieldCode() { return fieldCode; }
    public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }
    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }
    public List<ComplaintOcularFieldItem> getComplaintOcularFieldItems() { return complaintOcularFieldItems; }
    public void setComplaintOcularFieldItems(List<ComplaintOcularFieldItem> complaintOcularFieldItems) { 
        this.complaintOcularFieldItems = complaintOcularFieldItems; 
    }
}