package com.example.demo.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.VisitPurpose;
import com.example.demo.dto.VisitPurposeDto;

@Service
public class VisitPurposeService {
    
    private static final Logger log = LoggerFactory.getLogger(VisitPurposeService.class);
    
    @Autowired
    private DataSource dataSource;
    
    private static class ComplaintServiceQueries {
        public static final String GET_VISITPURPOSE_MASTER = "SELECT * FROM visit_purposes WHERE is_active = true";
    }

    public CompletableFuture<List<VisitPurposeDto>> getMaster() {

    return CompletableFuture.supplyAsync(() -> {
        String sql = ComplaintServiceQueries.GET_VISITPURPOSE_MASTER;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<VisitPurpose> entities = new ArrayList<>();
            while (rs.next()) {
                VisitPurpose vp = new VisitPurpose();
                vp.setId(rs.getLong("id"));
                vp.setKey(rs.getString("key"));
                vp.setValue(rs.getString("value"));
                vp.setSequence(rs.getInt("sequence"));
                vp.setIsActive(rs.getBoolean("is_active"));
                vp.setIcon(rs.getString("icon"));
                vp.setIsSelected(rs.getBoolean("is_selected"));
                vp.setIsGrouped(rs.getBoolean("is_grouped"));
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
                        dto.setIsSelected(entity.getIsSelected());
                        dto.setIsGrouped(entity.getIsGrouped());
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
