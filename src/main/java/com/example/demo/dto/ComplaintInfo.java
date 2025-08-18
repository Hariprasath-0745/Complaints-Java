package com.example.demo.dto;

public class ComplaintInfo {
    private boolean isOcularTrauma;
    private boolean isRedness;
    private String error;

    public boolean getIsOcularTrauma() {
        return isOcularTrauma;
    }

    public void setIsOcularTrauma(boolean isOcularTrauma) {
        this.isOcularTrauma = isOcularTrauma;
    }

    public boolean getIsRedness() {
        return isRedness;
    }

    public void setIsRedness(boolean isRedness) {
        this.isRedness = isRedness;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}