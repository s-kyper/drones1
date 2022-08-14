package com.example.drones.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AbstractBaseDTO {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
