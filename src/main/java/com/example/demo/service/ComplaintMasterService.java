package com.example.demo.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.demo.ViewModel.Complaint;
import com.example.demo.ViewModel.SubComplaint;
import com.example.demo.dto.ComplaintMasterData;
import com.example.demo.dto.ComplaintReadingDto;
import com.example.demo.dto.ComplaintSubcomplaintDto;
import com.example.demo.Entity.Master.*;

public class ComplaintMasterService {

    public CompletableFuture<List<ComplaintMasterData>> getComplaintMaster() {

    return CompletableFuture.supplyAsync(() -> {

        String finalQuery = ComplaintServiceQueries.GET_COMPLAINT_MASTER;

        if (finalQuery == null || finalQuery.isEmpty()) {
            return new ArrayList<>();
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
                complaints.add(mapComplaint(rs));
            }

            // ResultSet 2 → subcomplaints
            if (ps.getMoreResults()) {
                ResultSet rs2 = ps.getResultSet();
                while (rs2.next()) {
                    subcomplaints.add(mapSubComplaint(rs2));
                }
            }

            // ResultSet 3 → complaintSubcomplaints
            if (ps.getMoreResults()) {
                ResultSet rs3 = ps.getResultSet();
                while (rs3.next()) {
                    compSub.add(mapComplaintSubcomplaint(rs3));
                }
            }

            // ResultSet 4 → results
            if (ps.getMoreResults()) {
                ResultSet rs4 = ps.getResultSet();
                while (rs4.next()) {
                    results.add(mapComplaintResult(rs4));
                }
            }

            // ResultSet 5 → readings
            if (ps.getMoreResults()) {
                ResultSet rs5 = ps.getResultSet();
                while (rs5.next()) {
                    readings.add(mapComplaintReading(rs5));
                }
            }

            // build final data
            return complaints.stream()
                    .map(c -> {
                        ComplaintMasterData data = new ComplaintMasterData();
                        data.setId(c.getId());
                        data.setIsOcular(c.isOcular());
                        data.setComplaintText(c.getComplaintText());
                        data.setIsActive(c.isActive());
                        data.setIsEvent(c.isEvent());
                        data.setSubcomplaints(
                                subcomplaintsData(
                                        compSub,
                                        subcomplaints,
                                        readings,
                                        results,
                                        c.getId()));
                        data.setComplaintAssociateId(null);
                        data.setDepartmentId(null);
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
    
}
