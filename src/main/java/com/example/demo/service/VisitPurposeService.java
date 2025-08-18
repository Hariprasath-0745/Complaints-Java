package com.example.demo.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.demo.dto.VisitPurposeDto;

public class VisitPurposeService {

    public CompletableFuture<List<VisitPurposeDto>> getMaster() {

    return CompletableFuture.supplyAsync(() -> {
        String sql = ComplaintServiceQueries.GET_VISITPURPOSE_MASTER;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<VisitPurpose> entities = new ArrayList<>();
            while (rs.next()) {
                VisitPurpose vp = new VisitPurpose();
                vp.setId(rs.getLong("Id"));
                vp.setKey(rs.getString("Key"));
                vp.setValue(rs.getString("Value"));
                vp.setSequence(rs.getInt("Sequence"));
                vp.setIsActive(rs.getBoolean("IsActive"));
                vp.setIcon(rs.getString("Icon"));
                vp.setIsSelected(rs.getBoolean("IsSelected"));
                vp.setIsGrouped(rs.getBoolean("IsGrouped"));
                entities.add(vp);
            }

            // Map to DTO list
            return entities.stream()
                    .map(entity -> {
                        VisitPurposeDto dto = new VisitPurposeDto();
                        dto.setId(entity.getId());
                        dto.setKey(entity.getKey());
                        dto.setValue(entity.getValue());
                        dto.setIsActive(entity.getIsActive());
                        dto.setIsSelected(entity.isIsSelected());
                        dto.setIsGrouped(entity.isIsGrouped());
                        dto.setSequence(entity.getSequence());
                        dto.setIcon(entity.getIcon());
                        return dto;
                    })
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error getting visit purpose master", ex);
            return Collections.emptyList();
        }
    });
}

    
}
