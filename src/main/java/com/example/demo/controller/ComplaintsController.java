package com.example.demo.controller;

import com.example.demo.Helper.CommonHelper;
import com.example.demo.ViewModel.ComplaintViewModel;
import com.example.demo.dto.ComplaintInfo;
import com.example.demo.dto.ComplaintMasterData;
import com.example.demo.dto.ComplaintMasterSet;
import com.example.demo.service.ComplaintsService;

import jakarta.servlet.http.HttpServletRequest;

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
import java.util.Map;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintsController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ComplaintsController.class);
    private final ComplaintsService service;

    @Autowired
    private CommonHelper commonHelper;

    @Autowired
    private Environment environment;

    public ComplaintsController(ComplaintsService service, CommonHelper commonHelper) {
        this.service = service;
        this.commonHelper = commonHelper;
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

    long complaintsV1LastPeId = 164869447;

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
    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

@PostMapping("/{uId}/{peId}/{departmentId}/{createdBy}")
public ResponseEntity<?> create(@PathVariable String uId,
                                @PathVariable long peId,
                                @PathVariable int departmentId,
                                @PathVariable int createdBy,
                                @RequestBody ComplaintViewModel viewModel,
                                HttpServletRequest request) {
    try {
        String clientIp = commonHelper.getClientIp(request);
        ComplaintInfo response =
                service.create(uId, peId, departmentId, createdBy, clientIp, viewModel);

        return ResponseEntity.ok(response);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

@GetMapping("/abiComplaintList/{peId}")
public ResponseEntity<?> abiComplaintList(@PathVariable long peId) {

    try {
        // Get complaint master data from service
        ComplaintMasterSet masterSet = service.getComplaintMasterSet();
        List<ComplaintMasterData> masterData = masterSet.getComplaintMasterData();

        ComplaintViewModel data = service.abiComplaintList(masterData, peId);
        return ResponseEntity.ok(data);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

}
