package com.example.demo.Entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity extends GenericBaseEntity<Long> {
    // nothing additional here
}

