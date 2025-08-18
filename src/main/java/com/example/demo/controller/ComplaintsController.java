package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class ComplaintsController {
    private final ComplaintService service;

    public ComplaintsController(ComplaintService service) {
        this.service = service;
    }
@GetMapping("/check-trauma")
    public ResponseEntity<?> checkTrauma(@RequestParam  String uId) {
        try {
            boolean data = service.checkTrauma(uId);
            return ResponseEntity.ok(data);
        }

        catch (Exception ex) {
            //logger.error(ex.getMessage(), ex);
            return ResponseEntity.status(417).body( ex.getMessage());
        }
    }
    @GetMapping
    public List<Employee> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Employee one(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Employee create(@RequestBody Employee e) {
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
