package com.example.demo.service;

import com.example.demo.Entity.PatientComplaint;
import com.example.demo.ViewModel.ComplaintViewModel;
import com.example.demo.controller.DateOnly;
import com.example.demo.dto.ComplaintMasterData;
import com.example.demo.dto.ComplaintMasterSet;
import com.example.demo.dto.ComplaintReadingDto;
import com.example.demo.dto.ComplaintSubcomplaintDto;
import com.example.demo.dto.ComplaintTemplateDto;
import com.example.demo.dto.VisitPurposeDto;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

@Service
public class ComplaintsService {
    
    private static final Logger log = LoggerFactory.getLogger(ComplaintsService.class);
    
    private final EmployeeRepository repo;
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private Environment environment;
    
    
    @Autowired
    private ComplaintMasterService masterService;
    
    @Autowired
    private VisitPurposeService visitPurposeService;
    
    @Autowired
    private ComplaintTemplateService complaintTemplateService;

    public ComplaintsService(EmployeeRepository repo, JdbcTemplate jdbcTemplate) {
        this.repo = repo;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private static class ComplaintServiceQueries {
        public static final String GET_COMPLAINT_SUBCOMPLAINTS = "SELECT * FROM complaint_subcomplaints WHERE is_active = true";
        public static final String GET_COMPLAINT_READINGS = "SELECT * FROM complaint_readings WHERE is_active = true";
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
public ComplaintViewModel read(ComplaintMasterSet masterData,
                               long peId,
                               long complaintsV1LastPeId,
                               DateOnly visitDate,
                               boolean isSummary,
                               String viewMode) {

    List<PatientComplaint> patientComplaints = new ArrayList<>();

    boolean isShowAuthors = "current".equals(viewMode) || "previous".equals(viewMode);

    List<PatientComplaint> vwComplaint = isSummary
            ? getViewByPeId(peId, complaintsV1LastPeId, visitDate)
            : getViewByPeId(peId);

    if (vwComplaint != null && !vwComplaint.isEmpty()) {
        patientComplaints = vwComplaint;
    }

    if (patientComplaints.isEmpty()) {
        ComplaintViewModel vm = new ComplaintViewModel();
        vm.setId(0L);
        return vm;
    }

    ComplaintViewModel viewModel = dataModelToViewModel(complaintsV1LastPeId, patientComplaints, isShowAuthors);
    bindMasterValue(viewModel, masterData);

    return viewModel;
}
private CompletableFuture<ComplaintMasterSet> buildComplaintMasterSet() {
    return CompletableFuture.supplyAsync(() -> {
        try {
            // Simplified implementation without caching
            List<ComplaintMasterData> mData = masterService.getComplaintMaster().join();
            List<VisitPurposeDto> vData = visitPurposeService.getMaster().join();
            List<ComplaintReadingDto> rData = masterService.getComplaintReadings().join();
            List<ComplaintSubcomplaintDto> scData = masterService.getComplaintSubcomplaints().join();
            List<ComplaintTemplateDto> tData = complaintTemplateService.getMaster().join();

            ComplaintMasterSet masterSet = new ComplaintMasterSet();
            masterSet.setComplaintMasterData(mData);
            masterSet.setVisitPurposes(vData);
            masterSet.setComplaintReadings(rData);
            masterSet.setSubcomplaints(scData);
            masterSet.setComplaintTemplates(tData);

            return masterSet;
        } catch (Exception ex) {
            log.error("Error building complaint master set", ex);
            return new ComplaintMasterSet();
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

    public ComplaintMasterSet getComplaintMasterSet() {
        return buildComplaintMasterSet().join();
    }

    // Helper methods - simplified implementations
    private List<PatientComplaint> getViewByPeId(long peId, long complaintsV1LastPeId, DateOnly visitDate) {
        return new ArrayList<>(); // Implement actual query logic
    }
    
    private List<PatientComplaint> getViewByPeId(long peId) {
        return new ArrayList<>(); // Implement actual query logic
    }
    
    private ComplaintViewModel dataModelToViewModel(long complaintsV1LastPeId, List<PatientComplaint> patientComplaints, boolean isShowAuthors) {
        ComplaintViewModel vm = new ComplaintViewModel();
        vm.setId(0L);
        return vm; // Implement actual mapping logic
    }
    
    private void bindMasterValue(ComplaintViewModel viewModel, ComplaintMasterSet masterData) {
        // Implement binding logic
    }
public boolean checkPeids(long peId) {

    String sql = ComplaintServiceQueries.CHECK_PEIDS;

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setLong(1, peId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        }

    } catch (Exception ex) {
        log.error("Error checking PeIds", ex);
    }

    return false;
}


}

