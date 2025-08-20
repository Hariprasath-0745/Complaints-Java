package com.example.demo.controller;

import com.example.demo.dto.ComplaintOcularFieldDto;
import com.example.demo.service.ComplaintOcularFieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/complaintocularfield")
public class ComplaintOcularFieldController {
    
    private static final Logger _logger = LoggerFactory.getLogger(ComplaintOcularFieldController.class);
    
    private final ComplaintOcularFieldService _service;
    
    public ComplaintOcularFieldController(ComplaintOcularFieldService service) {
        this._service = service;
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
