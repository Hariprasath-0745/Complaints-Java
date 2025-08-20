package com.example.demo.service;

import com.example.demo.Entity.PatientComplaint;
import com.example.demo.Entity.VwPatientComplaint;
import com.example.demo.Entity.VwPatientComplaintReading;
import com.example.demo.Entity.VwPatientComplaintTrauma;
import com.example.demo.ViewModel.Complaint;
import com.example.demo.ViewModel.ComplaintDetail;
import com.example.demo.ViewModel.ComplaintSummary;
import com.example.demo.ViewModel.ComplaintTrauma;
import com.example.demo.ViewModel.ComplaintViewModel;
import com.example.demo.ViewModel.SubComplaint;
import com.example.demo.dto.ComplaintInfo;
import com.example.demo.dto.ComplaintMasterData;
import com.example.demo.dto.ComplaintMasterSet;
import com.example.demo.dto.ComplaintReadingDto;
import com.example.demo.dto.ComplaintResultData;
import com.example.demo.dto.ComplaintSubcomplaintDto;
import com.example.demo.dto.ComplaintTemplateDto;
import com.example.demo.dto.Peid;
import com.example.demo.dto.VisitPurposeDto;
import com.example.demo.Model.Employee;
import com.example.demo.Model.UserLogDetail;
import com.example.demo.repository.EmployeeRepository;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    
    // Stub repositories and dependencies
    private Object patientComplaintRepository = new Object() {
        public boolean existsByPeIdAndEnteredDate(long peId, LocalDate date) { return false; }
        public void saveAll(List<PatientComplaint> entities) {}
    };
    
    private Object unitOfWork = new Object() {
        public void saveChanges() {}
    };
    
    private Object applicationProperties = new Object() {
        public String getTz() { return "UTC"; }
    };
    
    private static class DateTimeHelper {
        public static Object getTimeZoneFromSettings(String tz) {
            return new Object() {
                public LocalDate toLocalDate() { return LocalDate.now(); }
            };
        }
    }
    
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
        public static final String CHECK_PEIDS = "SELECT COUNT(*) > 0 FROM patients WHERE pe_id = ?";
        
        public static final String FETCH_COMPLAINT_DISCHARGE_SUMMARY = """
            SELECT
                pc.id AS Id,
                pc.is_active AS IsActive,
                pc.is_deleted AS IsDeleted,
                pc.created_by AS CreatedBy,
                pc.created_at AS CreatedAt,
                pc.created_ip AS CreatedIP,
                pc.updated_by AS UpdatedBy,
                pc.updated_at AS UpdatedAt,
                pc.updated_ip AS UpdatedIP,
                pc.deleted_by AS DeletedBy,
                pc.deleted_at AS DeletedAt,
                pc.deleted_ip AS DeletedIP,
                pc.pe_id AS PeId,
                pc.uid AS Uid,
                pc.created_dept_id AS CreatedDeptId,
                pc.updated_dept_id AS UpdatedDeptId,
                pc.entered_date AS EnteredDate,
                pc.complaint_id AS ComplaintId,
                pc.duration_date AS DurationDate,
                pc.eye_part_id AS EyePartId,
                pc.free_text AS FreeText,
                pc.template_id AS TemplateId,
                pc.template_text AS TemplateText,
                pc.is_no_history AS IsNoHistory,
                pc.visit_purpose AS VisitPurpose,
                pc.informant AS Informant,
                pc.informant_others AS InformantOthers,
                pc.visit_purpose_others AS VisitPurposeOthers,
                pc.duration AS Duration
            FROM
                public.patient_complaint AS pc
            WHERE
                pc.uid = ? AND
                pc.entered_date >= ? AND
                pc.entered_date <= ?;

            SELECT
                pcr.id AS Id,
                pcr.is_active AS IsActive,
                pcr.patient_complaint_id AS PatientComplaintId,
                pcr.complaint_subcomplaint_id AS ComplaintSubcomplaintId,
                pcr.complaint_reading_id AS ComplaintReadingId
            FROM
                public.patient_complaint as pc
            INNER JOIN
                public.patient_complaint_readings AS pcr on pc.id = pcr.patient_complaint_id
            WHERE
                pc.uid = ? AND
                pc.entered_date >= ? AND
                pc.entered_date <= ?;

            SELECT
                pct.id AS Id,
                pct.is_active AS IsActive,
                pct.patient_complaint_id AS PatientComplaintId,
                pct.nature_of_trauma AS NatureOfTrauma,
                pct.trauma_date AS TraumaDate,
                pct.trauma_time AS TraumaTime,
                pct.place_of_trauma AS PlaceOfTrauma,
                pct.is_prior_treatment AS IsPriorTreatment,
                pct.prior_treatment_detail AS PriorTreatmentDetail,
                pct.trauma_remarks AS TraumaRemarks,
                pct.attender_detail AS AttenderDetail,
                pct.is_mlc_case AS IsMLCCase,
                pct.identification_marks AS IdentificationMarks
            FROM
                public.patient_complaint as pc
            INNER JOIN
                public.patient_complaint_trauma AS pct on pc.id = pct.patient_complaint_id
            WHERE
                pc.uid = ? AND
                pc.entered_date >= ? AND
                pc.entered_date <= ?
            """;

        public static final String FETCH_COMPLAINT_DISCHARGE_V1_SUMMARY = "uspGetComplaintDischargeSummary";
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
                               LocalDate visitDate,
                               boolean isSummary,
                               String viewMode) {

    List<PatientComplaint> patientComplaints = new ArrayList<>();

    boolean isShowAuthors = "current".equals(viewMode) || "previous".equals(viewMode);

    List<PatientComplaint> vwComplaint;
    if (isSummary) {
        List<VwPatientComplaint> vwResults = getViewByPeId(peId, complaintsV1LastPeId, visitDate);
        vwComplaint = mapToPatientComplaints(vwResults);
    } else {
        vwComplaint = getViewByPeId(peId);
    }

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
private ComplaintMasterSet buildComplaintMasterSet() {
    try {
        // Simplified implementation without caching
        List<ComplaintMasterData> mData = new ArrayList<>();
        List<VisitPurposeDto> vData = new ArrayList<>();
        List<ComplaintReadingDto> rData = new ArrayList<>();
        List<ComplaintSubcomplaintDto> scData = new ArrayList<>();
        List<ComplaintTemplateDto> tData = new ArrayList<>();

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
        return buildComplaintMasterSet();
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

public ComplaintInfo create(String uId,
                            long peId,
                            int departmentId,
                            int createdBy,
                            String clientIp,
                            ComplaintViewModel viewModel) {

    LocalDate visitDate = LocalDate.now(); // Simplified stub

    boolean exists = false; // Simplified stub

    UserLogDetail userLogDetail = new UserLogDetail();
    userLogDetail.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    userLogDetail.setCreatedBy(createdBy);
    userLogDetail.setCreatedIp(clientIp != null ? clientIp : "");
    userLogDetail.setCreatedDeptId((long)departmentId);
    userLogDetail.setUpdatedAt(null);
    userLogDetail.setUpdatedBy(0);
    userLogDetail.setUpdatedIp("");
    userLogDetail.setUpdatedDeptId(0L);

    if (exists) {
        return update(uId, peId, departmentId, createdBy, clientIp, viewModel);
    } else {
        List<PatientComplaint> entities =
                viewModelToDataModel(uId, peId, userLogDetail, viewModel);

        // Simplified stubs - no actual database operations

        try {
            // Stub save operation

            ComplaintInfo info = new ComplaintInfo();
            info.setIsOcularTrauma(isAddedOcularTrauma(viewModel.getComplaint().getComplaintDetails()));
            info.setIsRedness(isAddedRedness(viewModel.getComplaint().getComplaintDetails()));
            return info;

        } catch (Exception ex) {
            ComplaintInfo info = new ComplaintInfo();
            info.setError(
                    ex.getCause() != null
                            ? ex.getCause().getMessage()
                            : ex.getMessage()
            );
            return info;
        }
    }
}


private boolean isAddedOcularTrauma(List<ComplaintDetail> details) {
    return details != null && details.stream()
            .anyMatch(d -> d.getComplaintTrauma() != null && !d.getComplaintTrauma().isEmpty());
}

private boolean isAddedRedness(List<ComplaintDetail> details) {
    return details != null && details.stream()
            .anyMatch(d -> !d.isNoHistory()
                    && d.getComplaintId() == ComplaintsConstants.REDNESS_ID);
}

public ComplaintViewModel abiComplaintList(List<ComplaintMasterData> masterData, long peId) {

    // var results = await _patientComplaintRepository.AbiComplaintSummary(peId);
    List<VwPatientComplaint> results = abiComplaintSummary(peId);

    if (results != null && !results.isEmpty()) {

        // keep only items that have ComplaintId
        List<VwPatientComplaint> details = results.stream()
                .filter(r -> r.getComplaintId() != null && r.getComplaintId() != 0)
                .collect(Collectors.toList());

        // group by ComplaintId and take only one item from each group
        List<VwPatientComplaint> grouped =
                details.stream()
                       .collect(Collectors.groupingBy(VwPatientComplaint::getComplaintId))
                       .values()
                       .stream()
                       .map(list -> list.get(0))
                       .collect(Collectors.toList());

        // map to PatientComplaint
        List<PatientComplaint> complaints = new ArrayList<>(); // Simplified stub

        // build viewModel
        ComplaintViewModel viewModel = dataModelToViewModel(0L, complaints, false);

        if (viewModel != null && masterData != null) {
            List<ComplaintDetail> complaintDetails =
                    viewModel.getComplaint() != null
                            ? viewModel.getComplaint().getComplaintDetails()
                            : null;

            if (complaintDetails != null) {
                mapComplaintsName(complaintDetails, masterData, null, null);
            }
        }

        return viewModel;
    }

    // no data
    ComplaintViewModel empty = new ComplaintViewModel();
    empty.setId(0L);
    return empty;
}

private List<PatientComplaint> viewModelToDataModel(String uId, long peId, UserLogDetail userLogDetail, ComplaintViewModel viewModel) {
    // Stub implementation
    return new ArrayList<>();
}

// Add missing methods
private List<VwPatientComplaint> abiComplaintSummary(long peId) {
    return new ArrayList<>();
}

private void mapComplaintsName(List<ComplaintDetail> complaintDetails, List<ComplaintMasterData> masterData, Object param1, Object param2) {
    // Stub implementation
}

private static class ComplaintsConstants {
    public static final int NO_HISTORY_COMPLAINT_ID = 999;
    public static final int REDNESS_ID = 1001;
    public static final int OCULAR_TRAUMA_ID = 1002;
}

// Remove the unused mapper and TypeToken stubs

// Helper method to convert VwPatientComplaint to PatientComplaint
private List<PatientComplaint> mapToPatientComplaints(List<VwPatientComplaint> vwList) {
    List<PatientComplaint> result = new ArrayList<>();
    for (VwPatientComplaint vw : vwList) {
        PatientComplaint pc = new PatientComplaint();
        // TODO: Map fields from vw to pc as needed
        result.add(pc);
    }
    return result;
}

public ComplaintSummary dischargeSummaryListV2(ComplaintMasterSet masterData,
                                               String uid,
                                               int eye,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               LocalDate complaintsV1LastDate) {

    List<VwPatientComplaint> results = dischargeSummary(uid, startDate, endDate, complaintsV1LastDate);

    if (results == null || results.isEmpty()) {
        return new ComplaintSummary();
    }

    // get the last peId
    long peId = results.stream()
                       .sorted(Comparator.comparing(VwPatientComplaint::getPeId))
                       .map(VwPatientComplaint::getPeId)
                       .distinct()
                       .reduce((first, second) -> second)
                       .orElse(0L);

    // filter by that peId
    List<VwPatientComplaint> vwComplaint =
            results.stream().filter(r -> r.getPeId() == peId).collect(Collectors.toList());

    // filter by eye
    List<VwPatientComplaint> groupEye = vwComplaint.stream()
            .filter(v -> (eye == 1 && (v.getEyePartId() == 1 || v.getEyePartId() == 3)) ||
                         (eye == 2 && (v.getEyePartId() == 2 || v.getEyePartId() == 3)) ||
                         (eye == 3 && (v.getEyePartId() == 1 || v.getEyePartId() == 2 || v.getEyePartId() == 3)))
            .collect(Collectors.toList());

    if (groupEye.isEmpty()) {
        return new ComplaintSummary();
    }

    List<PatientComplaint> patientComplaint = new ArrayList<>(); // Simplified mapping

    ComplaintViewModel viewModel =
            dataModelToViewModel(0L, patientComplaint, false);

    bindMasterValue(viewModel, masterData);

    // build result
    ComplaintSummary summary = new ComplaintSummary();
    summary.setPeid(viewModel.getPeId());

    Complaint complaint = viewModel.getComplaint();
    summary.setInformant(
            complaint.getComplaintInformant() != null
                    ? complaint.getComplaintInformant()
                    : complaint.getComplaintInformantOther());

    String visitPurpose = Optional.ofNullable(viewModel.getComplaintVisitPurpose())
            .map(vp -> (vp.getVisitPurposeText() + ", " +
                       Optional.ofNullable(vp.getVisitPurposeOthers()).orElse("")))
            .orElse("");

    summary.setVisitPurpose(visitPurpose);

    List<String> complaints = complaintDisplayFormat(
            viewModel.getComplaint().getComplaintDetails());

    summary.setComplaints(complaints);

    return summary;
}

private List<String> complaintDisplayFormat(List<ComplaintDetail> complaintDetails) {

    List<String> complaint = new ArrayList<>();
    List<String> nhRe  = new ArrayList<>();
    List<String> nhLe  = new ArrayList<>();
    List<String> nhBe  = new ArrayList<>();
    List<String> nhNon = new ArrayList<>();

    for (ComplaintDetail cd : complaintDetails) {
        if (cd.getComplaintId() != ComplaintsConstants.OCULAR_TRAUMA_ID && cd.getEyePartId() != 0) {

            if (cd.getComplaintId() != 0 &&
                cd.getFreeText().isEmpty() &&
                cd.isNoHistory()) {

                switch (cd.getEyePartId()) {
                    case 1 -> nhRe.add(cd.getComplaint());
                    case 2 -> nhLe.add(cd.getComplaint());
                    case 3 -> nhBe.add(cd.getComplaint());
                    case 4 -> nhNon.add(cd.getComplaint());
                }
            }
            else {
                complaint.add(getComplaintString(cd));
            }
        }
    }

    StringBuilder sb = new StringBuilder();
    if (!nhRe.isEmpty())  sb.append("No history of ").append(String.join(", ", nhRe)).append(" in RE\n");
    if (!nhLe.isEmpty())  sb.append("No history of ").append(String.join(", ", nhLe)).append(" in LE\n");
    if (!nhBe.isEmpty())  sb.append("No history of ").append(String.join(", ", nhBe)).append(" in BE\n");
    if (!nhNon.isEmpty()) sb.append("No history of ").append(String.join(", ", nhNon)).append("\n");

    if (!sb.toString().isEmpty()) {
        complaint.add(sb.toString());
    }

    // Template
    complaintDetails.stream()
            .filter(c -> c.getTemplateId() != 0)
            .forEach(c -> complaint.add(getComplaintString(c)));

    // Trauma (Ocular)
    complaintDetails.stream()
            .filter(c -> c.getComplaintId() == ComplaintsConstants.OCULAR_TRAUMA_ID)
            .forEach(c -> complaint.add(getComplaintString(c)));

    // FreeText only
    complaintDetails.stream()
            .filter(c -> c.getComplaintId() == 0 && c.getTemplateId() == 0)
            .forEach(c -> complaint.add(getComplaintString(c)));

    return complaint;
}

private String getComplaintString(ComplaintDetail item) {

    if (item.getComplaintId() != 0 && item.getEyePartId() != 0) {

        List<SubComplaint> sub = item.getSubComplaints() != null
                ? item.getSubComplaints()
                : Collections.emptyList();

        String duration = builtDurationFormat(item.getDurationDate());

        if (item.getComplaintId() != ComplaintsConstants.OCULAR_TRAUMA_ID) {
            return builtComplaintString(
                    item.getComplaint(),
                    item.getEyePartId(),
                    item.isNoHistory(),
                    item.isEvent(),
                    duration,
                    sub
            ) + item.getFreeText();

        } else {
            ComplaintTrauma trauma = item.getComplaintTrauma() != null && !item.getComplaintTrauma().isEmpty()
                    ? item.getComplaintTrauma().get(0)
                    : null;

            return builtTraumaString(trauma, item.getEyePartId()) + item.getFreeText();
        }

    } else {
        // Template
        return item.getTemplateId() != 0
                ? builtTemplateString(item.getTemplateName(), item.getTemplateText())
                : "" + item.getFreeText();
    }
}

private Connection dbConnection(boolean oldData) throws SQLException {
    if (oldData) {
        // EMR Connection (SQL Server)
        String url = "jdbc:sqlserver://10.160.15.201:50000;databaseName=MduemrQA20;trustServerCertificate=true";
        String user = "AppUserQAWebEMR";
        String password = "@)@)WebEMRQA2020";
        return DriverManager.getConnection(url, user, password);
    } else {
        // Complaints Connection (Postgres)
        String url = "jdbc:postgresql://172.17.9.53:5432/en20_complaints_dev?ApplicationName=ComplaintConfigurationService";
        String user = "devuser";
        String password = "DevUsr@2022";
        return DriverManager.getConnection(url, user, password);
    }
} //hardcoded conn strings for testing

private List<VwPatientComplaint> fetchComplaints(Connection conn,
                                                 String sql,
                                                 Object[] params) {

    List<VwPatientComplaint> complaints = new ArrayList<>();
    List<VwPatientComplaintReading> readings = new ArrayList<>();
    List<VwPatientComplaintTrauma> traumas  = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        // set parameters
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }

        boolean moreResults = ps.execute();

        int index = 0;
        while (moreResults) {
            try (ResultSet rs = ps.getResultSet()) {

                if (index == 0) {
                    while (rs.next()) {
                        complaints.add(mapVwPatientComplaint(rs));
                    }
                } else if (index == 1) {
                    while (rs.next()) {
                        readings.add(mapVwPatientComplaintReading(rs));
                    }
                } else if (index == 2) {
                    while (rs.next()) {
                        traumas.add(mapVwPatientComplaintTrauma(rs));
                    }
                }

            }
            moreResults = ps.getMoreResults();
            index++;
        }

        // bind relationships - simplified for compilation
        for (VwPatientComplaint c : complaints) {
            long id = c.getId();
            // Note: Type conversion needed for proper entity mapping
            // Simplified for now to fix compilation errors
        }

        complaints.sort(Comparator.comparing(VwPatientComplaint::getId));

        return complaints;

    } catch (Exception ex) {
        log.error("Error fetching discharge summary", ex);
        return Collections.emptyList();
    }
}

public List<VwPatientComplaint> dischargeSummary(String uid,
                                                 LocalDate startDate,
                                                 LocalDate endDate,
                                                 LocalDate complaintsV1LastDate) {

    String sql;
    Object[] params;
    List<VwPatientComplaint> results;

    if (!startDate.isBefore(complaintsV1LastDate)) {
        // Postgres
        sql = ComplaintServiceQueries.FETCH_COMPLAINT_DISCHARGE_SUMMARY;
        params = new Object[]{ uid, startDate, endDate };
        try (Connection conn = dbConnection(false)) {
            results = fetchComplaints(conn, sql, params);
        } catch (SQLException e) {
            log.error("Database connection error", e);
            results = Collections.emptyList();
        }

    } else if (!endDate.isBefore(complaintsV1LastDate)) {
        // Postgres
        sql = ComplaintServiceQueries.FETCH_COMPLAINT_DISCHARGE_SUMMARY;
        params = new Object[]{ uid, complaintsV1LastDate, endDate };
        try (Connection conn = dbConnection(false)) {
            results = fetchComplaints(conn, sql, params);
        } catch (SQLException e) {
            log.error("Database connection error", e);
            results = Collections.emptyList();
        }

        if (results.isEmpty()) {
            // EMR SQL
            sql = ComplaintServiceQueries.FETCH_COMPLAINT_DISCHARGE_V1_SUMMARY;
            params = new Object[]{ uid, startDate, endDate };
            try (Connection conn = dbConnection(true)) {
                results = fetchComplaints(conn, sql, params);
            } catch (SQLException e) {
                log.error("Database connection error", e);
                results = Collections.emptyList();
            }
        }
    } else {
        // EMR SQL
        sql = ComplaintServiceQueries.FETCH_COMPLAINT_DISCHARGE_V1_SUMMARY;
        params = new Object[]{ uid, startDate, endDate };
        try (Connection conn = dbConnection(true)) {
            results = fetchComplaints(conn, sql, params);
        } catch (SQLException e) {
            log.error("Database connection error", e);
            results = Collections.emptyList();
        }
    }

    return results;
}


private static String builtTemplateString(String templateName, String templateText) {
    StringBuilder sb = new StringBuilder();
    sb.append(templateName).append(System.lineSeparator());
    sb.append(templateText);
    return sb.toString();
}

private static String builtDurationFormat(String duration) {

    if (duration == null || duration.isEmpty()) {
        return "Duration";
    }

    String[] parts = duration.split("/");

    int year  = Integer.parseInt(parts[0]);
    int month = Integer.parseInt(parts[1]);
    int week  = Integer.parseInt(parts[2]);
    int day   = Integer.parseInt(parts[3]);

    if (year == 0 && month == 0 && week == 0 && day == 0) {
        return "Duration";
    }

    String yComma = (month > 0 || week > 0 || day > 0) ? ", " : "";
    String mComma = (week  > 0 || day  > 0) ? ", " : "";
    String wComma = (day   > 0)             ? ", " : "";

    String yPart = year  > 0 ? (year  + (year  == 1 ? " year"  : " years")  + yComma) : "";
    String mPart = month > 0 ? (month + (month == 1 ? " month" : " months") + mComma) : "";
    String wPart = week  > 0 ? (week  + (week  == 1 ? " week"  : " weeks")  + wComma) : "";
    String dPart = day   > 0 ? (day   + (day   == 1 ? " day"   : " days"))             : "";

    return yPart + mPart + wPart + dPart;
}

private static String builtComplaintString(String complaint, int eyePartId, boolean isNoHistory, boolean isEvent, String duration, List<SubComplaint> sub) {
    // Implement complaint string building logic
    return complaint != null ? complaint : "";
}

private static String builtTraumaString(ComplaintTrauma trauma, int eyePartId) {
    // Implement trauma string building logic
    return trauma != null ? "Trauma details" : "";
}


    // Add missing mapper methods
    private VwPatientComplaint mapVwPatientComplaint(ResultSet rs) throws SQLException {
        VwPatientComplaint complaint = new VwPatientComplaint();
        // Add mapping logic as needed
        return complaint;
    }
    
    private VwPatientComplaintReading mapVwPatientComplaintReading(ResultSet rs) throws SQLException {
        VwPatientComplaintReading reading = new VwPatientComplaintReading();
        // Add mapping logic as needed
        return reading;
    }
    
    private VwPatientComplaintTrauma mapVwPatientComplaintTrauma(ResultSet rs) throws SQLException {
        VwPatientComplaintTrauma trauma = new VwPatientComplaintTrauma();
        // Add mapping logic as needed
        return trauma;
    }

    public ComplaintViewModel dischargeSummaryList(String uid,
                                               int eye,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               LocalDate complaintsV1LastDate) {

    List<VwPatientComplaint> results = dischargeSummary(uid, startDate, endDate, complaintsV1LastDate);

    if (results == null || results.isEmpty()) {
        ComplaintViewModel empty = new ComplaintViewModel();
        empty.setId(0L);
        return empty;
    }

    // get the last peId
    long peId = results.stream()
                       .sorted(Comparator.comparing(VwPatientComplaint::getPeId))
                       .map(VwPatientComplaint::getPeId)
                       .distinct()
                       .reduce((first, second) -> second)
                       .orElse(0L);

    // filter by that peId
    List<VwPatientComplaint> vwComplaint =
            results.stream()
                   .filter(r -> r.getPeId() == peId)
                   .collect(Collectors.toList());

    if (vwComplaint.isEmpty()) {
        ComplaintViewModel empty = new ComplaintViewModel();
        empty.setId(0L);
        return empty;
    }

    // filter by eye
    List<VwPatientComplaint> groupEye = vwComplaint.stream()
            .filter(v -> (eye == 1 && (v.getEyePartId() == 1 || v.getEyePartId() == 3)) ||
                         (eye == 2 && (v.getEyePartId() == 2 || v.getEyePartId() == 3)) ||
                         (eye == 3 && (v.getEyePartId() == 1 || v.getEyePartId() == 2 || v.getEyePartId() == 3)))
            .collect(Collectors.toList());

    if (groupEye.isEmpty()) {
        ComplaintViewModel empty = new ComplaintViewModel();
        empty.setId(0L);
        return empty;
    }
    List<PatientComplaint> complaints = mapToPatientComplaints(groupEye);

    return dataModelToViewModel(0L, complaints, false);
    // return dataModelToViewModel(0L, complaints, false);
}

public List<ComplaintViewModel> medicalSummaryList(Peid peIds, long complaintsV1LastPeId) {

    List<ComplaintViewModel> list = new ArrayList<>();

    for (Long peId : peIds.getPeIds()) {

        // get view by peId without visitDate
        List<VwPatientComplaint> results = getViewByPeId(peId, complaintsV1LastPeId, null);

        if (results != null && !results.isEmpty()) {
            List<PatientComplaint> entities = mapToPatientComplaints(results);

            ComplaintViewModel viewModel = dataModelToViewModel(0, entities, false);
            list.add(viewModel);
        }
    }

    return list;
}

private List<VwPatientComplaint> getViewByPeId(long peId,
                                              long complaintsV1LastPeId,
                                              LocalDate visitDate) {

    boolean isOldData;
    String sql;
    Object[] params;

    if (peId <= complaintsV1LastPeId) {
        // V1
        if (visitDate != null) {
            sql = "uspGetComplaintSummaryWithDate";   // hard-coded V1 query
            params = new Object[]{ peId, visitDate };
        } else {
            sql = "uspGetComplaintSummary";           // hard-coded V1 query
            params = new Object[]{ peId };
        }
        isOldData = true;
    } else {
        // V2
        if (visitDate != null) {
            // HARD-CODED V2 query (FETCH_COMPLAINT_SUMMARY_WITH_DATE)
            sql =
                """
                SELECT
                    pc.id,
                    pc.is_active,
                    pc.is_deleted,
                    pc.created_by,
                    pc.created_at,
                    pc.created_ip,
                    pc.updated_by,
                    pc.updated_at,
                    pc.updated_ip,
                    pc.deleted_by,
                    pc.deleted_at,
                    pc.deleted_ip,
                    pc.pe_id,
                    pc.uid,
                    pc.created_dept_id,
                    pc.updated_dept_id,
                    pc.entered_date,
                    pc.complaint_id,
                    pc.duration_date,
                    pc.eye_part_id,
                    pc.free_text,
                    pc.template_id,
                    pc.template_text,
                    pc.is_no_history,
                    pc.visit_purpose,
                    pc.informant,
                    pc.informant_others,
                    pc.visit_purpose_others,
                    pc.duration
                FROM public.patient_complaint pc
                WHERE pc.pe_id = ? AND pc.entered_date = ?;

                SELECT pcr.id, pcr.is_active, pcr.patient_complaint_id, 
                       pcr.complaint_subcomplaint_id, pcr.complaint_reading_id
                FROM public.patient_complaint pc
                INNER JOIN public.patient_complaint_readings pcr ON pc.id = pcr.patient_complaint_id
                WHERE pc.pe_id = ? AND pc.entered_date = ?;

                SELECT pct.id, pct.is_active, pct.patient_complaint_id,
                       pct.nature_of_trauma, pct.trauma_date, pct.trauma_time,
                       pct.place_of_trauma, pct.is_prior_treatment, pct.prior_treatment_detail,
                       pct.trauma_remarks, pct.attender_detail, pct.is_mlc_case,
                       pct.identification_marks
                FROM public.patient_complaint pc
                INNER JOIN public.patient_complaint_trauma pct ON pc.id = pct.patient_complaint_id
                WHERE pc.pe_id = ? AND pc.entered_date = ?;
                """;

            params = new Object[]{ peId, visitDate, peId, visitDate, peId, visitDate };

        } else {
            // FETCH_COMPLAINT_SUMMARY  (hard-coded base query)
            sql =
                """
                SELECT
                    pc.id,
                    pc.is_active,
                    pc.is_deleted,
                    pc.created_by,
                    pc.created_at,
                    pc.created_ip,
                    pc.updated_by,
                    pc.updated_at,
                    pc.updated_ip,
                    pc.deleted_by,
                    pc.deleted_at,
                    pc.deleted_ip,
                    pc.pe_id,
                    pc.uid,
                    pc.created_dept_id,
                    pc.updated_dept_id,
                    pc.entered_date,
                    pc.complaint_id,
                    pc.duration_date,
                    pc.eye_part_id,
                    pc.free_text,
                    pc.template_id,
                    pc.template_text,
                    pc.is_no_history,
                    pc.visit_purpose,
                    pc.informant,
                    pc.informant_others,
                    pc.visit_purpose_others,
                    pc.duration
                FROM public.patient_complaint pc
                WHERE pc.pe_id = ?;

                SELECT pcr.id, pcr.is_active, pcr.patient_complaint_id,
                       pcr.complaint_subcomplaint_id, pcr.complaint_reading_id
                FROM public.patient_complaint pc
                INNER JOIN public.patient_complaint_readings pcr ON pc.id = pcr.patient_complaint_id
                WHERE pc.pe_id = ?;

                SELECT pct.id, pct.is_active, pct.patient_complaint_id,
                       pct.nature_of_trauma, pct.trauma_date, pct.trauma_time,
                       pct.place_of_trauma, pct.is_prior_treatment, pct.prior_treatment_detail,
                       pct.trauma_remarks, pct.attender_detail, pct.is_mlc_case,
                       pct.identification_marks
                FROM public.patient_complaint pc
                INNER JOIN public.patient_complaint_trauma pct ON pc.id = pct.patient_complaint_id
                WHERE pc.pe_id = ?;
                """;

            params = new Object[]{ peId, peId, peId };
        }

        isOldData = false;
    }

    try (Connection conn = dbConnection(isOldData)) {
        return fetchComplaints(conn, sql, params);
    } catch (SQLException e) {
        log.error("Database connection error", e);
        return Collections.emptyList();
    }
}

public List<ComplaintSummary> medicalSummaryListV2(ComplaintMasterSet masterData,
                                                   Peid peIds,
                                                   long complaintsV1LastPeId) {

    List<ComplaintViewModel> complaintViewModels = new ArrayList<>();

    // fetch view for each peId
    for (Long peId : peIds.getPeIds()) {
        List<VwPatientComplaint> results =
                getViewByPeId(peId, complaintsV1LastPeId, null);

        if (results != null && !results.isEmpty()) {
            List<PatientComplaint> entities = mapToPatientComplaints(results);

            ComplaintViewModel view = dataModelToViewModel(0L, entities, false);
            complaintViewModels.add(view);
        }
    }

    // enrich
    if (!complaintViewModels.isEmpty() && masterData != null) {
        for (ComplaintViewModel item : complaintViewModels) {

            if (item.getComplaintVisitPurpose() != null &&
                item.getComplaintVisitPurpose().getVisitPurpose() != null) {

                String vpText = getVisitPurposeText(
                        masterData.getVisitPurposes(),
                        item.getComplaintVisitPurpose().getVisitPurpose());

                item.getComplaintVisitPurpose().setVisitPurposeText(vpText);
            }

            List<ComplaintDetail> details =
                    item.getComplaint() != null ?
                    item.getComplaint().getComplaintDetails() : null;

            if (details != null) {
                mapComplaintsName(details,
                        masterData.getComplaintMasterData(),
                        masterData.getComplaintReadings(),
                        masterData.getSubcomplaints());

                mapIsEvent(details, masterData.getComplaintMasterData());
                mapTemplateName(details, masterData.getComplaintTemplates());
            }
        }
    }

    return buildComplaintSummary(complaintViewModels);
}

private List<ComplaintSummary> buildComplaintSummary(List<ComplaintViewModel> complaintViewModels) {
    List<ComplaintSummary> summaries = new ArrayList<>();
    
    for (ComplaintViewModel vm : complaintViewModels) {
        ComplaintSummary summary = new ComplaintSummary();
        summary.setPeid(vm.getPeId());
        
        if (vm.getComplaint() != null) {
            summary.setInformant(vm.getComplaint().getComplaintInformant());
            
            if (vm.getComplaint().getComplaintDetails() != null) {
                List<String> complaints = complaintDisplayFormat(vm.getComplaint().getComplaintDetails());
                summary.setComplaints(complaints);
            }
        }
        
        if (vm.getComplaintVisitPurpose() != null) {
            summary.setVisitPurpose(vm.getComplaintVisitPurpose().getVisitPurposeText());
        }
        
        summaries.add(summary);
    }
    
    return summaries;
}

private String getVisitPurposeText(List<VisitPurposeDto> masters, String key) {
    return masters.stream()
            .filter(v -> v.getKey().equals(key))
            .map(VisitPurposeDto::getValue)
            .findFirst()
            .orElse("");
}

private void mapComplaintsName(List<ComplaintDetail> details,
                               List<ComplaintMasterData> masterData,
                               List<ComplaintReadingDto> readingMaster,
                               List<ComplaintSubcomplaintDto> complaintSubcomplaints) {

    for (ComplaintDetail d : details) {
        d.setComplaint(getComplaintText(masterData, d.getComplaintId()));

        if (d.getSubComplaints() != null) {
            for (SubComplaint sc : d.getSubComplaints()) {
                if (sc.getComplaintSubcomplaintId() == null && sc.getComplaintReadingId() != 0) {
                    ComplaintReadingDto rd = readingMaster.stream()
                            .filter(r -> r.getId() == sc.getComplaintReadingId())
                            .findFirst().orElse(null);

                    if (rd != null) {
                        Long csid = complaintSubcomplaints.stream()
                                .filter(x -> x.getId() == rd.getComplaintSubcomplaintId())
                                .map(ComplaintSubcomplaintDto::getSubcomplaintId)
                                .findFirst()
                                .orElse(null);

                        sc.setResult(getResultText(masterData,
                                d.getComplaintId(),
                                csid,
                                rd.getResultId()));
                    }
                } else {
                    sc.setResult(getResultText(masterData,
                            d.getComplaintId(),
                            sc.getComplaintSubcomplaintId(),
                            sc.getComplaintReadingId()));
                }
            }
        }
    }
}

private void mapIsEvent(List<ComplaintDetail> details,
                        List<ComplaintMasterData> masterData) {

    for (ComplaintDetail d : details) {
        if (d.getComplaintId() != null && d.getComplaintId() != 0) {
            d.setEvent(getIsEvent(masterData, d.getComplaintId()));
        }
    }
}

private void mapTemplateName(List<ComplaintDetail> details,
                             List<ComplaintTemplateDto> templates) {

    for (ComplaintDetail d : details) {
        if (d.getTemplateId() > 0) {
            d.setTemplateName(getTemplateName(templates, d.getTemplateId()));
        }
    }
}

private String getTemplateName(List<ComplaintTemplateDto> templates, int templateId) {
    return templates.stream()
            .filter(t -> t.getId() == templateId)
            .map(ComplaintTemplateDto::getTemplateName)
            .findFirst()
            .orElse("");
}

private boolean getIsEvent(List<ComplaintMasterData> masterData, Long id) {
    return masterData.stream()
            .filter(x -> x.getId() == id)
            .anyMatch(ComplaintMasterData::isEvent);
}

private String getComplaintText(List<ComplaintMasterData> masterData, Long id) {
    return masterData.stream()
                     .filter(m -> m.getId() == id)
                     .map(ComplaintMasterData::getComplaintText)
                     .findFirst()
                     .orElse("");
}

private String getResultText(List<ComplaintMasterData> masterData,
                             Long complaintId,
                             Long scId,
                             Long rId) {

    return masterData.stream()
            .filter(x -> x.getId() == complaintId)
            .flatMap(x -> x.getSubcomplaints().stream())
            .filter(sub -> Objects.equals(sub.getId(), scId))
            .flatMap(sub -> sub.getResults().stream())
            .filter(r -> r.getId() == rId)
            .map(ComplaintResultData::getResultText)
            .findFirst()
            .orElse("");
}

public ComplaintInfo update(String uId,
                            long peId,
                            int departmentId,
                            int updatedBy,
                            String clientIp,
                            ComplaintViewModel viewModel) {

    // TODO: Implement proper update functionality when repository and entity methods are available
    try {
        ComplaintInfo info = new ComplaintInfo();
        info.setIsOcularTrauma(isAddedOcularTrauma(viewModel.getComplaint().getComplaintDetails()));
        info.setIsRedness(isAddedRedness(viewModel.getComplaint().getComplaintDetails()));
        return info;
    } catch (Exception ex) {
        ComplaintInfo info = new ComplaintInfo();
        info.setError(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
        return info;
    }
}

public int updateUinMergeComplaints(String oldUid, String newUid) {

    final String sql = """
        UPDATE patient_complaint
           SET uid = ?
         WHERE uid = ?
        """;

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, newUid);
        ps.setString(2, oldUid);

        return ps.executeUpdate();

    } catch (SQLException ex) {
        log.error("Error merging UIDs in patient_complaint table", ex);
        throw new RuntimeException(ex.getMessage(), ex);
    }
}

}

