package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response chung cho API operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
    
    @JsonProperty("timestamp")
    private Long timestamp = System.currentTimeMillis();
    
    // Constructor cho success response
    public ApiResponse(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Constructor cho response không có data
    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.timestamp = System.currentTimeMillis();
    }
}
