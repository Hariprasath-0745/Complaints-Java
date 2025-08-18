package com.example.demo.dto;

import org.antlr.v4.runtime.misc.NotNull;

public abstract class GenericBaseDto<T> {

    @NotNull
    private T id;

    @NotNull
    private Boolean isActive;

    // getters and setters
}

