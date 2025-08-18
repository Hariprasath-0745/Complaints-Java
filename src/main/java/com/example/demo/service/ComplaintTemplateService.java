package com.example.demo.service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.demo.dto.ComplaintTemplateDto;

public class ComplaintTemplateService {

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
