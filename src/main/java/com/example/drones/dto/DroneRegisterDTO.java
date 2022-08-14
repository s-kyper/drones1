package com.example.drones.dto;

import com.example.drones.enums.DroneModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DroneRegisterDTO {
    private String serialNumber;
    private DroneModel droneModel;
    private int batteryCapacity;
    private int weightLimit;
}
