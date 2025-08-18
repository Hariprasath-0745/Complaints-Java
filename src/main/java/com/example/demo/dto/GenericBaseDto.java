package com.example.demo.dto;

import org.antlr.v4.runtime.misc.NotNull;

public abstract class GenericBaseDto<T> {

    @NotNull
    private T id;

    @NotNull
    private Boolean isActive;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

