package com.example.demo.controller;

import com.example.demo.Helper.CommonHelper;
import com.example.demo.ViewModel.ComplaintSummary;
import com.example.demo.ViewModel.ComplaintViewModel;
import com.example.demo.dto.ComplaintInfo;
import com.example.demo.dto.ComplaintMasterData;
import com.example.demo.dto.ComplaintMasterSet;
import com.example.demo.dto.Peid;
import com.example.demo.service.ComplaintsService;
import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Value("${app.complaints.v1-last-pe-id}")
    private long complaintsV1LastPeId;

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

    // Use the injected property value

    LocalDate visitDate = (vDate == null || vDate.equals("0")) ?
            null :
            LocalDate.parse(vDate);

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

@GetMapping("/checkTime")
public ResponseEntity<?> checkTime() {

    // timezone from configuration (hard-coded as per your note)
    ZoneId zoneId = ZoneId.of("Asia/Kolkata");

    LocalDateTime localServerTime = LocalDateTime.now();            // server time
    Instant utcInstant = Instant.now();                             // UTC
    ZonedDateTime istDateTime = utcInstant.atZone(zoneId);          // convert UTC → Asia/Kolkata
    LocalDate date = istDateTime.toLocalDate();                     // only date

    Map<String, Object> data = new HashMap<>();
    data.put("localServerTime", localServerTime);
    data.put("utcTime", utcInstant);
    data.put("istTime", istDateTime);
    data.put("date", date);

    return ResponseEntity.ok(data);
}

@GetMapping("/dischargeSummary/{uid}/{eye}/{sDate}/{eDate}")
public ResponseEntity<?> dischargeSummaryList(@PathVariable String uid,
                                              @PathVariable int eye,
                                              @PathVariable String sDate,
                                              @PathVariable String eDate) {

    try {
        LocalDate startDate           = LocalDate.parse(sDate);
        LocalDate endDate             = LocalDate.parse(eDate);
        LocalDate complaintsV1LastDate = LocalDate.parse("2023-01-01");   // <-- hard-coded

        ComplaintMasterSet masterData = service.getComplaintMasterSet();
        ComplaintSummary result = service.dischargeSummaryListV2(
                masterData, uid, eye, startDate, endDate, complaintsV1LastDate);

        return ResponseEntity.ok(result);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

@GetMapping("/dischargeSummaryV1/{uid}/{eye}/{sDate}/{eDate}")
public ResponseEntity<?> dischargeSummaryListV1(@PathVariable String uid,
                                                @PathVariable int eye,
                                                @PathVariable String sDate,
                                                @PathVariable String eDate) {

    try {
        LocalDate startDate = LocalDate.parse(sDate);
        LocalDate endDate   = LocalDate.parse(eDate);
        LocalDate complaintsV1LastDate = LocalDate.parse("2023-01-01"); // hard-coded value

        ComplaintViewModel data = service.dischargeSummaryList(uid, eye, startDate, endDate, complaintsV1LastDate);
        return ResponseEntity.ok(data);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

@PostMapping("/medicalSummaryV1")
public ResponseEntity<?> medicalSummaryListV1(@RequestBody Peid peIds) {

    try {
        // Use the injected property value
        List<ComplaintViewModel> data = service.medicalSummaryList(peIds, complaintsV1LastPeId);
        return ResponseEntity.ok(data);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

@PostMapping("/medicalSummary")
public ResponseEntity<?> medicalSummaryList(@RequestBody Peid peIds) {

    try {
        // fetch master data
        ComplaintMasterSet masterData = service.getComplaintMasterSet();

        // Use the injected property value

        List<ComplaintSummary> result =
                service.medicalSummaryListV2(masterData, peIds, complaintsV1LastPeId);

        return ResponseEntity.ok(result);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

@PutMapping("/{uId}/{peId}/{departmentId}/{updatedBy}")
public ResponseEntity<?> update(@PathVariable String uId,
                                @PathVariable long peId,
                                @PathVariable int departmentId,
                                @PathVariable int updatedBy,
                                @RequestBody ComplaintViewModel viewModel,
                                HttpServletRequest request) {
    try {
        String clientIp = commonHelper.getClientIp(request);

        ComplaintInfo response =
                service.update(uId, peId, departmentId, updatedBy, clientIp, viewModel);

        return ResponseEntity.ok(response);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

@PutMapping("/update-uin-merge-complaints/{oldUid}/{newUid}")
public ResponseEntity<?> updateUinMergeComplaints(@PathVariable String oldUid,
                                                  @PathVariable String newUid) {
    try {
        int rowsAffected = service.updateUinMergeComplaints(oldUid, newUid);
        return ResponseEntity.ok(rowsAffected);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}

}
