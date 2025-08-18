package com.example.demo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "complaint_templates")
public class ComplaintTemplate extends AuditEntity {

    @NotNull
    @Size(max = 20)
    @Column(name = "template_name", length = 20, nullable = false)
    private String templateName = "";

    @NotNull
    @Size(max = 8000)
    @Column(name = "template_text", length = 8000, nullable = false)
    private String templateText = "";

    @NotNull
    @Column(name = "dept_id", nullable = false)
    private Long deptId;

    @NotNull
    @Column(name = "site_id", nullable = false)
    private Integer siteId;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Boolean getIsActive() {
        return super.getIsActive();
    }

    public void setIsActive(Boolean isActive) {
        super.setIsActive(isActive);
    }
}

