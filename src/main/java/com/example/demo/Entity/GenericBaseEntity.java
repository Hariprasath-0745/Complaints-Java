package com.example.demo.Entity;
import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GenericBaseEntity<T> {

    @Id
    @Column(name = "id")
    private T id;

    @NotNull
    @Column(name = "is_active")
    private Boolean isActive = true;

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
