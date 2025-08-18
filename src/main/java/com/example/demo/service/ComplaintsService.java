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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ComplaintsService {
    private final EmployeeRepository repo;
    private final JdbcTemplate jdbcTemplate;

    public ComplaintsService(EmployeeRepository repo, JdbcTemplate jdbcTemplate) {
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
public ComplaintViewModel read(ComplaintMasterSet masterData,
                               long peId,
                               long complaintsV1LastPeId,
                               DateOnly visitDate,
                               boolean isSummary,
                               String viewMode) {

    List<PatientComplaint> patientComplaints = new ArrayList<>();

    boolean isShowAuthors = "current".equals(viewMode) || "previous".equals(viewMode);

    var vwComplaint = isSummary
            ? getViewByPeId(peId, complaintsV1LastPeId, visitDate)
            : getViewByPeId(peId);

    if (!vwComplaint.isEmpty()) {
        patientComplaints = mapper.map(vwComplaint, new TypeToken<List<PatientComplaint>>() {}.getType());
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
        // clear existing cache
        cacheManager.getCache("ComplaintsMaster").evict("Complaints");

        // retrieve data using cache with expiration
        List<ComplaintMasterData> mData = cacheService.get(
                "ComplaintsMaster", "Complaints",
                () -> masterService.getComplaintMaster(),
                Duration.ofSeconds(applicationProperties.getRedisCacheExpirySeconds()));

        List<VisitPurposeDto> vData = cacheService.get(
                "VisitPurposeMaster", "Complaints",
                () -> visitPurposeService.getMaster(),
                Duration.ofSeconds(applicationProperties.getRedisCacheExpirySeconds()));

        List<ComplaintReadingDto> rData = cacheService.get(
                "ComplaintsMaster", "ComplaintsReadings",
                () -> masterService.getComplaintReadings(),
                Duration.ofSeconds(applicationProperties.getRedisCacheExpirySeconds()));

        List<ComplaintSubcomplaintDto> scData = cacheService.get(
                "ComplaintsMaster", "ComplaintSubcomplaint",
                () -> masterService.getComplaintSubcomplaints(),
                Duration.ofSeconds(applicationProperties.getRedisCacheExpirySeconds()));

        List<ComplaintTemplateDto> tData = cacheService.get(
                "ComplaintsMaster", "ComplaintTemplate",
                () -> complaintTemplateService.getMaster(),
                Duration.ofSeconds(applicationProperties.getRedisCacheExpirySeconds()));

        ComplaintMasterSet masterSet = new ComplaintMasterSet();
        masterSet.setComplaintMasterData(mData);
        masterSet.setVisitPurposes(vData);
        masterSet.setComplaintReadings(rData);
        masterSet.setSubcomplaints(scData);
        masterSet.setComplaintTemplates(tData);

        return masterSet;
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


}

