package com.example.demo.controller;

import com.example.demo.ViewModel.ComplaintViewModel;
import com.example.demo.dto.ComplaintMasterSet;
import com.example.demo.model.Employee;
import com.example.demo.service.ComplaintsService;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class ComplaintsController {
    private final ComplaintsService service;
    
    @Autowired
    private Environment environment;

    public ComplaintsController(ComplaintsService service) {
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

    @GetMapping("/summary/{peId}/{vDate}/{viewMode}")
public ResponseEntity<?> summary(@PathVariable long peId,
                                 @PathVariable String vDate,
                                 @PathVariable String viewMode) {

    long complaintsV1LastPeId = Long.parseLong(environment.getProperty("ComplaintsV1LastPeId"));

    DateOnly visitDate = (vDate == null || vDate.equals("0")) ?
            null :
            DateOnly.parse(vDate); // Simplified - removed CultureInfo dependency

    ComplaintMasterSet masterData = service.getComplaintMasterSet();

    try {
        ComplaintViewModel data =
                service.read(masterData, peId, complaintsV1LastPeId, visitDate, true, viewMode);

        return ResponseEntity.ok(data);

    } 
    catch (Exception ex) {
        //log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

}
