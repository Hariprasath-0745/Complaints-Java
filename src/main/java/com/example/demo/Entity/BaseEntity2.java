package com.example.demo.Entity;

import jakarta.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity2 extends GenericBaseEntity<UUID> {
}
