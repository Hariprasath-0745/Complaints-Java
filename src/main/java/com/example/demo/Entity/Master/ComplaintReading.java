package com.example.demo.Entity.Master;

import java.nio.charset.CoderResult;

import org.antlr.v4.runtime.misc.NotNull;

import com.example.demo.Entity.BaseEntity;

public class ComplaintReading extends BaseEntity {

    @NotNull
    private Long complaintSubcomplaintId;

    private Long resultId;

    @NotNull
    private Integer sequence;

    private ComplaintSubcomplaint complaintSubcomplaint;
    private CoderResult complaintResult;

    // getters and setters
}


