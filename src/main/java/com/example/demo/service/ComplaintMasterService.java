package com.example.demo.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.demo.ViewModel.Complaint;
import com.example.demo.ViewModel.SubComplaint;
import com.example.demo.dto.ComplaintMasterData;
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

    
}
