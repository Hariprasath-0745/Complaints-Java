package com.example.demo.dto;

import java.util.List;

public class SubcomplaintData {

    private long id;
    private String subcomplaintText = "";
    private boolean isActive;
    private List<ComplaintResultData> results;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubcomplaintText() {
        return subcomplaintText;
    }

    public void setSubcomplaintText(String subcomplaintText) {
        this.subcomplaintText = subcomplaintText;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<ComplaintResultData> getResults() {
        return results;
    }

    public void setResults(List<ComplaintResultData> results) {
        this.results = results;
    }
}
