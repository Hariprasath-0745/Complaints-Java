package com.example.demo.service;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ComplaintService {
    private final EmployeeRepository repo;
    private final JdbcTemplate jdbcTemplate;

    public ComplaintService(EmployeeRepository repo, JdbcTemplate jdbcTemplate) {
        this.repo = repo;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Employee> getAll() {
        return repo.findAll();
    }

    public Employee getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Employee save(Employee e) {
        return repo.save(e);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
public boolean checkTrauma(String uid) {
    String sql = "SELECT EXISTS (" +
                 "SELECT 1 FROM patient_complaint pc " +
                 "WHERE pc.uid = ? " +
                 "AND pc.complaint_id = ? " +
                 "AND pc.entered_date BETWEEN CURRENT_DATE - (? || ' days')::interval AND CURRENT_DATE " +
                 "LIMIT 1) found";
    
    List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, uid, 72, 200);
    return result != null && !result.isEmpty() && (Boolean) result.get(0).get("found");
} 
}

