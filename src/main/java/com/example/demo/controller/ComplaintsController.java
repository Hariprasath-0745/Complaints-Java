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
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}
@GetMapping("/data/{peId}")
public ResponseEntity<?> getData(@PathVariable long peId) {
    try {
        if (service.checkPeids(peId)) {
            ComplaintMasterSet masterData = service.getComplaintMasterSet();
            ComplaintViewModel data =
                    service.read(masterData, peId, 0L, null, false, "0");

            return ResponseEntity.ok(data);
        } else {
            ComplaintViewModel empty = new ComplaintViewModel();
            empty.setId(0L);
            return ResponseEntity.ok(empty);
        }
    } catch (BadRequestException brex) {
        log.warn(brex.getMessage(), brex);
        return ResponseEntity.badRequest()
                .body(Collections.singletonMap("Message", brex.getMessage()));
    } catch (NotFoundException nfx) {
        log.warn(nfx.getMessage(), nfx);
        Map<String,Object> body = new HashMap<>();
        body.put("Source", nfx.getSource());
        body.put("Message", nfx.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}


}
