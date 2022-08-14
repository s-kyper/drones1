package com.example.drones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationDTO extends AbstractBaseDTO {
    private Long id;
    private String name;
    private int weight;
    private String code;
    private byte[] image;
    private Long droneId;
}
