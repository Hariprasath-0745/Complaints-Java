package com.example.demo.service;

import com.example.demo.dto.ComplaintOcularFieldDto;
import com.example.demo.Entity.Master.*;
import com.example.demo.repository.ComplaintOcularFieldRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
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