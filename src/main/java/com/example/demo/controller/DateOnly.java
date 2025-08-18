package com.example.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateOnly {
    
    private LocalDate date;
    
    public DateOnly(LocalDate date) {
        this.date = date;
    }
    
    public static DateOnly parse(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
            return new DateOnly(localDate);
        } catch (Exception e) {
            // Try alternative format
            try {
                LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return new DateOnly(localDate);
            } catch (Exception ex) {
                return null;
            }
        }
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        return date != null ? date.toString() : "";
    }
}
