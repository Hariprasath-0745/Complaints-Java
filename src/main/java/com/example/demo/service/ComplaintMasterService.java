package com.example.demo.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.ViewModel.Complaint;
import com.example.demo.ViewModel.ComplaintViewModel;
import com.example.demo.ViewModel.SubComplaint;
import com.example.demo.dto.ComplaintMasterData;
import com.example.demo.dto.ComplaintReadingDto;
import com.example.demo.dto.ComplaintSubcomplaintDto;
import com.example.demo.dto.SubcomplaintData;
import com.example.demo.Entity.Master.*;

@Service
public class ComplaintMasterService {
    
    private static final Logger log = LoggerFactory.getLogger(ComplaintMasterService.class);
    
    @Autowired
    private DataSource dataSource;
    
    // Stub dependencies
    private Object cacheService = new Object() {
        @SuppressWarnings("unchecked")
        public <T> T get(String key1, String key2, java.util.function.Supplier<T> supplier, Duration duration) {
            return supplier.get();
        }
    };
    
    private Object masterService = this;
    
    private Object applicationProperties = new Object() {
        public long getRedisCacheExpirySeconds() {
            return 300;
        }
    };
    
    private Object service = new Object() {
        public ComplaintViewModel abiComplaintList(List<ComplaintMasterData> masterData, long peId) {
            return new ComplaintViewModel();
        }
    };
    
    private static class ComplaintServiceQueries {
        public static final String GET_COMPLAINT_MASTER = "SELECT * FROM complaints";
        public static final String GET_COMPLAINT_SUBCOMPLAINTS = "SELECT * FROM complaint_subcomplaints";
        public static final String GET_COMPLAINT_READINGS = "SELECT * FROM complaint_readings";
    }

    public CompletableFuture<List<ComplaintMasterData>> getComplaintMaster() {

    return CompletableFuture.supplyAsync(() -> {

        String finalQuery = ComplaintServiceQueries.GET_COMPLAINT_MASTER;

        if (finalQuery == null || finalQuery.isEmpty()) {
            return Collections.emptyList();
        }

        // execute query and read multiple resultsets
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(finalQuery);
             ResultSet rs = ps.executeQuery()) {

            List<Complaint> complaints          = new ArrayList<>();
            List<SubComplaint> subcomplaints    = new ArrayList<>();
            List<ComplaintSubcomplaint> compSub = new ArrayList<>();
            List<ComplaintResult> results       = new ArrayList<>();
            List<ComplaintReading> readings     = new ArrayList<>();

            // NOTE: standard JDBC doesn't support QueryMultiple,
            // so we execute the query in separate statements or
            // use a stored procedure returning multiple result sets.
            //
            // For simplicity we assume the query returns 5 resultsets
            // and we iterate over them one by one using `getMoreResults()`.

            // ResultSet 1 → complaint
            while (rs.next()) {
                // Map complaint from result set - simplified for now
                Complaint complaint = new Complaint();
                // complaint.setId(rs.getLong("id"));
                // complaint.setComplaintText(rs.getString("complaint_text"));
                // complaint.setIsOcular(rs.getBoolean("is_ocular"));
                // complaint.setIsActive(rs.getBoolean("is_active"));
                // complaint.setIsEvent(rs.getBoolean("is_event"));
                complaints.add(complaint);
            }

            // ResultSet 2 → subcomplaints
            if (ps.getMoreResults()) {
                ResultSet rs2 = ps.getResultSet();
                while (rs2.next()) {
                    // Map subcomplaint from result set
                    SubComplaint subComplaint = new SubComplaint();
                    subcomplaints.add(subComplaint);
                }
            }

            // ResultSet 3 → complaintSubcomplaints
            if (ps.getMoreResults()) {
                ResultSet rs3 = ps.getResultSet();
                while (rs3.next()) {
                    // Map complaint subcomplaint from result set
                    ComplaintSubcomplaint compSubcomplaint = new ComplaintSubcomplaint();
                    compSub.add(compSubcomplaint);
                }
            }

            // ResultSet 4 → results
            if (ps.getMoreResults()) {
                ResultSet rs4 = ps.getResultSet();
                while (rs4.next()) {
                    // Map complaint result from result set
                    ComplaintResult result = new ComplaintResult();
                    results.add(result);
                }
            }

            // ResultSet 5 → readings
            if (ps.getMoreResults()) {
                ResultSet rs5 = ps.getResultSet();
                while (rs5.next()) {
                    // Map complaint reading from result set
                    ComplaintReading reading = new ComplaintReading();
                    readings.add(reading);
                }
            }

            // build final data
            return complaints.stream()
                    .map(c -> {
                        ComplaintMasterData data = new ComplaintMasterData();
                        // Map complaint to ComplaintMasterData
                        // data.setId(c.getId());
                        // data.setIsOcular(c.isOcular());
                        // data.setComplaintText(c.getComplaintText());
                        // data.setIsActive(c.isActive());
                        // data.setIsEvent(c.isEvent());
                        data.setSubcomplaints(new ArrayList<>());
                        data.setComplaintAssociateId(new ArrayList<>());
                        data.setDepartmentId(new ArrayList<>());
                        return data;
                    })
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error while retrieving complaint master data", ex);
            return Collections.emptyList();
        }
    });
}
public CompletableFuture<List<ComplaintSubcomplaintDto>> getComplaintSubcomplaints() {

    return CompletableFuture.supplyAsync(() -> {
        String sql = ComplaintServiceQueries.GET_COMPLAINT_SUBCOMPLAINTS;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<ComplaintSubcomplaintDto> list = new ArrayList<>();
            while (rs.next()) {
                ComplaintSubcomplaintDto dto = new ComplaintSubcomplaintDto();
                dto.setId(rs.getLong("Id"));
                dto.setComplaintId(rs.getLong("ComplaintId"));
                dto.setSubcomplaintId(rs.getLong("SubcomplaintId"));
                dto.setSequence(rs.getInt("Sequence"));
                dto.setIsActive(rs.getBoolean("IsActive"));
                list.add(dto);
            }
            return list;

        } catch (Exception ex) {
            log.error("Error getting complaint subcomplaints", ex);
            return Collections.emptyList();
        }
    });
}

public CompletableFuture<List<ComplaintReadingDto>> getComplaintReadings() {

    return CompletableFuture.supplyAsync(() -> {
        String sql = ComplaintServiceQueries.GET_COMPLAINT_READINGS;

        if (sql == null || sql.isEmpty()) {
            return new ArrayList<>();
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<ComplaintReadingDto> list = new ArrayList<>();
            while (rs.next()) {
                ComplaintReadingDto dto = new ComplaintReadingDto();
                dto.setId(rs.getLong("Id"));
                dto.setComplaintSubcomplaintId(rs.getLong("ComplaintSubcomplaintId"));
                dto.setResultId(rs.getLong("ResultId"));
                dto.setSequence(rs.getInt("Sequence"));
                dto.setIsActive(rs.getBoolean("IsActive"));
                list.add(dto);
            }
            return list;

        } catch (Exception ex) {
            log.error("Error getting complaint readings", ex);
            return Collections.emptyList();
        }
    });
}
    
public ResponseEntity<?> abiComplaintList(long peId) {

    try {
        // cache lookup for complaint master data
        List<ComplaintMasterData> masterData = getComplaintMaster().join(); // Simplified stub

        ComplaintViewModel data = new ComplaintViewModel(); // Stub implementation
        return ResponseEntity.ok(data);

    } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(417)
                .body(Collections.singletonMap("Message", ex.getMessage()));
    }
}



}
