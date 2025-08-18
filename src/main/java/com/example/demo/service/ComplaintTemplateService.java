package com.example.demo.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.ComplaintTemplate;
import com.example.demo.dto.ComplaintTemplateDto;
import com.example.demo.repository.ComplaintTemplateRepository;

@Service
public class ComplaintTemplateService {
    
    private static final Logger log = LoggerFactory.getLogger(ComplaintTemplateService.class);
    
private final ComplaintTemplateRepository complaintTemplateRepository;

    @Autowired
    public ComplaintTemplateService(ComplaintTemplateRepository complaintTemplateRepository) {
        this.complaintTemplateRepository = complaintTemplateRepository;
    }

    public CompletableFuture<List<ComplaintTemplateDto>> getMaster() {

    return CompletableFuture.supplyAsync(() -> {
        try {
            // get all entities from repository
            List<ComplaintTemplate> entities = complaintTemplateRepository.findAll();

            // map to DTO
            return entities.stream()
                    .map(entity -> {
                        ComplaintTemplateDto dto = new ComplaintTemplateDto();
                        dto.setId(entity.getId());
                        dto.setTemplateName(entity.getTemplateName());
                        dto.setTemplateText(entity.getTemplateText());
                        dto.setDeptId(entity.getDeptId());
                        dto.setSiteId(entity.getSiteId());
                        dto.setIsActive(entity.getIsActive());
                        return dto;
                    })
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error getting complaint template master", ex);
            return Collections.emptyList();
        }
    });
}

    
}
