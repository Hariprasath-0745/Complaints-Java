package com.example.demo.Entity.Master;

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

import com.example.demo.Entity.BaseEntity;
import com.example.demo.ViewModel.Complaint;
import com.example.demo.ViewModel.SubComplaint;

public class ComplaintSubcomplaint extends BaseEntity {

    @NotNull
    private Long complaintId;

    @NotNull
    private Long subcomplaintId;

    @NotNull
    private Integer sequence;

    private Complaint complaint;
    private SubComplaint subcomplaint;
    private List<ComplaintReading> complaintReadings;

    // getters and setters
}

