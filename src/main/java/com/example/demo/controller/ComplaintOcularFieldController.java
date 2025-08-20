package com.example.demo.controller;

import com.example.demo.dto.ComplaintOcularFieldDto;
import com.example.demo.service.ComplaintOcularFieldService;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Helper.CommonHelper;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaintocularfield")
public class ComplaintOcularFieldController {
    
    private static final Logger _logger = LoggerFactory.getLogger(ComplaintOcularFieldController.class);
    private final CommonHelper _commonHelper;
    private final ComplaintOcularFieldService _service;
    
    
   public ComplaintOcularFieldController(ComplaintOcularFieldService service, CommonHelper commonHelper) {
        this._service = service;
        this._commonHelper = commonHelper;
    }
    
    @GetMapping("/CommonMaster")
    public ResponseEntity<?> commonMaster() {
        try {
            List<ComplaintOcularFieldDto> data = _service.read();
            return ResponseEntity.ok(data);
        } catch (BadRequestException brex) {
            _logger.warn(brex.getMessage(), brex);
            return ResponseEntity.badRequest().body(new ErrorResponse(brex.getMessage()));
        } catch (NotFoundException nfx) {
            _logger.warn(nfx.getMessage(), nfx);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(nfx.getSource(), nfx.getMessage()));
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/{createdBy}")
    public ResponseEntity<?> create(@PathVariable int createdBy,
                                   @RequestBody ComplaintOcularFieldDto dto,
                                   HttpServletRequest request) {
        try {
            String myIP = _commonHelper.getClientIp(request);
            if (myIP == null) {
                myIP = "";
            }
            
            ComplaintOcularFieldDto response = _service.create(createdBy, myIP, dto);
            
            // Remove from cache (assuming you have cache service)
            // _cache.removeAsync(Table.COMPLAINTS_MASTER, "complaintOcularFieldItems");
            
            return ResponseEntity.ok(response);
            
        } catch (BadRequestException brex) {
            _logger.warn(brex.getMessage(), brex);
            return ResponseEntity.badRequest().body(new ErrorResponse(brex.getMessage()));
        } catch (NotFoundException nfx) {
            _logger.warn(nfx.getMessage(), nfx);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(nfx.getSource(), nfx.getMessage()));
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }
     @PutMapping("/{id}/{updatedBy}")
    public ResponseEntity<?> update(@PathVariable UUID id, 
                                   @PathVariable int updatedBy,
                                   @RequestBody ComplaintOcularFieldDto dto,
                                   HttpServletRequest request) {
        try {
             String myIP = _commonHelper.getClientIp(request);
            if (myIP == null) {
                myIP = "";
            }
            
            ComplaintOcularFieldDto response = _service.update(id, updatedBy, myIP, dto);
            
            // Remove from cache (assuming you have cache service)
            // _cache.removeAsync(Table.COMPLAINTS_MASTER, "complaintOcularFieldItems");
            
            return ResponseEntity.ok(response);
            
        } catch (BadRequestException brex) {
            _logger.warn(brex.getMessage(), brex);
            return ResponseEntity.badRequest().body(new ErrorResponse(brex.getMessage()));
        } catch (NotFoundException nfx) {
            _logger.warn(nfx.getMessage(), nfx);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(nfx.getSource(), nfx.getMessage()));
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }
    // Exception classes (you might already have these)
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
    
    public static class NotFoundException extends RuntimeException {
        private String source;
        
        public NotFoundException(String message) {
            super(message);
        }
        
        public NotFoundException(String source, String message) {
            super(message);
            this.source = source;
        }
        
        public String getSource() {
            return source;
        }
    }
    
    public static class ErrorResponse {
        private String source;
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public ErrorResponse(String source, String message) {
            this.source = source;
            this.message = message;
        }
        
        // Getters and setters
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
