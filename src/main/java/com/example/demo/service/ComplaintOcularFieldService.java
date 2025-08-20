package com.example.demo.service;

import com.example.demo.dto.ComplaintOcularFieldDto;
import com.example.demo.Entity.Master.*;
import com.example.demo.controller.ComplaintOcularFieldController.NotFoundException;
import com.example.demo.repository.ComplaintOcularFieldRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ComplaintOcularFieldService {
    
    private static final Logger logger = LoggerFactory.getLogger(ComplaintOcularFieldService.class);
    
    private final ComplaintOcularFieldRepository complaintOcularFieldRepository;
    private final ModelMapper modelMapper;
    
    public ComplaintOcularFieldService(ComplaintOcularFieldRepository complaintOcularFieldRepository, 
                                     ModelMapper modelMapper) {
        this.complaintOcularFieldRepository = complaintOcularFieldRepository;
        this.modelMapper = modelMapper;
    }
    
    public List<ComplaintOcularFieldDto> read() {
        try {
            logger.info("Fetching all ComplaintOcularFields with their items");
            
            // Fetch all active ComplaintOcularFields with their items
            List<ComplaintOcularField> result = complaintOcularFieldRepository.findAllWithItems();
            
            // Map entities to DTOs
            Type listType = new TypeToken<List<ComplaintOcularFieldDto>>() {}.getType();
            List<ComplaintOcularFieldDto> dtoList = modelMapper.map(result, listType);
            
            logger.info("Successfully fetched {} ComplaintOcularFields", dtoList.size());
            return dtoList;
            
        } catch (Exception ex) {
            logger.error("Error fetching ComplaintOcularFields: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch ComplaintOcularFields", ex);
        }
    }
    
    public ComplaintOcularFieldDto create(int createdBy, String myIP, ComplaintOcularFieldDto dto) {
        try {
            logger.info("Creating new ComplaintOcularField for user: {}", createdBy);
            
            // Map DTO to entity with additional context
            ComplaintOcularField entity = modelMapper.map(dto, ComplaintOcularField.class);
            
            // Set audit fields manually since ModelMapper doesn't support context items like C#
            entity.setCreatedBy(createdBy);
            entity.setCreatedAt(OffsetDateTime.now());
            entity.setCreatedIp(myIP != null ? myIP : "");
            entity.setIsActive(true);
            entity.setDeleted(false);
            
            // Set createdDeptId if you have this information
            // entity.setCreatedDeptId(deptId);
            
            // Save the entity
            ComplaintOcularField savedEntity = complaintOcularFieldRepository.save(entity);
            
            logger.info("Successfully created ComplaintOcularField with ID: {}", savedEntity.getId());
            
            // Map back to DTO
            return modelMapper.map(savedEntity, ComplaintOcularFieldDto.class);
            
        } catch (Exception ex) {
            logger.error("Error creating ComplaintOcularField: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to create ComplaintOcularField", ex);
        }
    }
    public ComplaintOcularFieldDto update(UUID id, int updatedBy, String myIP, ComplaintOcularFieldDto dto) {
        try {
            logger.info("Updating ComplaintOcularField with ID: {} by user: {}", id, updatedBy);
            
            // Find existing entity
            Optional<ComplaintOcularField> existingDataOpt = complaintOcularFieldRepository.findById(id);
            
            if (existingDataOpt.isEmpty()) {
                throw new NotFoundException("ComplaintOcularField not found with ID: " + id);
            }
            
            ComplaintOcularField existingData = existingDataOpt.get();
            
            // Update audit fields
            existingData.setUpdatedIp(myIP != null ? myIP : "");
            existingData.setUpdatedBy(updatedBy);
            existingData.setUpdatedAt(OffsetDateTime.now());
            
            // Update basic fields
            existingData.setFieldName(dto.getFieldName());
            existingData.setFieldCode(dto.getFieldCode());
            existingData.setSequence(dto.getSequence());
            existingData.setIsActive(dto.getIsActive());
            
            // Update nested items if provided
            if (dto.getComplaintOcularFieldItems() != null) {
                List<ComplaintOcularFieldItem> items = dto.getComplaintOcularFieldItems().stream()
                        .map(itemDto -> {
                            ComplaintOcularFieldItem item = modelMapper.map(itemDto, ComplaintOcularFieldItem.class);
                            item.setComplaintOcularFieldId(id); // Set the foreign key
                            return item;
                        })
                        .toList();
                existingData.setComplaintOcularFieldItems(items);
            }
            
            // Save the updated entity
            ComplaintOcularField updatedEntity = complaintOcularFieldRepository.save(existingData);
            
            logger.info("Successfully updated ComplaintOcularField with ID: {}", id);
            
            // Return the original DTO or map back from entity
            return dto;
            
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating ComplaintOcularField: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to update ComplaintOcularField", ex);
        }
    }
  
    // Additional methods if needed
    @Transactional
    public ComplaintOcularFieldDto create(ComplaintOcularFieldDto dto) {
        // Implementation for create
        return null;
    }
    
    @Transactional
    public ComplaintOcularFieldDto update(UUID id, ComplaintOcularFieldDto dto) {
        // Implementation for update
        return null;
    }
    
    @Transactional
    public void delete(UUID id) {
        // Implementation for delete
    }
    
    public ComplaintOcularFieldDto findById(UUID id) {
        // Implementation for find by id
        return null;
    }
}