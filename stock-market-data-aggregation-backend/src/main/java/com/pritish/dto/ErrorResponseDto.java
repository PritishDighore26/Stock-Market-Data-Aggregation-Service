package com.pritish.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponseDto {

    private int status;
    private String message;
    private String timestamp;

    public ErrorResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public int getStatus()      { return status; }
    public String getMessage()  { return message; }
    public String getTimestamp(){ return timestamp; }

    public void setStatus(int status)         { this.status = status; }
    public void setMessage(String message)    { this.message = message; }
    public void setTimestamp(String timestamp){ this.timestamp = timestamp; }
}
