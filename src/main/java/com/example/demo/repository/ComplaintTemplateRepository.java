// Correct: top-level interface
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Entity.ComplaintTemplate;

public interface ComplaintTemplateRepository extends JpaRepository<ComplaintTemplate, Long> {
    List<ComplaintTemplate> findByDeptId(Long deptId);
}

