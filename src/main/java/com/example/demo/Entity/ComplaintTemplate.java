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

