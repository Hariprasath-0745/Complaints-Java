package com.example.demo.repository;

import com.example.demo.Entity.Master.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComplaintOcularFieldRepository extends JpaRepository<ComplaintOcularField, UUID> {
    
    @Query("SELECT c FROM ComplaintOcularField c LEFT JOIN FETCH c.complaintOcularFieldItems WHERE c.isActive = true")
    List<ComplaintOcularField> findAllWithItems();
    
    @Query("SELECT c FROM ComplaintOcularField c LEFT JOIN FETCH c.complaintOcularFieldItems WHERE c.id = :id AND c.isActive = true")
    ComplaintOcularField findByIdWithItems(@Param("id") UUID id);
}
